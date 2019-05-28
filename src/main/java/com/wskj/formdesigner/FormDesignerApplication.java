package com.wskj.formdesigner;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@MapperScan(basePackages="com.wskj.project.dao")
@ComponentScan("com.wskj.project.*")
@EnableSwagger2
@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)//添加事务
public class FormDesignerApplication {
    private static String[] args;
    private static ConfigurableApplicationContext context;


    public static void main(String[] args) {
        FormDesignerApplication.args = args;
        FormDesignerApplication.context = SpringApplication.run(FormDesignerApplication.class, args);
    }
    public static void restart() {
        context.close();
        FormDesignerApplication.context =SpringApplication.run(FormDesignerApplication.class, args);

    }

}
