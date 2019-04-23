package com.wskj.project.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.wskj.project.model.ResponseResult;
import com.wskj.project.model.Tree;
import com.wskj.project.service.impl.ClassLevelServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api("档案树菜单Controller")
public class ClassNodeController {
    private Logger logger = LoggerFactory.getLogger(ClassNodeController.class);
    @Resource
    private ClassLevelServiceImpl classLevelService;

    @ApiOperation(value = "获取Tree菜单", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getTreeMenu", method = RequestMethod.GET)
    public ResponseResult getTreeMenu() {
        Tree tree = classLevelService.getTreeMenu();
        ResponseResult responseResult = new ResponseResult(ResponseResult.OK, "成功 ", tree);
        return responseResult;
    }

    @ApiOperation(value = "创建 L中间门类 C底层门类 ", notes = "返回信息 0成功，400失败   ")
    @RequestMapping(value = "/getCreateTree", method = RequestMethod.POST)
    public ResponseResult createTree(String name,String type,String attrs,String tableDescriptions) {
        logger.info("创建 L中间门类 C底层门类"+name+"--"+type+"--"+attrs+"--"+tableDescriptions);
        Type typeObj = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object>  pras=JSONObject.parseObject(attrs,typeObj);//JSONObject转换map
        Map<String, Object>  tds=JSONObject.parseObject(tableDescriptions,typeObj);//JSONObject转换map
        Map<String, Object> parmMap = new HashMap<>();
        parmMap.put("attrs", pras);
        parmMap.put("name", name);
        parmMap.put("type", type);
        parmMap.put("tableDescriptions", tds);//表描述
        int result = classLevelService.createTreeL(parmMap);
        if (result == 1) {
            return new ResponseResult(ResponseResult.OK, "成功", parmMap);
        } else {
            return new ResponseResult(ResponseResult.OK, "创建失败");
        }
    }

    @ApiOperation(value = "删除 L中间门类 C底层门类 (如果旗下还存在子节点前台请给与提示)", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/delTree", method = RequestMethod.POST)
    public ResponseResult delTree(String attrs) {
        logger.info("删除 L中间门类 C底层门类 (如果旗下还存在子节点前台请给与提示)"+attrs);
        Type typeObj = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String>  pras=JSONObject.parseObject(attrs,typeObj);//JSONObject转换map
        String result= classLevelService.delTreeLC(pras);
        if(!"".equals(result)){
            return new ResponseResult(ResponseResult.OK, result);
        }else{
            return new ResponseResult(ResponseResult.OK, "删除失败");
        }
    }


    @ApiOperation(value = "获取字典", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getAllDictionaryData", method = RequestMethod.GET)
    public ResponseResult getAllDictionaryData() {
        List<Map<String,String>> list=classLevelService.getAllDictionaryData();
        return  new ResponseResult(ResponseResult.OK, "成功", list);
    }

}
