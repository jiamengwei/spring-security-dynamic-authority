package org.dynamic.authority.autoconfigure;

import java.util.List;

/**
 * 用于初始化请求的角色信息
 */
public interface SecurityMetadataSourceSupport {

    List<RequestAuthority> getAllAuthority();
}
