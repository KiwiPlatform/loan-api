package com.kiwipay.kiwipay_loan_api.leads.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Asegurar que el CSS personalizado se sirva correctamente
        registry.addResourceHandler("/swagger-ui-fix.css")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0); // Sin cache para development
        
        // Configuración adicional para recursos de Swagger UI
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/")
                .setCachePeriod(0);
    }
} 