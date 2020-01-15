package com.example.security.config.security;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MyAccessDecisionManager implements AccessDecisionManager {

    /**
     * 校验当前用户是否拥有试图访问的Url所需的权限
     *
     * @param authentication 当前用户的验证信息
     * @param object    请求对象
     * @param configAttributes Url关联的权限信息
     * @throws AccessDeniedException
     * @throws InsufficientAuthenticationException
     */
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {

        /**
         * 将当前Url所关联的权限列表，转化为HashMap用于后续的校验
         */
        Map<String, String> configAttributeMap = configAttributes
                .stream()
                .collect(Collectors.toMap(ConfigAttribute::getAttribute, ConfigAttribute::getAttribute, (oldObj, newObj) -> newObj));
        /**
         * 如果当前的Url关联的权限为Others，直接放行交给下一个过滤器处理
         */
        if (configAttributeMap.get("Others") != null){
            return;
        }
        /**
         * 如果当前用户没有权限，抛出拒绝访问异常
         */
        authentication.getAuthorities().stream().filter(auth -> {
            return configAttributeMap.get(auth.getAuthority()) != null  ? true : false;
        }).findFirst().orElseThrow(()-> new AccessDeniedException("Access is denied"));
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
