package com.project.backend.controller;

import com.project.backend.entity.RestBean;
import com.project.backend.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {
    @Resource
    AccountService service;
    @GetMapping("/getVerifyCode")
    public RestBean<Void> getVerifyCode(@RequestParam String type,
                                  @RequestParam String email,
                                  HttpServletRequest request){
       String result = service.registerEmailVerifyCode(type,email,request.getRequestURL().toString());
       return result==null? RestBean.success() : RestBean.fail(400,result);
    }
}
