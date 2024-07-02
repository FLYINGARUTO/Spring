package com.project.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.backend.entity.dto.Account;

import com.project.backend.entity.vo.request.EmailAccountVO;
import com.project.backend.entity.vo.request.ResetPasswordVO;
import com.project.backend.entity.vo.request.ResetVerifyVO;
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


    /**
     * 注册账号
     * @param vo 注册请求封装的实体类
     *  包含 用户名 邮箱 密码 验证码
     * @return
     */
    String registerEmailAccount(EmailAccountVO vo);


    /**
     * 重置密码的第一步 输入email和验证码验证身份
     * @param vo
     * @return
     */
    String resetVerification(ResetVerifyVO vo);


    /**
     * 重置密码的第二步 提交新密码
     * @param vo
     * @return
     */
    String resetPassword(ResetPasswordVO vo);
}
