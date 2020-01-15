package com.example.security.auth.service;

import com.example.security.auth.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.security.auth.entity.UserDetailsDTO;
import com.example.security.entity.WebApiResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author JiaMengwei
 * @since 2020-01-07
 */
public interface IUserService extends IService<User> {

    /**
     * 注册用户
     *
     * @param user
     * @return
     */
    WebApiResponse<Boolean> register(User user);

    /**
     * 根据username获取用户的UserDetails
     * @param username
     * @return
     */
    UserDetailsDTO findUserDetailsByUsername(String username);
}
