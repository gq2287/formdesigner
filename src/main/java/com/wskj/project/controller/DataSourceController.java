package com.wskj.project.controller;

import com.wskj.project.model.DataSourceConfig;
import com.wskj.project.model.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.configuration.ConfigurationException;
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
    public ResponseResult setDB(String driverClassName,String url,String username,String password){
        try {
            PropertiesConfiguration conf = new PropertiesConfiguration("db.properties");
            conf.setProperty("driverClassName",driverClassName);
            conf.setProperty("url",url);
            conf.setProperty("username",username);
            conf.setProperty("password",password);
            conf.save();
            dataSourceConfig.changeDataSource();
        } catch (Exception e) {
            logger.error("获取数据库配置文件失败：" + e);
            return new ResponseResult(ResponseResult.OK, "数据库配置失败,请检查异常信息:"+e.getMessage(),false);
        }
        return new ResponseResult(ResponseResult.OK, "数据库配置成功",true);
    }

    @ApiOperation(value = "获取数据源", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getDB", method = RequestMethod.POST)
    public ResponseResult getDB(){
        Map<String,String> db=new HashMap<>();
        try {
            PropertiesConfiguration conf= new PropertiesConfiguration("DB.properties");
            db.put("driverClassName",conf.getString("driverClassName"));
            db.put("url",conf.getString("url"));
            db.put("username",conf.getString("username"));
            db.put("password",conf.getString("password"));
        } catch (ConfigurationException e) {
            logger.error("获取数据库配置文件失败：" + e);

            return new ResponseResult(ResponseResult.OK, e.getMessage(),false);
        }
        return new ResponseResult(ResponseResult.OK, "数据库配置成功",db,true);
    }

}
