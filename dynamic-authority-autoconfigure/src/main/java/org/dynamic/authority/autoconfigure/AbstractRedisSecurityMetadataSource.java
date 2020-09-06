package org.dynamic.authority.autoconfigure;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 提供对Redis中的资源信息进行增加、查询、以及删除操作
 *
 * @author JiaMengwei
 */
public abstract class AbstractRedisSecurityMetadataSource implements InitializingBean {


    protected RedisTemplate redisTemplate;

    protected DynamicAuthorityProperties dynamicAuthorityProperties;

    protected SecurityMetadataSourceSupport securityMetadataSourceSupport;

    public AbstractRedisSecurityMetadataSource(DynamicAuthorityProperties dynamicAuthorityProperties) {
        this.dynamicAuthorityProperties = dynamicAuthorityProperties;
    }

    /**
     * 获取所有资源的角色名称
     *
     * @return
     */
    public Collection<ConfigAttribute> getConfigAttributes() {
        return getConfigAttributeFromRedis(redisTemplate.opsForHash().keys(dynamicAuthorityProperties.getAuthorityKey()));
    }

    /**
     * 获取指定资源需要的角色名称
     *
     * @param hashKey 资源url
     * @return
     */
    public Collection<ConfigAttribute> getConfigAttributesByKey(String hashKey) {
        if (!redisTemplate.opsForHash().hasKey(dynamicAuthorityProperties.getAuthorityKey(), hashKey)) {
            return Collections.emptyList();
        }
        return getConfigAttributeFromRedis(Arrays.asList(hashKey));
    }

    /**
     * 从redis中获取所有的资源权限信息
     *
     * @return
     */
    public Collection<ConfigAttribute> getConfigAttributeFromRedis(Collection<? extends String> hashKeys) {
        List<List<String>> requestAuthorityList = redisTemplate.opsForHash().multiGet(dynamicAuthorityProperties.getAuthorityKey(), hashKeys);
        List<String> authorityList = new ArrayList<>();
        requestAuthorityList.stream().forEach(roles -> {
            authorityList.addAll(roles);
        });
        return authorityList.stream()
            .map(authority -> {
                return new SecurityConfig(authority);
            }).distinct().collect(Collectors.toList());
    }

    public int reloadConfigAttributes(List<RequestAuthority> list) {
        Object[] hashKeys = redisTemplate.opsForHash().keys(dynamicAuthorityProperties.getAuthorityKey()).toArray();
        if (hashKeys != null && hashKeys.length > 0) {
            redisTemplate.opsForHash().delete(dynamicAuthorityProperties.getAuthorityKey(), hashKeys);
        }
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        list.stream().forEach(requestAuthority -> {
            redisTemplate.opsForHash().put(dynamicAuthorityProperties.getAuthorityKey(), requestAuthority.getRequest(), requestAuthority.getAuthorities());
        });
        return list.size();
    }

    public int refresh() {
        List<RequestAuthority> userAuthorities = securityMetadataSourceSupport.getAllAuthority();
        if (CollectionUtils.isEmpty(userAuthorities)) {
            userAuthorities = Collections.emptyList();
        }
        return reloadConfigAttributes(userAuthorities);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.redisTemplate == null){
            throw new IllegalStateException("redisTemplate 不能为null");
        }

        if (this.securityMetadataSourceSupport == null){
            throw new IllegalStateException("securityMetadataSourceSupport 不能为null");
        }
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public SecurityMetadataSourceSupport getSecurityMetadataSourceSupport() {
        return securityMetadataSourceSupport;
    }

    public void setSecurityMetadataSourceSupport(SecurityMetadataSourceSupport securityMetadataSourceSupport) {
        this.securityMetadataSourceSupport = securityMetadataSourceSupport;
    }

}
