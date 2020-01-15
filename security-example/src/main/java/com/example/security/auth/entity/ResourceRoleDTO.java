package com.example.security.auth.entity;

import lombok.Data;

import java.util.List;

@Data
public class ResourceRoleDTO {
    /**
     * Url
     * 示例： /admin, /product
     */
    private String resource;
    /**
     * 角色列表
     */
    private List<String> roleList;
}
