package com.wskj.project.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.wskj.project.model.ResponseResult;
import com.wskj.project.model.Template;
import com.wskj.project.model.Tree;
import com.wskj.project.service.impl.ClassLevelServiceImpl;
import com.wskj.project.service.impl.TableServiceImpl;
import com.wskj.project.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api("档案树菜单Controller")
public class ResponseController {
    private Logger logger = LoggerFactory.getLogger(ResponseController.class);
    @Resource
    private ClassLevelServiceImpl classLevelService;
    @Resource
    private TableServiceImpl tableService;

    @ApiOperation(value = "获取Tree菜单", notes = "返回信息 0成功，400失败,500参数错误")
    @RequestMapping(value = "/getTreeMenu", method = RequestMethod.GET)
    public ResponseResult getTreeMenu() {
        Tree tree = classLevelService.getTreeMenu();
        ResponseResult responseResult = new ResponseResult(0, "成功 ", tree);
        return responseResult;
    }

    @ApiOperation(value = "创建 L中间门类 C底层门类 ", notes = "返回信息 0成功，400失败,500参数错误  ")
    @RequestMapping(value = "/getCreateTree", method = RequestMethod.POST)
    public ResponseResult createTree(String name,String type,String attrs,String tableDescriptions) {
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
            return new ResponseResult(0, "成功", parmMap);
        } else {
            return new ResponseResult(500, "处理失败", null);
        }
    }

    @ApiOperation(value = "删除 L中间门类 C底层门类 (如果旗下还存在子节点前台请给与提示)", notes = "返回信息 0成功，400失败,500参数错误")
    @RequestMapping(value = "/delTree", method = RequestMethod.POST)
    public ResponseResult delTree(String attrs) {
        Type typeObj = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String>  pras=JSONObject.parseObject(attrs,typeObj);//JSONObject转换map
        String result= classLevelService.delTreeLC(pras);
        if(!"".equals(result)){
            return new ResponseResult(0, result);
        }else{
            return new ResponseResult(400, "删除失败");
        }
    }

    @ApiOperation(value = "获取要修改表的详细信息", notes = "返回信息 0成功，400失败,500参数错误")
    @RequestMapping(value = "/getUpTree", method = RequestMethod.POST)
    public ResponseResult getUpTreeByAttrs(String attrs) {
        Type typeObj = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object>  pras=JSONObject.parseObject(attrs,typeObj);//JSONObject转换map
        Map<String,Object> mapList=tableService.getUpTreeByAttrs(pras);
        if(mapList!=null&&mapList.size()>0){
            return new ResponseResult(0, "成功 ",mapList);
        }else {
            return new ResponseResult(500, "失败 ",mapList);
        }
    }

    @ApiOperation(value = "当前表是否纯在数据", notes = "返回信息 0成功，400失败,500参数错误")
    @RequestMapping(value = "/getIsOkUpDataByTableName", method = RequestMethod.POST)
    public ResponseResult getIsOkUpDataByTableName(String tableName) {
        boolean bool=tableService.getIsOkUpDataByTableName(tableName);
        if(bool){
            return new ResponseResult(0, "可以修改成功");
        }else {
            return new ResponseResult(400, "失败");
        }
    }
    @ApiOperation(value = "type(add增 del删 up改) 字段", notes = "返回信息 0成功，400失败,500参数错误")
    @RequestMapping(value = "/getModifyFieldInfo", method = RequestMethod.POST)
    public ResponseResult getModifyFieldInfo(String fieldInfo,String type) {
        boolean bool=true;
        Type typeObj = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object>  pras=JSONObject.parseObject(fieldInfo,typeObj);// 获取参数列
        //type判断是对字段的 add增 del删 up改
        if("add".equals(type)){
            bool=tableService.addField(pras);
        }else if("del".equals(type)){
            bool=tableService.delField(pras);

        }else if("up".equals(type)){
            bool=tableService.upField(pras);
        }
        if(bool){
            return new ResponseResult(0, "处理成功");
        }else{
            return new ResponseResult(400, "处理失败");
        }
    }




    @ApiOperation(value = "模版列表", notes = "返回信息 0成功，400失败,500参数错误")
    @RequestMapping(value = "/getTemplateList", method = RequestMethod.GET)
    public ResponseResult getTemplateList() {
        ResponseResult responseResult = new ResponseResult(0, "成功 ", tableService.getTemplateList());
        return responseResult;
    }

    @ApiOperation(value = "已选模版", notes = "返回信息 0成功，400失败,500参数错误")
    @RequestMapping(value = "/getOptionalTemplateList", method = RequestMethod.GET)
    public ResponseResult getOptionalTemplateList() {
        ResponseResult responseResult = new ResponseResult(0, "成功 ", tableService.getOptionalTemplateList());
        return responseResult;
    }

    @ApiOperation(value = "当前节点可选择底层模版", notes = "返回信息 0成功，400失败,500参数错误")
    @RequestMapping(value = "/getSelectTemplateList", method = RequestMethod.GET)
    public ResponseResult getSelectTemplateList(String parentCode) {
        Map<String,String> parmMap=new HashMap<>();
        parmMap.put("parentCode",parentCode);
        ResponseResult responseResult = new ResponseResult(0, "成功 ", tableService.getSelectTemplateList(parmMap));
        return responseResult;
    }

    @ApiOperation(value = "选中后加载旗下实体表", notes = "返回信息 0成功，400失败,500参数错误")
    @RequestMapping(value = "/getSelectTableByClassCode", method = RequestMethod.GET)
    public ResponseResult getSelectTableByClassCode(String classCode) {
        Map<String,String> parmMap=new HashMap<>();
        parmMap.put("classCode",classCode);
        ResponseResult responseResult = new ResponseResult(0, "成功 ", tableService.getSelectTableByClassCode(parmMap));
        return responseResult;
    }

    @ApiOperation(value = "加载实体表字段信息", notes = "返回信息 0成功，400失败,500参数错误")
    @RequestMapping(value = "/getTableByTableCode", method = RequestMethod.GET)
    public ResponseResult getTableByTableCode(String tableCode) {
        Map<String,String> parmMap=new HashMap<>();
        parmMap.put("tableCode",tableCode);
        ResponseResult responseResult = new ResponseResult(0, "成功 ", tableService.getTableInfoByTableCode(parmMap));
        return responseResult;
    }

    @ApiOperation(value = "添加已选模版", notes = "返回信息 0成功，400失败,500参数错误")
    @RequestMapping(value = "/getCreateTemplate", method = RequestMethod.POST)
    public ResponseResult CreateTemplate(String createData) {
        //操作使用fastjson进行字符串对象转换
        List<Template> list=new ArrayList<Template>();
        JSONArray jsonArray= JSONArray.parseArray(createData);
        for(int i=0;i<jsonArray.size();i++){
            JSONObject jsonResult = jsonArray.getJSONObject(i);
            Template templatePOJO=JSONObject.toJavaObject(jsonResult,Template.class);
            list.add(templatePOJO);
        }
        ResponseResult responseResult = null;
        for (Template temp:list) {
            Map<String, String> map = new HashMap<>();
            map.put("classCode", StringUtil.getRandomStr(9));
            map.put("name", temp.getClassTableCode());
            map.put("chineseName", "自定义【" + temp.getName() + "】");
            int result = tableService.createTemplate(map);
            if (result == 1) {
                responseResult = new ResponseResult(0, "成功 ", null);
            } else {
                responseResult = new ResponseResult(400, "失败 ", null);
            }
        }
        return responseResult;
    }

    @ApiOperation(value = "删除已选模版", notes = "返回信息 0成功，400失败,500参数错误")
    @RequestMapping(value = "/delTemplate", method = RequestMethod.POST)
    public ResponseResult delTemplate(String delData) {
        //操作使用fastjson进行字符串对象转换
        List<Template> list=new ArrayList<Template>();
        JSONArray jsonArray= JSONArray.parseArray(delData);
        for(int i=0;i<jsonArray.size();i++){
            JSONObject jsonResult = jsonArray.getJSONObject(i);
            Template templatePOJO=JSONObject.toJavaObject(jsonResult,Template.class);
            list.add(templatePOJO);
        }
        ResponseResult responseResult = null;
        for (Template temp:list) {
            int result = tableService.delTemplate(temp.getClassCode());
            if (result == 1) {
                responseResult = new ResponseResult(0, "成功 ", null);
            } else {
                responseResult = new ResponseResult(400, "失败 ", null);
            }
        }
        return responseResult;
    }

    @ApiOperation(value = "获取字典", notes = "返回信息 0成功，400失败,500参数错误")
    @RequestMapping(value = "/getAllDictionaryData", method = RequestMethod.GET)
    public ResponseResult getAllDictionaryData() {
        List<Map<String,String>> list=classLevelService.getAllDictionaryData();
        if(list!=null&&list.size()>0){
            return  new ResponseResult(0, "成功 ", list);
        }else{
            return  new ResponseResult(400, "失败 ", list);
        }
    }

}
