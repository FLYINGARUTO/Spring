package com.project.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.backend.entity.Const;
import com.project.backend.entity.dto.Account;
import com.project.backend.mapper.AccountMapper;
import com.project.backend.service.AccountService;
import com.project.backend.utils.FluxUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
    @Resource
    StringRedisTemplate template;
    @Resource
    AmqpTemplate amqpTemplate;
    @Resource
    FluxUtils fluxUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account=this.findByNameOrEmail(username);
        if(account==null)
            throw new UsernameNotFoundException("用户名或密码错误");
        return User.withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    @Override
    public Account findByNameOrEmail(String text){
        return this.query()
                .eq("username",text).or()
                .eq("email",text).one();
    }

    @Override
    public String registerEmailVerifyCode(String type, String email, String address) {
        synchronized (address.intern()){
            if(fluxUtils.withinSixty(address))
                return "60秒内已请求过，勿频繁请求，稍后再试";
            Random random=new Random();
            //生成一个6位数，即100000～999999
            int code = random.nextInt(899999) + 100000;
            Map<String,Object> map= Map.of("type",type,"email",email,"code",code);
            //放入消息队列
            amqpTemplate.convertAndSend("mail",map);
            //把验证码存入redis
            template.opsForValue().set(Const.VERIFY_EMAIL_DATA+email ,
                    String.valueOf(code),3, TimeUnit.MINUTES);
            return null;
        }

    }
}
