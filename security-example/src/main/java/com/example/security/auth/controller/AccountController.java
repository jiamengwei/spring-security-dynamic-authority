package com.example.security.auth.controller;

import com.example.security.auth.entity.User;
import com.example.security.auth.service.IUserService;
import com.example.security.entity.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("account")
public class AccountController {
	@Autowired
	private IUserService userService;

	@PostMapping("register")
	public WebApiResponse<Boolean> register(User user) {
		return userService.register(user);
	}
}
