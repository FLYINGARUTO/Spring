package com.project.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.backend.entity.dto.Account;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account>, UserDetailsService {

    Account findByNameOrEmail(String text);

    /**
     * 生成并发送验证码
     * @param type 请求的类型 注册或者重置密码
     * @param email 目标邮箱
     * @param ip 请求来自的ip
     * @return
     */
    String registerEmailVerifyCode(String type, String email,String ip);
}
