package org.dynamic.demo.security;


import org.dynamic.authority.autoconfigure.SecurityMetadataSourceSupport;
import org.dynamic.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SecurityBean {

    @Autowired
    private UserService userService;

    @Bean
    public UserDetailsService jdbcUserDetailsService(){
        return new UserDetailsServiceImpl(userService);
    }

    @Bean
    public UserDetailsService phoneUserDetailsService(){
        return new PhoneUserDetailsServiceImpl();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityMetadataSourceSupport securityMetadataSourceSupport() {
        return new SecurityMetadataSourceSupportImpl();
    }


    @Bean
    public AuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(jdbcUserDetailsService());
        return provider;
    }

    @Bean
    public AuthenticationProvider mobileAuthenticationProvider(){
        MobileAuthenticationProvider provider = new MobileAuthenticationProvider();
        provider.setUserDetailsService(phoneUserDetailsService());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        List<AuthenticationProvider> list = new ArrayList<>();
        list.add(daoAuthenticationProvider());
        list.add(mobileAuthenticationProvider());
        ProviderManager manager = new ProviderManager(list);
        return manager;
    }
}
