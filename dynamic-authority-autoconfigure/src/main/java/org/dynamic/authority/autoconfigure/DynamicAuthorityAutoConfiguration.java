package org.dynamic.authority.autoconfigure;

import org.dynamic.authority.autoconfigure.DynamicAuthorityProperties;
import org.dynamic.authority.autoconfigure.DynamicSecurityInterceptor;
import org.dynamic.authority.autoconfigure.MetadataSource.DefaultSecurityMetadataSourceSupport;
import org.dynamic.authority.autoconfigure.MetadataSource.JdbcFilterInvocationSecurityMetadataSource;
import org.dynamic.authority.autoconfigure.OneVotePermit;
import org.dynamic.authority.autoconfigure.SecurityMetadataSourceSupport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

@EnableConfigurationProperties({DynamicAuthorityProperties.class})
@Configuration
public class DynamicAuthorityAutoConfiguration {

    private final RedisTemplate redisTemplate;
    private final DynamicAuthorityProperties dynamicAuthorityProperties;
    private FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource;

    public DynamicAuthorityAutoConfiguration(RedisTemplate redisTemplate, DynamicAuthorityProperties dynamicAuthorityProperties) {
        this.redisTemplate = redisTemplate;
        this.dynamicAuthorityProperties = dynamicAuthorityProperties;
    }

    @Bean
    @ConditionalOnMissingBean(AccessDecisionManager.class)
    public AccessDecisionManager accessDecisionManager() {
        return new OneVotePermit();
    }

//    @Bean
//    public FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource(SecurityMetadataSourceSupport securityMetadataSourceSupport) {
//        DynamicSecurityMetadataSource metadataSource = new DynamicSecurityMetadataSource(dynamicAuthorityProperties);
//        metadataSource.setRedisTemplate(redisTemplate);
//        metadataSource.setSecurityMetadataSourceSupport(securityMetadataSourceSupport);
//        return metadataSource;
//    }

//    @Bean
//    public FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource(){
//        JdbcFilterInvocationSecurityMetadataSource metadataSource = new JdbcFilterInvocationSecurityMetadataSource();
//        return metadataSource;
//    }

    @Bean
    @ConditionalOnMissingBean(SecurityMetadataSourceSupport.class)
    public SecurityMetadataSourceSupport securityMetadataSourceSupport() {
        return new DefaultSecurityMetadataSourceSupport();
    }


    @Bean
    @ConditionalOnBean(FilterInvocationSecurityMetadataSource.class)
    public DynamicSecurityInterceptor dynamicSecurityInterceptor() {
        return new DynamicSecurityInterceptor(filterInvocationSecurityMetadataSource, accessDecisionManager());
    }
}
