package com.example.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.security.KeyPair;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JwtUtil {

	public static void main(String[] args) {

		KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256); //or RS384, RS512, PS256, PS384, PS512, ES256, ES384, ES512
		Key privateKey = keyPair.getPrivate();
		Key publicKey = keyPair.getPublic();

		Claims claims = Jwts.claims();
		claims.setSubject("Joe");

		Map<String, Object> map = new HashMap<>();
		map.put("username", "张三");
		map.put("role", Arrays.asList("admin", "developer"));
		map.put("expireTime", LocalDateTime.of(2020,1,16,10,2,2).plusMinutes(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

		String jws = Jwts.builder()
			.setClaims(claims)
			.setClaims(map)
			.signWith(privateKey)
			.compact();
		System.out.println("生成Token：" + jws);

		Claims claimsA = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(jws).getBody();
		System.out.println(claimsA.get("username"));
		List<String> roles = (List<String>) claimsA.get("role");
		roles.forEach(System.out::println);

		String expireTimeStr = (String) claimsA.get("expireTime");
		LocalDateTime time = LocalDateTime.parse(expireTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		System.out.println(time);
		if (time.isBefore(LocalDateTime.now())) {
			System.out.println("已过期");
		} else {
			Duration duration = Duration.between(LocalDateTime.now(), time);
			System.out.println("token还有" + duration.getSeconds() + "秒过期");
		}
	}
}
