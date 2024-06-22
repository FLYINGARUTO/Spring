package com.project.backend.config;

import com.project.backend.entity.Const;
import com.project.backend.filter.MyCorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<MyCorsFilter> myCorsFilter() {
        FilterRegistrationBean<MyCorsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new MyCorsFilter());
        registrationBean.addUrlPatterns("/*"); // Apply filter to all URLs
        registrationBean.setOrder(Const.CORS_ORDER);
        return registrationBean;
    }
}
