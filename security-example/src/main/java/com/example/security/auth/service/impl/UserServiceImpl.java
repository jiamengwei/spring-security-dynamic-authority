package com.example.security.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.security.auth.entity.User;
import com.example.security.auth.entity.UserDetailsDTO;
import com.example.security.auth.mapper.UserMapper;
import com.example.security.auth.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.security.entity.WebApiResponse;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JiaMengwei
 * @since 2020-01-07
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    /**
     * 注册用户
     *
     * @param user
     * @return
     */
    @Override
    public WebApiResponse<Boolean> register(User user) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String password = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(password);
        boolean result = save(user);
        if (result){
            return WebApiResponse.success(result);
        }
        return WebApiResponse.error("注册失败，请稍后重试");
    }

    /**
     * 根据username获取用户的UserDetails
     *
     * @param username
     * @return
     */
    @Override
    public UserDetailsDTO findUserDetailsByUsername(String username) {
        UserDetailsDTO userDetailsDTO = userMapper.findUserDetailsByUsername(username);
        return userDetailsDTO;
    }

    /**
     * 根据username获取用户对象
     *
     * @param username
     * @return
     */
    @Override
    public User findUserByUsername(String username){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return userMapper.selectOne(wrapper);
    }
}
