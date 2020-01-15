package com.example.security.config.security;

import com.example.security.auth.entity.Resource;
import com.example.security.auth.entity.ResourceAttribute;
import com.example.security.auth.service.IResourceService;
import com.example.security.config.security.AbstractSecurityMetadataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UrlSecurityMetadataSource extends AbstractSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

	@Autowired
	private IResourceService resourceService;

	/**
	 * Bean初始化完成后执行，用于初始化资源的权限信息
	 */
	@PostConstruct
	public void init() {
		super.refresh();
	}

	/**
	 * 获取当前Url所需要的角色权限信息
	 * 如果找不到，返回Others，表示当前Url没有配置权限
	 * 在决策管理器(AccessDecisionManager)中会对当前Url所需的权限进行校验，如果为Others则直接放行，交给下一个过滤器进行处理
	 *
	 * @param object
	 * @return
	 * @throws IllegalArgumentException
	 */
	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		final HttpServletRequest request = ((FilterInvocation) object).getRequest();
		log.info("current requestURI:" + request.getRequestURI());
		for (Map.Entry<Resource, Collection<ConfigAttribute>> entry : getRequestMap()
			.entrySet()) {
			if (entry.getKey().getResource().equals(request.getRequestURI())) {
				return entry.getValue();
			}
		}
		Collection<ConfigAttribute> any = new ArrayList<>();
		any.add(new SecurityConfig("Others"));
		return any;
	}

	/**
	 * 用于校验ConfigAttribute是否可用
	 *
	 * @return
	 */
	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		Set<ConfigAttribute> allAttributes = new HashSet<>();
		for (Map.Entry<Resource, Collection<ConfigAttribute>> entry : getRequestMap()
			.entrySet()) {
			allAttributes.addAll(entry.getValue());
		}
		return allAttributes;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	public Map<Resource, Collection<ConfigAttribute>> getNewRequestMap() {
		/**
		 * 从数据库获取Url和对应的权限信息
		 */
		List<ResourceAttribute> resourceAttributeList = resourceService.findAllResourceAttribute();
		if (CollectionUtils.isEmpty(resourceAttributeList)) {
			return null;
		}
		/**
		 * 将权限信息转换为Map类型
		 */
		return resourceAttributeList
			.stream()
			.collect(Collectors.toMap(ResourceAttribute::getResource, resourceAttribute -> {
					return resourceAttribute.getRoleList()
						.stream()
						.map(role -> new SecurityConfig(role.getName()))
						.collect(Collectors.toList());
				}
			));
	}
}
