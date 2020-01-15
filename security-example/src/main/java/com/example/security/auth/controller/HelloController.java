package com.example.security.auth.controller;

import com.example.security.auth.entity.Resource;
import com.example.security.config.security.UrlSecurityMetadataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author JiaMengwei
 */
@RestController
public class HelloController {

	@Autowired
	private UrlSecurityMetadataSource urlSecurityMetadataSource;

	@RequestMapping("")
	public String index() {
		return "Welcome";
	}

	@RequestMapping("developer")
	public String developer() {
		return "developer";
	}

	@RequestMapping("user")
	public String world() {
		return "user";
	}

	@RequestMapping("admin")
	public String user() {
		return "admin";
	}

	@GetMapping("refresh")
	public Integer refresh() {
		return urlSecurityMetadataSource.refresh();
	}

	@GetMapping("getUrlDetails")
	public Map<Resource, Collection<ConfigAttribute>> getUrlDetails() {
		return urlSecurityMetadataSource
			.getRequestMap();
	}


	@GetMapping("getAuthentication")
	public List<String> getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority).collect(Collectors.toList());
	}

}
