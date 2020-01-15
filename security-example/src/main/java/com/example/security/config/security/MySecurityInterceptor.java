package com.example.security.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Component
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
}
