package com.example.security.auth.mapper;

import com.example.security.auth.entity.Resource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.security.auth.entity.ResourceAttribute;
import com.example.security.auth.entity.ResourceRoleDTO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author JiaMengwei
 * @since 2020-01-07
 */
public interface ResourceMapper extends BaseMapper<Resource> {

    List<ResourceAttribute> findAllResourceAttribute();
}
