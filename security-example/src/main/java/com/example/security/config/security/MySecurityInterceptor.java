package com.example.security.config.security;

import com.example.security.utils.RedisUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class MySecurityInterceptor extends AbstractSecurityInterceptor implements Filter {

	/**
	 * 该字段将作为Key保存到请求中，如果对应的值为True表明当前Url已被别的过滤器处理过
	 */
	private static final String FILTER_APPLIED = "__spring_security_filterSecurityInterceptor_filterApplied";

	/**
	 * 该字段表明当前过滤器是否要对其它过滤器已经处理过的Url再次进行过滤
	 * true表示不需要再次过滤，false表示需要再次过滤
	 * <p>
	 * 请求中的FILTER_APPLIED属性表明Url是否被别的处理器处理过
	 */
	private boolean observeOncePerRequest = false;

	/**
	 * 过期时间，单位：分钟
	 * 默认30分钟过期
	 */
	private long expireTime = 30L;

	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * 装配自定义的FilterInvocationSecurityMetadataSource，用于加载Url及其对应的权限信息
	 */
	@Autowired
	private FilterInvocationSecurityMetadataSource securityMetadataSource;

	@Override
	public Class<?> getSecureObjectClass() {
		return FilterInvocation.class;
	}

	/**
	 * 注入自定义的访问决策管理器（AccessDecisionManager），用于判断当前用户是否有权访问当前Url
	 *
	 * @param accessDecisionManager
	 */
	@Autowired
	@Override
	public void setAccessDecisionManager(AccessDecisionManager accessDecisionManager) {
		super.setAccessDecisionManager(accessDecisionManager);
	}

	@Override
	public SecurityMetadataSource obtainSecurityMetadataSource() {
		return this.securityMetadataSource;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

		verifyCookie(servletRequest, servletResponse);
		/**
		 * FilterInvocation 封装了请求信息和响应信息以及过滤链对象
		 */
		FilterInvocation fi = new FilterInvocation(servletRequest, servletResponse, filterChain);
		invoke(fi);
	}

	public void invoke(FilterInvocation fi) throws IOException, ServletException {
		/**
		 * 如果当前Url已被别的过滤器处理过且observeOncePerRequest的值为true，直接放行交给下一个处理器进行处理
		 */
		if ((fi.getRequest() != null)
			&& (fi.getRequest().getAttribute(FILTER_APPLIED) != null)
			&& observeOncePerRequest) {
			fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
		} else {
			/**
			 * 如果observeOncePerRequest的值为true，将FILTER_APPLIED写入请求中，表明当前Url已被处理过
			 */
			if (fi.getRequest() != null && observeOncePerRequest) {
				fi.getRequest().setAttribute(FILTER_APPLIED, Boolean.TRUE);
			}
			/**
			 * 执行权限校验逻辑
			 */
			InterceptorStatusToken token = super.beforeInvocation(fi);

			/**
			 * 执行完毕交给下个过滤器进行处理
			 */
			try {
				fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
			} finally {
				super.finallyInvocation(token);
			}
			super.afterInvocation(token, null);
		}
	}

	private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

	/**
	 * 从cookie中获取sessionId，如果存在sessionId，根据sessionId从Redis获取token
	 * 如果token存在，则表示该用户已在别的服务中登录过，随后对token进行校验，如果token未过期，将token反序列化为Authentication，
	 *
	 * 否则将当前用户设置为匿名
	 * 注意：redis中如果不存在用户的sessionId，用户可能已在异地登录，当前用户会被挤掉
	 *
	 * @param servletRequest
	 */
	private void verifyCookie(ServletRequest servletRequest, ServletResponse servletResponse) {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length < 2) {
			return;
		}
		/**
		 * 获取sessionId
		 */
		Cookie sessionIdCookie = null;
		Cookie lastTimeCookie = null;

		for (Cookie cookie : cookies){
			if (cookie.getName().equals("sessionId")){
				sessionIdCookie = cookie;
			}
			if (cookie.getName().equals("lastTime")){
				lastTimeCookie = cookie;
			}
		}

		if (sessionIdCookie == null || lastTimeCookie == null){
			return;
		}
		/**
		 * 检查Redis中是否存在sessionId
		 */
		boolean exist = RedisUtils.checkToken(sessionIdCookie.getValue());
		/**
		 * 如果不存在将当前用户设置为匿名用户
		 */
		if (!exist) {
			Authentication newAuthentication = new AnonymousAuthenticationToken("key", "anonymous",
				AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
			SecurityContextHolder.getContext().setAuthentication(newAuthentication);
			return;
		}


		/**
		 * 如果不是匿名用户说明当前用户在本服务第一次登录，此时已存在Authentication信息，直接返回
		 */
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!trustResolver.isAnonymous(authentication)) {
			deleteTokenIfExpire(lastTimeCookie, sessionIdCookie, authentication.getName());
			return;
		}
		/**
		 * 如果是匿名用户说明当前用户在别的服务登录，需要在本服务构造一个Authentication对象作为Token填充到SecurityContext中
		 * 因此需要从redis中获取用户角色信息，构造一个UsernamePasswordAuthenticationToken
		 */
		String jwsStr = RedisUtils.getToken(sessionIdCookie.getValue());
		byte[] tokenBytes = Base64.getDecoder().decode(jwsStr);
		String tokenStr = new String(tokenBytes);

		UsernamePasswordAuthenticationToken token = null;
		try {
			token = objectMapper.readValue(tokenStr, UsernamePasswordAuthenticationToken.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		boolean expire = deleteTokenIfExpire(lastTimeCookie, sessionIdCookie, token.getName());
		if (!expire){
			SecurityContextHolder.getContext().setAuthentication(token);
			response.addCookie(new Cookie("lastTime", Instant.now().toString()));
		}
	}


	/**
	 * 如果token过期，将当前用户设置为匿名，并从Redis删除token
	 *
	 * @param lastTimeCookie
	 * @param sessionIdCookie
	 * @param username
	 * @return  已过期返回true，反之false
	 */
	private boolean deleteTokenIfExpire(Cookie lastTimeCookie, Cookie sessionIdCookie, String username){
		Instant lastInstant = Instant.parse(lastTimeCookie.getValue());
		long minutes = Duration.between(lastInstant, Instant.now()).toMinutes();
		if (minutes > expireTime){
			Authentication newAuthentication = new AnonymousAuthenticationToken("key", "anonymous",
				AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
			SecurityContextHolder.getContext().setAuthentication(newAuthentication);
			RedisUtils.deleteToken(sessionIdCookie.getValue());
			RedisUtils.deleteUser(username);
			log.warn("用户：" + username + "的token已过期");
			return true;
		}
		return false;
	}
}
