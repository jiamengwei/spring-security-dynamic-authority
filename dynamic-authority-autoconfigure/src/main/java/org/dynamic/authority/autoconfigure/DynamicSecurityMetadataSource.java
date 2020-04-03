package org.dynamic.authority.autoconfigure;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class DynamicSecurityMetadataSource extends AbstractRedisSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    public DynamicSecurityMetadataSource(DynamicAuthorityProperties dynamicAuthorityProperties) {
        super(dynamicAuthorityProperties);
    }

    /**
     * Bean初始化完成后执行，用于从数据库中加载资源的权限信息
     */
    @PostConstruct
    public void init() {
        SecurityMetadataSourceSupport support = super.securityMetadataSourceSupport;
        if (support != null && !CollectionUtils.isEmpty(support.getAllAuthority())) {
            super.refresh();
        }
    }


    /**
     * 从Redis中获取当前Url所有需要的角色权限信息
     * 如果找不到，返回OPEN，表示当前Url没有配置权限
     * <p>
     * 在决策管理器中会对当前Url所需的权限进行校验，如果为OPEN则直接放行，交给下一个过滤器进行处理
     *
     * @param object
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        final HttpServletRequest request = ((FilterInvocation) object).getRequest();
        String requestURI = request.getRequestURI();
        /**
         * 校验当前url是否配置未公开访问
         */
        List<String> permitUrls = super.dynamicAuthorityProperties.getPermits();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        long res = permitUrls.stream().filter(pattern -> {
            return antPathMatcher.match(pattern, requestURI);
        }).count();
        /**
         * 返回空list即不进行权限校验，直接放行
         */
        if (res > 0) {
            return null;
        }
        Collection<ConfigAttribute> configAttributes = getConfigAttributesByKey(requestURI);
        if (!CollectionUtils.isEmpty(configAttributes)) {
            return configAttributes;
        }

        if (!dynamicAuthorityProperties.isNoMatcherPermit()) {
            Collection<ConfigAttribute> any = new ArrayList<>();
            any.add(new SecurityConfig("unauthorized"));
            return any;
        }
        return null;
    }

    /**
     * 用于校验ConfigAttribute是否可用
     *
     * @return
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<>();
        allAttributes.addAll(super.getConfigAttributes());
        return allAttributes;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
