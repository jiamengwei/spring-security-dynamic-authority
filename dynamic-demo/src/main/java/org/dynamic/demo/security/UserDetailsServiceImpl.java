package org.dynamic.demo.security;

import org.dynamic.demo.entity.TUser;
import org.dynamic.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


public class UserDetailsServiceImpl implements UserDetailsService {

    private UserService userService;

    public UserDetailsServiceImpl(UserService userService){
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TUser tUser = userService.findUserByName(username);
        if (null == tUser) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return User.builder()
            .username(username)
            .password(tUser.getPassword())
            .authorities(tUser.getRoleList().toArray(new String[tUser.getRoleList().size()]))
            .accountExpired(false)
            .accountLocked(false)
            .disabled(false)
            .build();
    }
}
