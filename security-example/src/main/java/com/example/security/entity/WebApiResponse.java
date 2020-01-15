package com.example.security.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jia01
 * @date 2019-11-20
 * @description
 **/

@Data
@Accessors(chain = true)
public class WebApiResponse<T> {
    private static final Integer SUCCESS_CODE = 1;
    private static final String SUCCESS_MSG = "OK";
    private static final Integer ERROR_CODE = 0;
    
    private Integer code;
    private String msg;
    private T data;
    
    public static <T> WebApiResponse<T> success(T data){
        WebApiResponse<T> result = new WebApiResponse<>();
        result.setCode(SUCCESS_CODE);
        result.setMsg(SUCCESS_MSG);
        result.setData(data);
        return result;
    }

    public static <T> WebApiResponse<T> error(String msg){
        WebApiResponse<T> result = new WebApiResponse<>();
        result.setCode(SUCCESS_CODE);
        result.setMsg(msg);
        return result;
    }
}