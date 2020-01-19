package com.example.security.auth.service;

import com.example.security.auth.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author JiaMengwei
 * @since 2020-01-07
 */
public interface IRoleService extends IService<Role> {

	/**
	 * 根据用户id获取对应的角色信息
	 * @param userId
	 * @return
	 */
	List<Role> findRolesByUserId(Integer userId);
}
