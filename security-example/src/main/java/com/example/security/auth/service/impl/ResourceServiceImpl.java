package com.example.security.auth.service.impl;

import com.example.security.auth.entity.Resource;
import com.example.security.auth.entity.ResourceAttribute;
import com.example.security.auth.entity.ResourceRoleDTO;
import com.example.security.auth.mapper.ResourceMapper;
import com.example.security.auth.service.IResourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JiaMengwei
 * @since 2020-01-07
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements IResourceService {

    @Autowired
    private  ResourceMapper resourceMapper;
    /**
     * 获取资源属性列表
     *
     * @return
     */
    @Override
    public List<ResourceAttribute> findAllResourceAttribute() {
        return resourceMapper.findAllResourceAttribute();
    }
}
