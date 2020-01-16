package com.example.security.config.security;

import com.example.security.auth.entity.Role;
import com.example.security.auth.entity.UserDetailsDTO;
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

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetailsDTO userDetailsDTO = userService.findUserDetailsByUsername(username);

		if (userDetailsDTO == null) {
			log.debug("Query returned no results for user '" + username + "'");
			throw new UsernameNotFoundException("username not found");
		}

		Collection<? extends GrantedAuthority> authorities = Collections.emptyList();

		List<Role> roleList = userDetailsDTO.getRoleList();
		if (!CollectionUtils.isEmpty(roleList)) {
			String[] roles = userDetailsDTO.getRoleList()
				.stream()
				.map(Role::getName)
				.collect(Collectors.toList())
				.toArray(new String[userDetailsDTO.getRoleList().size()]);

			authorities = AuthorityUtils.createAuthorityList(roles);
		}

		return new org.springframework.security.core.userdetails.User(userDetailsDTO.getUsername(), userDetailsDTO.getPassword(), userDetailsDTO.getEnabled(),
			true, true, true, authorities);
	}
}
