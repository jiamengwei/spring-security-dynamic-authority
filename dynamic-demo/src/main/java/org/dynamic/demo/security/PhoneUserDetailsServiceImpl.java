package org.dynamic.demo.security;

import org.dynamic.demo.entity.TUser;
import org.dynamic.demo.service.UserService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;


public class PhoneUserDetailsServiceImpl implements UserDetailsService {




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return User.builder()
            .username(username)
            .password("123456")
            .authorities("user")
            .accountExpired(false)
            .accountLocked(false)
            .disabled(false)
            .build();
    }
}
