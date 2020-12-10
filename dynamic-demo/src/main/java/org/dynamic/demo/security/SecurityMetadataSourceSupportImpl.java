package org.dynamic.demo.security;


import org.dynamic.authority.autoconfigure.RequestAuthority;
import org.dynamic.authority.autoconfigure.SecurityMetadataSourceSupport;

import java.util.ArrayList;
import java.util.Arrays;import java.util.List;

public class SecurityMetadataSourceSupportImpl implements SecurityMetadataSourceSupport {


    @Override
    public List<RequestAuthority> getAllAuthority() {
        /**
         * 从数据库查询URI权限信息转换成List<RequestAuthority>类型
         */
        RequestAuthority helloRequest = new RequestAuthority();
        helloRequest.setRequest("/hello");
        helloRequest.setAuthorities(Arrays.asList("user","admin"));

        RequestAuthority helloAdminRequest = new RequestAuthority();
        helloAdminRequest.setRequest("/hello/admin");
        helloAdminRequest.setAuthorities(Arrays.asList("admin"));

        List<RequestAuthority> requestAuthorityList = new ArrayList<RequestAuthority>();
        requestAuthorityList.add(helloRequest);
        requestAuthorityList.add(helloAdminRequest);

        return requestAuthorityList;
    }
}
