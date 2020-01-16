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


	public static boolean checkSessionId(String hashKey) {
		return hashOperations.hasKey(EnumRedisKeys.SESSION.getValue(), hashKey);
	}

	public static String getAuthorities(String sessionId) {
		return (String) hashOperations.get(EnumRedisKeys.SESSION.getValue(), sessionId);
	}

	public static String getValue(String key, String hashKey) {
		return (String) hashOperations.get(key, hashKey);
	}
}
