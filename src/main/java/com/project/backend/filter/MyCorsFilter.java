package com.project.backend.filter;

import com.project.backend.entity.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;



public class MyCorsFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.addCorsHeader(request,response);
        chain.doFilter(request,response);
    }
    private void addCorsHeader(HttpServletRequest request, HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));
        response.addHeader("Access-Control-Allow-Method","GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers","Authorization, Content-Type");


    }
}
