package com.example.security.config.security;

import com.example.security.auth.entity.ResourceRoleDTO;
import com.example.security.enums.EnumRedisKeys;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 提供对Redis中的资源信息进行增加、查询、以及删除操作
 *
 * @author JiaMengwei
 */
public abstract class AbstractRedisSecurityMetadataSource {

    /**
     * Redis Key
     */
	private static final String KEY = EnumRedisKeys.RESOURCE.getValue();

    /**
     * 用于保存资源url
     */
	private static final List<String> resourceList = new ArrayList<>();

	@Autowired
	private HashOperations hashOperations;

	/**
	 * 记录存储到Redis的资源Url
	 * 将资源的权限信息存储到Redis
	 *
	 * @param list
	 * @return 资源总数
	 */
	public int reloadConfigAttributes(List<ResourceRoleDTO> list) {
		/**
		 * 如果resourceList不为空，说明是重新加载权限到Redis
		 * 此时需要从redis中删除resourceList中保存的url信息，
		 * 然后清空resourceList，放入新的url信息
		 * 再将新的url与角色信息存入redis
		 */
		if (!CollectionUtils.isEmpty(resourceList)) {
			hashOperations.delete(KEY, resourceList.toArray());
			resourceList.clear();
		}
		List<String> resourceList = list.stream().map(ResourceRoleDTO::getResource).collect(Collectors.toList());
		resourceList.addAll(resourceList);

		list.stream().forEach(resourceRoleDTO -> {
			hashOperations.put(KEY, resourceRoleDTO.getResource(), StringUtils.join(resourceRoleDTO.getRoleList(), ","));
		});
		return resourceList.size();
	}

	public Collection<ConfigAttribute> getConfigAttributes() {
		return getConfigAttributeFromRedis(hashOperations.keys(KEY));
	}

	public Collection<ConfigAttribute> getConfigAttributesByKey(String hashKey) {
		if (!hashOperations.hasKey(KEY, hashKey)) {
			return null;
		}
		return getConfigAttributeFromRedis(Arrays.asList(hashKey));
	}

	/**
	 * 从redis中获取所有的资源权限信息
	 *
	 * @return
	 */
	public Collection<ConfigAttribute> getConfigAttributeFromRedis(Collection<? extends String> hashKeys) {
		List<String> roleStrList = hashOperations.multiGet(KEY, hashKeys);
		List<String> roleList = new ArrayList<>();
		roleStrList.stream().forEach(role -> {
			List<String> list = Arrays.asList(role.split(","));
			roleList.addAll(list);
		});
		return roleList.stream()
			.map(role -> {
				return new SecurityConfig(role);
			})
			.collect(Collectors.toList());
	}

	public List<String> getResourceList() {
		return resourceList;
	}

    abstract List<ResourceRoleDTO> getResourceRoleList();

	public int refresh(){
        List<ResourceRoleDTO> resourceRoleDTOList = getResourceRoleList();
        if (CollectionUtils.isEmpty(resourceRoleDTOList)){
        	return 0;
        }
        return reloadConfigAttributes(resourceRoleDTOList);
    }
}
