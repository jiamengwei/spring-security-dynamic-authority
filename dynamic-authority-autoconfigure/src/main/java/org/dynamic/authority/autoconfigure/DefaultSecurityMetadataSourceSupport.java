package org.dynamic.authority.autoconfigure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultSecurityMetadataSourceSupport implements SecurityMetadataSourceSupport {
    @Override
    public List<UserAuthority> getAllAuthority() {

        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setAuthorities(Arrays.asList(1));
        userAuthority.setUri("/hello/admin");

        List<UserAuthority> list = new ArrayList<>();
        list.add(userAuthority);
        return list;
    }
}
