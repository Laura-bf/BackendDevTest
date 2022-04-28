package com.AMS.backendDevTest.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {                                    
    @Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()   
          .apis(RequestHandlerSelectors.basePackage("com.AMS.backendDevTest"))          
          .paths(PathSelectors.any())                          
          .build()
          .apiInfo(new ApiInfo("Similar Products Backend API",
				"Backend Developer Test",
				"1.0",
				"http://product/", null, null, null,Collections.emptyList()));                                           
    }
}