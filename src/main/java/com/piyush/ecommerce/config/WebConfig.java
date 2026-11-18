package com.piyush.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ye line sabse important hai Render/Linux ke liye
        String rootPath = Paths.get(".").toAbsolutePath().normalize().toString();
        String uploadPath = "file:" + rootPath + "/uploads/";

        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadPath)
                .setCachePeriod(0);

        // Ye logs mein check karna ki path kya print ho raha hai
        System.out.println("Final Image Path for Render: " + uploadPath);
    }
}