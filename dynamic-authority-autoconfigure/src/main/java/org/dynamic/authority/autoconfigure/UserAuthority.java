package org.dynamic.authority.autoconfigure;

import java.util.List;

public class UserAuthority {

    private String uri;

    private List<Integer> authorities;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<Integer> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Integer> authorities) {
        this.authorities = authorities;
    }
}