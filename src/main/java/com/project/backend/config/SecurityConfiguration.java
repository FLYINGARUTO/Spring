package com.project.backend.config;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.project.backend.entity.RestBean;
import com.project.backend.entity.dto.Account;
import com.project.backend.entity.vo.response.AuthorizeVO;
import com.project.backend.filter.JwtAuthorizeFilter;
import com.project.backend.service.AccountService;
import com.project.backend.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration {

    @Resource
    JwtUtils jwtUtils;
    @Resource
    JwtAuthorizeFilter authorizeFilter;
    @Resource
    AccountService service;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf -> conf
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                ).formLogin(conf ->conf
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure)
                ).logout(conf -> conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)
                )
                .exceptionHandling(conf-> conf
                        .authenticationEntryPoint(this::onUnauthorized)
                        .accessDeniedHandler(this::onAccessDenied)
                )

                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .addFilterBefore(authorizeFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        User user = (User) authentication.getPrincipal();
        Account account=service.findByNameOrEmail(user.getUsername());
        String token=jwtUtils.createJwt(user,account.getId(), account.getUsername());
        AuthorizeVO vo=account.asViewObject(AuthorizeVO.class);
        vo.setExpire(jwtUtils.expireDate());
        vo.setToken(token);
        response.getWriter().write(JSONObject.toJSONString(RestBean.success(vo), JSONWriter.Feature.WriteNulls));
    }
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONObject.toJSONString(RestBean.fail(401, exception.getMessage())));
    }
    public void onUnauthorized(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
    }
    public void onAccessDenied(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(RestBean.forbidden(exception.getMessage()).asJsonString());
    }
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        String token=request.getHeader("Authorization");
        PrintWriter writer=response.getWriter();
        if(jwtUtils.invalidateJwt(token)){
            writer.write(RestBean.success("登出成功").asJsonString());
        }else{
            writer.write(RestBean.fail(400,"登出失败").asJsonString());
        }
    }
}
