package org.dynamic.demo.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class TUser {
    private String username;
    private String password;
    private List<String> roleList;
}
