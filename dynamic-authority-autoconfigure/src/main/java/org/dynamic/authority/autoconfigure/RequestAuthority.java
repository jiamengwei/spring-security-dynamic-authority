package org.dynamic.authority.autoconfigure;

import java.util.List;

/**
 * 表示request对应的角色信息
 * request:当前请求的uri
 * authorities：可访问当前请求的角色
 */
public class RequestAuthority {

    private String request;

    private List<String> authorities;


    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}