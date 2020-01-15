package com.example.security.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class UserDetailsDTO {

    private Integer id;

    private String username;

    private String password;

    private Boolean enabled;

    private List<Role> roleList;
}
