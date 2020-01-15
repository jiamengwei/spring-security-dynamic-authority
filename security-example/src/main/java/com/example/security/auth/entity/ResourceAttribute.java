package com.example.security.auth.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author JiaMengwei
 */
@Data
public class ResourceAttribute implements Serializable {

    private static final long serialVersionUID = 4890570169732112124L;
    /**
     * 资源
     */
    private Resource resource;
    /**
     * 角色列表
     */
    private List<Role> roleList;
}
