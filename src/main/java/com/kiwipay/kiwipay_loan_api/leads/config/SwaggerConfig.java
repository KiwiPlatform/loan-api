package com.kiwipay.kiwipay_loan_api.leads.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI kiwipayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("KiwiPay Loan API")
                        .version("1.0.0")
                        .description("API para gestión de leads de préstamos")
                        .contact(new Contact()
                                .name("KiwiPay Development Team")
                                .email("dev@kiwipay.pe")));
    }
} 