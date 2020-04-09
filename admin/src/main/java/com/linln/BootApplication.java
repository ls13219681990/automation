package com.linln;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@SpringBootApplication
@EnableJpaAuditing // 使用jpa自动赋值
@EnableCaching // 开启缓存
public class BootApplication extends SpringBootServletInitializer {

    protected static final Logger logger = LoggerFactory.getLogger(SpringBootServletInitializer.class);



    public static void main(String[] args) {

        SpringApplication.run(BootApplication.class, args);

    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BootApplication.class);

    }



}
