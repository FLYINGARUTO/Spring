package com.project.backend.utils;

import com.project.backend.entity.Const;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class FluxUtils {
    @Resource
    StringRedisTemplate template;

    /**
     * 判断当前ip在60秒内有没有请求过验证码
     * @param ip
     * @return
     */
    public boolean withinSixty(String ip){
        boolean exist = Boolean.TRUE.equals(   template.hasKey(Const.VERIFY_EMAIL_LIMIT+ip)   );
        //redis里存在该ip 60秒内请求的记录 让原ip滚蛋
        if(exist){
            return true;
        }
        //不存在该ip，则在redis中记录下该ip
        else{
            template.opsForValue().set(Const.VERIFY_EMAIL_LIMIT+ip,"",60, TimeUnit.SECONDS);
            return  false;
        }
    }
}
