package com.example.security.utils;

import com.example.security.enums.EnumRedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtils {

	private static RedisTemplate<String, String> redisTemplate;
	private static HashOperations hashOperations;

	@Autowired
	public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
		RedisUtils.redisTemplate = redisTemplate;
		RedisUtils.hashOperations = redisTemplate.opsForHash();
	}

	public static boolean checkToken(String hashKey) {
		return hashOperations.hasKey(EnumRedisKeys.TOKEN.getValue(), hashKey);
	}

	public static String getToken(String sessionId) {
		return (String) hashOperations.get(EnumRedisKeys.TOKEN.getValue(), sessionId);
	}

	public static Long deleteToken(String sessionId) {
		return  hashOperations.delete(EnumRedisKeys.TOKEN.getValue(), sessionId);
	}

	public static Long deleteUser(String username) {
		return  hashOperations.delete(EnumRedisKeys.USER.getValue(), username);
	}

	public static String getValue(String key, String hashKey) {
		return (String) hashOperations.get(key, hashKey);
	}
}
