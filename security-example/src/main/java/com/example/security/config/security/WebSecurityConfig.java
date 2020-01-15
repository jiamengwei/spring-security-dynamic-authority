package com.example.security.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

/**
 * @author JiaMengwei
 */
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new MyUserDetailsServiceImpl();
	}

	@Configuration
	public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

		@Autowired
		private MySecurityInterceptor securityInterceptor;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			/**
			 * 将注册的url设定为所有人都可以访问
			 * 除登录注册外的所有url都需要登录才可以访问
			 * 设置filterSecurityInterceptorOncePerRequest为false
			 * 指定登录成功跳转的url为/hello
			 */
			http
				.csrf().disable()
				.authorizeRequests().antMatchers("/account/register").permitAll()
				.and().authorizeRequests().anyRequest().authenticated()
				.filterSecurityInterceptorOncePerRequest(false)
				.and()
				.formLogin(Customizer.withDefaults());

			/**
			 * 添加自定义的过滤器，过滤器的位置在FilterSecurityInterceptor的前面
			 * 也就是说请求会先经过自定义的过滤器进行权限验证，如果通过了验证就会直接返回，反之在通过FilterSecurityInterceptor进行验证
			 * 注意：如果filterSecurityInterceptorOncePerRequest的值为true，经过自定义过滤器的验证之后不管是否成功都会直接返回，
			 * 不会执行FilterSecurityInterceptor过滤
			 */
			http.addFilterBefore(securityInterceptor, FilterSecurityInterceptor.class);
		}
	}
}