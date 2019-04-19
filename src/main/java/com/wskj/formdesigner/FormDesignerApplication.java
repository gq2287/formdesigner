package com.wskj.formdesigner;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@MapperScan(basePackages="com.wskj.project.dao")
@ComponentScan("com.wskj.project.*")
@EnableSwagger2
@SpringBootApplication
public class FormDesignerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FormDesignerApplication.class, args);
    }
}
