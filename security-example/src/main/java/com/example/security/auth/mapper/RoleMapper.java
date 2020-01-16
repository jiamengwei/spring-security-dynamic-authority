package com.example.security.auth.mapper;

import com.example.security.auth.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author JiaMengwei
 * @since 2020-01-07
 */
public interface RoleMapper extends BaseMapper<Role> {

	Role findByResourceId(@Param("resourceId") Integer id);

	List<Role> findRolesByUserId(@Param("userId") Integer userId);
}
