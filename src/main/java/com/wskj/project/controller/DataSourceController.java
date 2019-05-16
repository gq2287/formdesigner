package com.wskj.project.controller;

import com.wskj.project.dataSourceConfig.DataSourceConfig;
import com.wskj.project.model.ResponseResult;
import com.wskj.project.service.impl.DBServiceImpl;
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
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@Api("数据源设置Controller")
public class DataSourceController {
    private Logger logger = LoggerFactory.getLogger(DataSourceController.class);
    @Resource
    private DBServiceImpl dbService;
    public static int countDB=0;
    @Resource
    private DataSourceConfig dataSourceConfig;

    //测试数据库连接
    private static  Connection con;//声明Connection对象
    private static   Statement sql;
    private static  ResultSet res;

    @ApiOperation(value = "设置数据源", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/setDB", method = RequestMethod.POST)
    public ResponseResult setDB(String driverClassName, String url, String username, String password) {//前端非空判断了
        if(driverClassName!=null&&!"".equals(driverClassName)&&url!=null&&!"".equals(url)&&username!=null&&!"".equals(username)&&password!=null&&!"".equals(password)){
            if(countDB>=8){
                return new ResponseResult(ResponseResult.OK, "密码错误"+countDB+"次，无法设置数据源,请确认密码及用户名,", false);
            }
            boolean isOk=dbService.getUser(username.toUpperCase());
            if(isOk){
                Connection  conss=getTestDB(driverClassName,url,username,password);
                if(conss!=null){
                    try {
                        PropertiesConfiguration properties = new PropertiesConfiguration( StringUtil.getRealPathByIdea());
                        properties.setProperty("spring.datasource.driverClassName", driverClassName);
                        properties.setProperty("spring.datasource.url", url);
                        properties.setProperty("spring.datasource.username", username);
                        properties.setProperty("spring.datasource.password", password);
                        properties.save();
                        dataSourceConfig.changeDataSource();//重新启动
                        countDB=0;
                        return new ResponseResult(ResponseResult.OK, "设置数据源成功", true);
                    } catch (Exception e) {
                        logger.error("设置数据源失败：" + e);
                        countDB++;
                        return new ResponseResult(ResponseResult.OK, "设置数据源失败,请检查填写信息是否正确,异常信息:" + e.getMessage(), false);
                    }
                }else{
                    countDB++;
                    return new ResponseResult(ResponseResult.OK, "密码错误"+countDB+"次,设置数据源失败,请检查密码是否正确", false);
                }

            }else {
                return new ResponseResult(ResponseResult.OK, "设置数据源失败,请检查用户名是否正确", false);
            }
        }else{
            return new ResponseResult(ResponseResult.OK, "设置数据源失败,请检查输入框不能为空", false);
        }
    }

    @ApiOperation(value = "获取数据源", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getDB", method = RequestMethod.GET)
    public ResponseResult getDB() {
        Map<String, String> db = new HashMap<>();
        try {
            PropertiesConfiguration properties = new PropertiesConfiguration( StringUtil.getRealPathByIdea());
            db.put("driverClassName", properties.getString("spring.datasource.driverClassName"));
            db.put("url", properties.getString("spring.datasource.url"));
            db.put("username", properties.getString("spring.datasource.username"));
            db.put("password", properties.getString("spring.datasource.password"));
        } catch (Exception e) {
            logger.error("获取数据库配置文件失败：" + e);
            return new ResponseResult(ResponseResult.OK, e.getMessage(), false);
        }
        countDB=0;
        return new ResponseResult(ResponseResult.OK, "获取数据库配置成功", db, true);
    }

    /**
     * 测试数据库连接
     * @return
     */
    private Connection getTestDB(String driverClassName, String url, String username, String password){
        try {
            if(countDB==9){
                return null;
            }
            //加载数据库驱动类
            Class.forName(driverClassName);
//            System.out.println("数据库驱动加载成功");  //返回加载驱动成功信息
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        try {
            con= DriverManager.getConnection(url,username,password);//通过访问数据库的URL获取数据库连接对象 ，这里后两个参数分别是数据库的用户名及密码
//            System.out.println("数据库连接成功");  //返回连接成功信息
        }catch(SQLException e) {
            con=null;
//            System.out.println("数据库连接失败");
        }
        return con;//按方法要求返回一个Connection对象
    }

}
