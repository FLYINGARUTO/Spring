package com.project.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.backend.entity.dto.Account;
import com.project.backend.mapper.AccountMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account>, UserDetailsService {
    public Account findByNameOrEmail(String text);
}
