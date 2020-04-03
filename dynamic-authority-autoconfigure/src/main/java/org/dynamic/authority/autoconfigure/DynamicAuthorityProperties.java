package org.dynamic.authority.autoconfigure;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(
        prefix = "spring.security.dynamic"
)
public class DynamicAuthorityProperties implements InitializingBean {

    private List<String> permits = new ArrayList<>();

    private String authorityKey;

    private boolean noMatcherPermit = false;

    public List<String> getPermits() {
        return permits;
    }

    public void setPermits(List<String> permits) {
        this.permits = permits;
    }

    public boolean isNoMatcherPermit() {
        return noMatcherPermit;
    }

    public void setNoMatcherPermit(boolean noMatcherPermit) {
        this.noMatcherPermit = noMatcherPermit;
    }

    public String getAuthorityKey() {
        return authorityKey;
    }

    public void setAuthorityKey(String authorityKey) {
        this.authorityKey = authorityKey;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isEmpty(authorityKey)){
            throw new IllegalStateException("必须配置authorityKey属性");
        }
    }
}
