package com.example.security.config.security;

import com.example.security.enums.EnumRedisKeys;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;

/**
 * @author JiaMengwei
 */
public class MyAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

	public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
	public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

	private String usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;
	private String passwordParameter = SPRING_SECURITY_FORM_PASSWORD_KEY;
	private boolean postOnly = true;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private HashOperations hashOperations;

	@Autowired
	private RedisTemplate redisTemplate;

	protected MyAuthenticationProcessingFilter() {
		super(new AntPathRequestMatcher("/login", "POST"));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
		if (postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException(
				"Authentication method not supported: " + request.getMethod());
		}

		String username = obtainUsername(request);
		String password = obtainPassword(request);

		if (username == null) {
			username = "";
		}

		if (password == null) {
			password = "";
		}

		username = username.trim();

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
			username, password);

		setDetails(request, authRequest);
		Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);
		String sessionId = request.getRequestedSessionId();
		/**
		 * 保存用户名与sessionId到Redis
		 */
		pushSessionId(username, sessionId);
		/**
		 * 保存token到redis
		 */
		pushToken(sessionId, authentication);
		/**
		 * 添加sessionId和请求时间到cookie
		 */
		addCookie(response, sessionId);
		return authentication;
	}

	/**
	 * 添加sessionId和lastTime(请求时间)到cookie，
	 * sessionId用于校验当前用户是否登录
	 * lastTime用于校验sessionId是否过期
	 *
	 * @param response
	 * @param sessionId
	 */
	private void addCookie(HttpServletResponse response, String sessionId){
		response.addCookie(new Cookie("sessionId", sessionId));
		response.addCookie(new Cookie("lastTime", Instant.now().toString()));
	}

	/**
	 * 序列化authentication对象并使用Base64进行编码，保存到redis
	 *
	 * @param sessionId 用户的sessionId
	 * @param authentication token对象
	 */
	private void pushToken(String sessionId, Authentication authentication) {
		String tokenStr = null;
		try {
			tokenStr = objectMapper.writer().writeValueAsString(authentication);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		tokenStr = Base64.getEncoder().encodeToString(tokenStr.getBytes());
		hashOperations.put(EnumRedisKeys.TOKEN.getValue(), sessionId, tokenStr);
	}

	/**
	 * 保存用户名与sessionId到Redis
	 *
	 * @param username  用户名
	 * @param sessionId sessionId
	 * @return
	 */
	public void pushSessionId(String username, String sessionId) {
		/**
		 * 校验用户名是否已存在
		 *
		 * 如果以及存在，通过用户名从Redis拿到sessionId
		 * 删除sessionId
		 * 删除username
		 */
		boolean exist = hashOperations.hasKey(EnumRedisKeys.USER.getValue(), username);
		if (exist) {
			String oldSessionId = (String) hashOperations.get(EnumRedisKeys.USER.getValue(), username);
			hashOperations.delete(EnumRedisKeys.TOKEN.getValue(), oldSessionId);
			hashOperations.delete(EnumRedisKeys.USER.getValue(), username);
		}
		/**
		 * 添加新的用户名与sessionId
		 */
		hashOperations.put(EnumRedisKeys.USER.getValue(), username, sessionId);
	}

	@Nullable
	protected String obtainPassword(HttpServletRequest request) {
		return request.getParameter(passwordParameter);
	}

	/**
	 * Enables subclasses to override the composition of the username, such as by
	 * including additional values and a separator.
	 *
	 * @param request so that request attributes can be retrieved
	 * @return the username that will be presented in the <code>Authentication</code>
	 * request token to the <code>AuthenticationManager</code>
	 */
	@Nullable
	protected String obtainUsername(HttpServletRequest request) {
		return request.getParameter(usernameParameter);
	}

	/**
	 * Provided so that subclasses may configure what is put into the authentication
	 * request's details property.
	 *
	 * @param request     that an authentication request is being created for
	 * @param authRequest the authentication request object that should have its details
	 *                    set
	 */
	protected void setDetails(HttpServletRequest request,
	                          UsernamePasswordAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}

	/**
	 * Sets the parameter name which will be used to obtain the username from the login
	 * request.
	 *
	 * @param usernameParameter the parameter name. Defaults to "username".
	 */
	public void setUsernameParameter(String usernameParameter) {
		Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
		this.usernameParameter = usernameParameter;
	}

	/**
	 * Sets the parameter name which will be used to obtain the password from the login
	 * request..
	 *
	 * @param passwordParameter the parameter name. Defaults to "password".
	 */
	public void setPasswordParameter(String passwordParameter) {
		Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
		this.passwordParameter = passwordParameter;
	}

	/**
	 * Defines whether only HTTP POST requests will be allowed by this filter. If set to
	 * true, and an authentication request is received which is not a POST request, an
	 * exception will be raised immediately and authentication will not be attempted. The
	 * <tt>unsuccessfulAuthentication()</tt> method will be called as if handling a failed
	 * authentication.
	 * <p>
	 * Defaults to <tt>true</tt> but may be overridden by subclasses.
	 */
	public void setPostOnly(boolean postOnly) {
		this.postOnly = postOnly;
	}

	public final String getUsernameParameter() {
		return usernameParameter;
	}

	public final String getPasswordParameter() {
		return passwordParameter;
	}
}
