package com.example.security.auth.service;

import com.example.security.auth.entity.Resource;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.security.auth.entity.ResourceAttribute;
import com.example.security.auth.entity.ResourceRoleDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author JiaMengwei
 * @since 2020-01-07
 */
public interface IResourceService extends IService<Resource> {

    /**
     * 获取资源属性列表
     * @return
     */
    List<ResourceAttribute> findAllResourceAttribute();
}
