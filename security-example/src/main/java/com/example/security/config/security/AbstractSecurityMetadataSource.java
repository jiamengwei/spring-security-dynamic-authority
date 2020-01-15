package com.example.security.config.security;

import com.example.security.auth.entity.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractSecurityMetadataSource {

	private final Map<Resource, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<>();

	/**
	 * 重新加载权限信息，清空之前的权限信息
	 *
	 * @param map
	 * @return
	 */
	public int reloadConfigAttributes(Map<Resource, Collection<ConfigAttribute>> map) {
		requestMap.clear();
		requestMap.putAll(map);
		return requestMap.size();
	}

	/**
	 * 获取所有权限信息
	 *
	 * @return
	 */
	public Map<Resource, Collection<ConfigAttribute>> getRequestMap() {
		return requestMap;
	}

	/**
	 * 刷新所有Url权限
	 *
	 * @return Url总数
	 */
	public int refresh(){
		Map<Resource, Collection<ConfigAttribute>> newRequestMap =  getNewRequestMap();
		if (newRequestMap == null){
			return 0;
		}
		return reloadConfigAttributes(newRequestMap);
	}

	/**
	 * 获取Url信息和与之对应的权限信息，可以从任意数据源获取
	 *
	 * @return
	 */
	abstract Map<Resource, Collection<ConfigAttribute>> getNewRequestMap();
}
