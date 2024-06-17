package com.project.springbootprojectbackend.entity;

import org.springframework.context.annotation.Bean;


public record RestBean<T>(int code, T data, String message) {
    public static <T> RestBean<T> success(T data){
        return new RestBean<T>(200,data,"登录成功");
    }
    public static <T> RestBean<T> success(){
        return success(null);
    }
    public static <T> RestBean<T> fail(int code,String message){
        return new RestBean<>(code,null,message);
    }
}
