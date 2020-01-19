package com.example.security.auth.service.impl;

import com.example.security.auth.entity.Role;
import com.example.security.auth.mapper.RoleMapper;
import com.example.security.auth.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JiaMengwei
 * @since 2020-01-07
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

	@Autowired
	private RoleMapper roleMapper;

	/**
	 * 根据用户id获取对应的角色信息
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public List<Role> findRolesByUserId(Integer userId) {
		return roleMapper.findRolesByUserId(userId);
	}
}
