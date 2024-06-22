package com.project.backend.entity;


import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

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
    public static <T> RestBean<T> unauthorized(String message){
        return RestBean.fail(401,message);
    }
    public static <T> RestBean<T> forbidden(String message){
        return RestBean.fail(403,message);
    }
    public String asJsonString(){
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}
