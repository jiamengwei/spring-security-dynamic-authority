package com.example.security.auth.controller;

import com.example.security.config.security.UrlRedisSecurityMetadataSource;
import com.example.security.entity.WebApiResponse;
import com.example.security.enums.EnumRedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author JiaMengwei
 */
@RestController
public class HelloController {
	/**
	 * Redis Key
	 */
	private static final String RESOURCE_KEY = EnumRedisKeys.RESOURCE.getValue();

	@Autowired
	private UrlRedisSecurityMetadataSource urlRedisSecurityMetadataSource;

	@Autowired
	private HashOperations hashOperations;

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
		return urlRedisSecurityMetadataSource.refresh();
	}

	@GetMapping("getUrlDetails")
	public List<String> getUrlDetails() {
		List<String> stringList = hashOperations.multiGet(RESOURCE_KEY, hashOperations.keys(RESOURCE_KEY));
		return stringList;
	}

	/**
	 * 获取当前用户拥有的角色信息
	 *
	 * @return
	 */
	@GetMapping("getAuthentication")
	public List<String> getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority).collect(Collectors.toList());
	}

	@GetMapping("isLogin")
	public WebApiResponse<Boolean> isLogin(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		boolean isLogin = Stream.of(cookies)
			.filter(c -> c.getName().equals("sessionId"))
			.findFirst().isPresent() ? true : false;
		return WebApiResponse.success(isLogin);
	}
}
