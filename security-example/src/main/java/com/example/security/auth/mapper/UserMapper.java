package com.example.security.auth.mapper;

import com.example.security.auth.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.security.auth.entity.UserDetailsDTO;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author JiaMengwei
 * @since 2020-01-07
 */
public interface UserMapper extends BaseMapper<User> {

    UserDetailsDTO findUserDetailsByUsername(String username);
}
