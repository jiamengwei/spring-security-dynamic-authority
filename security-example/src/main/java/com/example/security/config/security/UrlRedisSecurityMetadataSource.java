package com.example.security.config.security;

import com.example.security.auth.entity.ResourceAttribute;
import com.example.security.auth.entity.ResourceRoleDTO;
import com.example.security.auth.entity.Role;
import com.example.security.auth.service.IResourceService;
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
public class UrlRedisSecurityMetadataSource extends AbstractRedisSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

	@Autowired
	private IResourceService resourceService;

	/**
	 * Bean初始化完成后执行，用于从数据库中加载资源的权限信息
	 */
	@PostConstruct
	public void init() {
		refresh();
	}

	/**
	 * 从Redis中获取当前Url所有需要的角色权限信息
	 * 如果找不到，返回Others，表示当前Url没有配置权限
	 * <p>
	 * 在决策管理器中会对当前Url所需的权限进行校验，如果为Others则直接放行，交给下一个过滤器进行处理
	 *
	 * @param object
	 * @return
	 * @throws IllegalArgumentException
	 */
	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		final HttpServletRequest request = ((FilterInvocation) object).getRequest();
		log.info("current requestURI:" + request.getRequestURI());
		Collection<ConfigAttribute> configAttributes = getConfigAttributesByKey(request.getRequestURI());
		if (configAttributes != null) {
			return configAttributes;
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
		allAttributes.addAll(getConfigAttributes());
		return allAttributes;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

	/**
	 * 从数据库中获取所有资源权限信息
	 *
	 * @return
	 */
	@Override
	public List<ResourceRoleDTO> getResourceRoleList() {
		List<ResourceAttribute> attributeList = resourceService.findAllResourceAttribute();
		if (CollectionUtils.isEmpty(attributeList)) {
			return null;
		}
		return attributeList
			.stream()
			.map(r -> {
				ResourceRoleDTO dto = new ResourceRoleDTO();
				dto.setResource(r.getResource().getResource());
				List<String> roleNameList = r.getRoleList().stream().map(Role::getName).collect(Collectors.toList());
				dto.setRoleList(roleNameList);
				return dto;
			}).collect(Collectors.toList());
	}
}
