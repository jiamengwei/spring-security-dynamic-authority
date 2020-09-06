package org.dynamic.demo.security;

import org.dynamic.authority.autoconfigure.DynamicSecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DynamicSecurityInterceptor dynamicSecurityInterceptor;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(dynamicSecurityInterceptor, FilterSecurityInterceptor.class);
        http.csrf().disable();
        http.formLogin()
            .and()
            .logout();
    }
}
