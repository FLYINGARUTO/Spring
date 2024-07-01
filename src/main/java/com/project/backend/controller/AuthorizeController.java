package com.project.backend.controller;

import com.project.backend.entity.RestBean;
import com.project.backend.entity.vo.request.EmailAccountVO;
import com.project.backend.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {
    @Resource
    AccountService service;

    @GetMapping("/getVerifyCode")
    public RestBean<Void> getVerifyCode(@RequestParam @Pattern(regexp = "register|reset") String type,
                                  @RequestParam @Email String email,
                                  HttpServletRequest request){
       String result = service.registerEmailVerifyCode(type,email,request.getRemoteAddr());
       return result==null? RestBean.success() : RestBean.fail(400,result);
    }
    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody @Valid EmailAccountVO vo){
        String message=service.registerEmailAccount(vo);
        return message==null? RestBean.success() : RestBean.fail(400,message);
    }

}
