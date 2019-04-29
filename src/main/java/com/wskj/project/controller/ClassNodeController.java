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
        Tree tree =null;
        try {
            tree=classLevelService.getTreeMenu();
            logger.info("获取Tree菜单---getTreeMenu--参数--{}",tree.toString());
            return new ResponseResult(ResponseResult.OK, "成功", tree,true);
        }catch (Exception e){
            return new ResponseResult(ResponseResult.OK, e.getMessage(), tree,false);
        }
    }

    @ApiOperation(value = "创建 L中间门类 C底层门类 ", notes = "返回信息 0成功，400失败   ")
    @RequestMapping(value = "/getCreateTree", method = RequestMethod.POST)
    public ResponseResult createTree(String name,String type,String attrs,String tableDescriptions) {
        logger.info("创建 L中间门类 C底层门类---getCreateTree--参数--{}",name+"--"+type+"--"+attrs+"--"+tableDescriptions);
        Type typeObj = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object>  pras=JSONObject.parseObject(attrs,typeObj);//JSONObject转换map
        Map<String, Object>  tds=JSONObject.parseObject(tableDescriptions,typeObj);//JSONObject转换map
        Map<String, Object> parmMap = new HashMap<>();
        parmMap.put("attrs", pras);
        parmMap.put("name", name);
        parmMap.put("type", type);
        parmMap.put("tableDescriptions", tds);//表描述
        try {
            int result = classLevelService.createTreeL(parmMap);
            if (result == 1) {
                return new ResponseResult(ResponseResult.OK, "成功", parmMap,true);
            } else {
                return new ResponseResult(ResponseResult.OK, "创建失败",false);
            }
        }catch (Exception e){
            return new ResponseResult(ResponseResult.OK, "创建 L中间门类 C底层门类异常--"+e.getMessage(),false);
        }
    }

    @ApiOperation(value = "删除 L中间门类 C底层门类 (如果旗下还存在子节点前台请给与提示)", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/delTree", method = RequestMethod.POST)
    public ResponseResult delTree(String attrs) {
        logger.info("删除  L中间门类 C底层门类---delTree--参数--{}",attrs);
        Type typeObj = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String>  pras=JSONObject.parseObject(attrs,typeObj);//JSONObject转换map
        String result=null;
        try {
            result=classLevelService.delTreeLC(pras);
            if(!"".equals(result)){
                return new ResponseResult(ResponseResult.OK, result,true);
            }else{
                return new ResponseResult(ResponseResult.OK, "删除失败",true);
            }
        }catch (Exception e){
            return new ResponseResult(ResponseResult.OK, "删除 L中间门类 C底层门类异常--"+e.getMessage(),false);
        }
    }


    @ApiOperation(value = "获取字典", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getAllDictionaryData", method = RequestMethod.GET)
    public ResponseResult getAllDictionaryData() {
        List<Map<String,String>> list=null;
        try {
            list=classLevelService.getAllDictionaryData();
            logger.info("获取字典---getAllDictionaryData--参数--{}",list);
            return  new ResponseResult(ResponseResult.OK, "成功", list,true);
        }catch (Exception e){
            return new ResponseResult(ResponseResult.OK, "获取字典异常--"+e.getMessage(),false);
        }
    }

    @ApiOperation(value = "修改名称和序号", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getTreeNameAndSerial", method = RequestMethod.POST)
    public ResponseResult getTreeNameAndSerial(String treeInfo) {
        logger.info("修改名称和序号---getTreeNameAndSerial--参数--{}",treeInfo);
        boolean aBoolean=true;
        Type typeObj = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String>  pras=JSONObject.parseObject(treeInfo,typeObj);//JSONObject转换map
        aBoolean=classLevelService.upTreeNameAndSerial(pras);
        if(aBoolean){
            return new ResponseResult(ResponseResult.OK, "修改名称和序号成功",aBoolean);
        }else{
            return new ResponseResult(ResponseResult.OK, "修改名称和序号失败",aBoolean);
        }
    }

}
