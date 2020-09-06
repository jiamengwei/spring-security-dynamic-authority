package org.dynamic.demo.service;

import org.dynamic.demo.entity.TUser;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public TUser findUserByName(String username) {
        /**
         * 从数据库查询用户信息
         */
        TUser tUser = new TUser();
        return tUser.setUsername("admin")
            .setPassword("$2a$10$cMJfZMIoJVkHaSTXBsG6XeFi8jxrm6PB/H0IFb1lkaZu/sMztVu2C")
            .setRoleList(Arrays.asList("user", "admin"));
    }
}
