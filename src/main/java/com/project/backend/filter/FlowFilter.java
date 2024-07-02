package com.project.backend.filter;

import com.project.backend.entity.Const;
import com.project.backend.entity.RestBean;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Order(Const.FLOW_ORDER)
public class FlowFilter extends HttpFilter {
    @Resource
    StringRedisTemplate template;
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String ip=request.getRemoteAddr();
        if(this.countIP(ip)){
            chain.doFilter(request,response);
        }else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(RestBean.forbidden("请求过于频繁").asJsonString());
        }

    }
    private boolean isForbidden(String ip){
        return Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_FORBID + ip));
    }

    private boolean countIP(String ip){
        //先判断该ip是否被封
        if(isForbidden(ip)){
            return false;
        }else {
            //判断ip是否是第一次访问
            if(Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_COUNT+ip))){
                long count= Optional.ofNullable(template.opsForValue().increment(Const.FLOW_LIMIT_COUNT+ip)).orElse(0L);
                if(count>10){
                    template.opsForValue().set(Const.FLOW_LIMIT_FORBID+ip,"",20,TimeUnit.SECONDS);
                    return false;
                }
            }else{
                template.opsForValue().set(Const.FLOW_LIMIT_COUNT+ip,"1",10, TimeUnit.SECONDS);
            }
            return true;
        }


    }


}
