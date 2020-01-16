package com.example.security.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserRole implements Serializable {

	private static final long serialVersionUID = -4216843194795863426L;

	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	private String userId;

	private String roleId;
}
