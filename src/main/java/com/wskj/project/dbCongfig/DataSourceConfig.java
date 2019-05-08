package com.wskj.project.dbCongfig;

import com.alibaba.druid.pool.DruidDataSource;
import com.wskj.formdesigner.FormDesignerApplication;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据源配置
 */
@Configuration
public class DataSourceConfig {
    private Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean
    public DruidDataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        try {
            PropertiesConfiguration properties = new PropertiesConfiguration("DB.properties");
            String driverClassName = properties.getString("driverClassName");
            dataSource.setDriverClassName(driverClassName);
            dataSource.setUrl(properties.getString("url"));
            dataSource.setUsername(properties.getString("username"));
            dataSource.setPassword(properties.getString("password"));

        } catch (ConfigurationException e) {
            logger.error("获取数据库配置文件失败" + e);
        }

        return dataSource;
    }

    /**
     * 动态修改数据库链接
     */
    public void changeDataSource () {
        Thread restartThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    FormDesignerApplication.restart();
                } catch (InterruptedException ignored) {
                }
            }
        });
        restartThread.setDaemon(false);
        restartThread.start();
    }

}