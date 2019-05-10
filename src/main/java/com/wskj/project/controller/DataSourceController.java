package com.wskj.project.controller;

import com.wskj.project.dataSourceConfig.DataSourceConfig;
import com.wskj.project.model.ResponseResult;
import com.wskj.project.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@Api("数据源设置Controller")
public class DataSourceController {
    private Logger logger = LoggerFactory.getLogger(DataSourceController.class);
    @Resource
    private DataSourceConfig dataSourceConfig;

    @ApiOperation(value = "设置数据源", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/setDB", method = RequestMethod.POST)
    public ResponseResult setDB(String driverClassName, String url, String username, String password) {
        try {
            PropertiesConfiguration properties = new PropertiesConfiguration(StringUtil.getProperties());
            properties.setProperty("spring.datasource.driverClassName", driverClassName);
            properties.setProperty("spring.datasource.url", url);
            properties.setProperty("spring.datasource.username", username);
            properties.setProperty("spring.datasource.password", password);
            properties.save();
            dataSourceConfig.changeDataSource();//重新启动
        } catch (Exception e) {
            logger.error("设置数据源失败：" + e);
            return new ResponseResult(ResponseResult.OK, "设置数据源失败,请检查异常信息:" + e.getMessage(), false);
        }
          return new ResponseResult(ResponseResult.OK, "设置数据源成功", true);
    }

    @ApiOperation(value = "获取数据源", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getDB", method = RequestMethod.GET)
    public ResponseResult getDB() {
        Map<String, String> db = new HashMap<>();
        try {
            PropertiesConfiguration properties = new PropertiesConfiguration(StringUtil.getProperties());
            db.put("driverClassName", properties.getString("spring.datasource.driverClassName"));
            db.put("url", properties.getString("spring.datasource.url"));
            db.put("username", properties.getString("spring.datasource.username"));
            db.put("password", properties.getString("spring.datasource.password"));
        } catch (Exception e) {
            logger.error("获取数据库配置文件失败：" + e);
            return new ResponseResult(ResponseResult.OK, e.getMessage(), false);
        }
        return new ResponseResult(ResponseResult.OK, "获取数据库配置成功", db, true);
    }

}
