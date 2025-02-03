package com.ausiasmarch.deckrift.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ausiasmarch.deckrift.service.JWTService;
import com.ausiasmarch.filter.JWTFilter;

@Configuration
public class FilterConfig {


    private final JWTService jwtService;

    @Autowired
    public FilterConfig(JWTService jwtService) {
        this.jwtService = jwtService; // Inyección de JWTService
    }

    @Bean
    public FilterRegistrationBean<JWTFilter> jwtFilter() {
        FilterRegistrationBean<JWTFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JWTFilter(jwtService));
        registrationBean.addUrlPatterns("/usuario/*", "/admin/*"); // Ajusta las rutas protegidas
        registrationBean.setOrder(1); // Para ejecutarse antes que otros filtros
        return registrationBean;
    }
}
