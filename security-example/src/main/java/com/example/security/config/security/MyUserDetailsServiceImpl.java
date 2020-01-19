package com.example.security.config.security;

import com.example.security.auth.entity.Role;
import com.example.security.auth.entity.User;
import com.example.security.auth.service.IRoleService;
import com.example.security.auth.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MyUserDetailsServiceImpl implements UserDetailsService {


	@Autowired
	private IUserService userService;

	@Autowired
	private IRoleService roleService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userService.findUserByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("username not found");
		}
		List<Role> roleList = roleService.findRolesByUserId(user.getId());
		Collection<? extends GrantedAuthority> authorities = Collections.emptyList();
		if (!CollectionUtils.isEmpty(roleList)) {
			String[] roles = roleList
				.stream()
				.map(Role::getName)
				.collect(Collectors.toList())
				.toArray(new String[roleList.size()]);
			authorities = AuthorityUtils.createAuthorityList(roles);
		}

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.getEnabled(),
			true, true, true, authorities);
	}
}
