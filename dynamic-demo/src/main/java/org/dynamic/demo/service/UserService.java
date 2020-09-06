package org.dynamic.demo.service;


import org.dynamic.demo.entity.TUser;

public interface UserService {
    TUser findUserByName(String username);
}
