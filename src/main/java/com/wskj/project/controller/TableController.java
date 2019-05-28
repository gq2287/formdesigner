package com.wskj.project.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.wskj.project.model.ResponseResult;
import com.wskj.project.model.Template;
import com.wskj.project.service.impl.TableServiceImpl;
import com.wskj.project.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api("表菜单Controller")
public class TableController {
    private Logger logger = LoggerFactory.getLogger(TableController.class);
    @Resource
    private TableServiceImpl tableService;

    @ApiOperation(value = "获取要修改表的详细信息", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getUpTree", method = RequestMethod.POST)
    public ResponseResult getUpTreeByAttrs(@ApiParam(required =true, name = "attrs", value = "属性")String attrs) {
        Type typeObj = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object>  pras=JSONObject.parseObject(attrs,typeObj);//JSONObject转换map
        Map<String,Object> mapList=tableService.getUpTreeByAttrs(pras);
        if(mapList!=null&&mapList.size()>0){
            return new ResponseResult(ResponseResult.OK, "成功 ",mapList,true);
        }else {
            return new ResponseResult(ResponseResult.OK, "失败 ",mapList,false);
        }
    }

    @ApiOperation(value = "当前表是否纯在数据", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getIsOkUpDataByTableName", method = RequestMethod.POST)
    public ResponseResult getIsOkUpDataByTableName(@ApiParam(required =true, name = "tableName", value = "表名")String tableName) {
//        boolean bool=tableService.getIsOkUpDataByTableName(tableName);

//        if(bool){
//            return new ResponseResult(ResponseResult.OK, "可以修改成功",true);
//        }else {
//            return new ResponseResult(ResponseResult.OK, "无法修改",true);
//        }

        return new ResponseResult(ResponseResult.OK, "可以修改成功",true);
    }

    @ApiOperation(value = "type(add增 del删 up改) 字段", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getModifyFieldInfo", method = RequestMethod.POST)
    public ResponseResult getModifyFieldInfo(@ApiParam(required =true, name = "type", value = "type判断是对字段的 add增 del删 up改")String type,
                                             @ApiParam(required =true, name = "fieldInfo", value = "获取参数列")String fieldInfo) {
//        logger.info("type(add增 del删 up改) 字段--getModifyFieldInfo--参数--{}",type+"--"+fieldInfo);
        Boolean bool=null;
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
            return new ResponseResult(ResponseResult.OK, "处理成功",true);
        }else{
            return new ResponseResult(ResponseResult.OK, "存在数据无法操作",false);
        }
    }

    @ApiOperation(value = "修改表关系字段", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getModifyTableRelation", method = RequestMethod.POST)
    public ResponseResult getModifyTableRelation( @ApiParam(required =true, name = "fieldRelation", value = "字段关系")String fieldRelation) {
//        logger.info("修改表关系字段---getModifyTableRelation--参数--{}",fieldRelation);
        Boolean bool=null;
        Type typeObj = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object>  pras=JSONObject.parseObject(fieldRelation,typeObj);// 获取参数列
        bool=tableService.upFieldTableRelation(pras);
        if(bool){
            return new ResponseResult(ResponseResult.OK, "处理成功",true);
        }else{
            return new ResponseResult(ResponseResult.OK, "存在数据无法操作",false);
        }
    }

    @ApiOperation(value = "删除表字段关系", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/delFieldTableRelation", method = RequestMethod.POST)
    public ResponseResult delFieldTableRelation(@ApiParam(required =true, name = "relationCode", value = "唯一编号")String relationCode) {
//        logger.info("删除表字段关系---delFieldTableRelation--参数--{}",relationCode);
        Boolean bool=null;
        bool=tableService.delFieldTableRelation(relationCode);
        if(bool){
            return new ResponseResult(ResponseResult.OK, "处理成功",true);
        }else{
            return new ResponseResult(ResponseResult.OK, "存在数据无法操作",false);
        }
    }

    @ApiOperation(value = "修改描述表信息字段", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getModifyTableDescription", method = RequestMethod.POST)
    public ResponseResult getModifyTableDescription(@ApiParam(required =true, name = "fieldDescription", value = "字段描述")String fieldDescription) {
        Boolean bool=null;
        Type typeObj = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String>  pras=JSONObject.parseObject(fieldDescription,typeObj);// 获取参数列
        bool=tableService.upFieldTableDescription(pras);
        if(bool){
            return new ResponseResult(ResponseResult.OK, "处理成功",true);
        }else{
            return new ResponseResult(ResponseResult.OK, "存在数据无法操作",false);
        }
    }


    @ApiOperation(value = "添加字段关系", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/addTableRelation", method = RequestMethod.POST)
    public ResponseResult addTableRelation(@ApiParam(required =true, name = "fieldInfo", value = "字段对象")String fieldInfo) {
        Type typeObj = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String>  pras=JSONObject.parseObject(fieldInfo,typeObj);// 获取参数列
        try {
            tableService.addTableRelation(pras);
            return new ResponseResult(ResponseResult.OK, "处理成功",true);
        }catch (Exception e){
            return new ResponseResult(ResponseResult.OK, e.getMessage(),false);
        }
    }






    @ApiOperation(value = "模版列表", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getTemplateList", method = RequestMethod.GET)
    public ResponseResult getTemplateList() {
        List<Map<String,String>> pram=tableService.getTemplateList();
        ResponseResult responseResult = new ResponseResult(ResponseResult.OK, "成功 ",pram ,true);
        return responseResult;
    }

    @ApiOperation(value = "已选模版", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getOptionalTemplateList", method = RequestMethod.GET)
    public ResponseResult getOptionalTemplateList() {
        List<Map<String,String>> pram=tableService.getOptionalTemplateList();
        ResponseResult responseResult = new ResponseResult(ResponseResult.OK, "成功 ",pram ,true);
        return responseResult;
    }

    @ApiOperation(value = "当前节点可选择底层模版", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getSelectTemplateList", method = RequestMethod.GET)
    public ResponseResult getSelectTemplateList(@ApiParam(required =true, name = "parentCode", value = "父编号")String parentCode) {
        Map<String,String> parmMap=new HashMap<>();
        parmMap.put("parentCode",parentCode);
        ResponseResult responseResult = new ResponseResult(ResponseResult.OK, "成功", tableService.getSelectTemplateList(parmMap),true);
        return responseResult;
    }

    @ApiOperation(value = "选中后加载旗下实体表", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getSelectTableByClassCode", method = RequestMethod.GET)
    public ResponseResult getSelectTableByClassCode(@ApiParam(required =true, name = "classCode", value = "门类编号")String classCode) {
        Map<String,String> parmMap=new HashMap<>();
        parmMap.put("classCode",classCode);
        ResponseResult responseResult = new ResponseResult(ResponseResult.OK, "成功", tableService.getSelectTableByClassCode(parmMap),true);
        return responseResult;
    }

    @ApiOperation(value = "加载实体表字段信息", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getTableByTableCode", method = RequestMethod.GET)
    public ResponseResult getTableByTableCode(@ApiParam(required =true, name = "tableCode", value = "表编号")String tableCode) {
        Map<String,String> parmMap=new HashMap<>();
        parmMap.put("tableCode",tableCode);
        ResponseResult responseResult = new ResponseResult(ResponseResult.OK, "成功 ", tableService.getTableInfoByTableCode(parmMap),true);
        return responseResult;
    }

    @ApiOperation(value = "添加已选模版", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getCreateTemplate", method = RequestMethod.POST)
    public ResponseResult CreateTemplate(@ApiParam(required =true, name = "createData", value = "模版对象")String createData) {
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
            map.put("classCode", StringUtil.getRandomStr(6));
            map.put("name", temp.getClassTableCode());
            map.put("classTableCode", temp.getClassTableCode());
            map.put("chineseName", "自定义【" + temp.getName() + "】");
            map.put("parentCode",temp.getParentCode() );
            map.put("nodeCode",temp.getNodeCode() );
            boolean result = tableService.addTemplate(map);
            if (result) {
                responseResult = new ResponseResult(ResponseResult.OK, "成功",true);
            } else {
                responseResult = new ResponseResult(ResponseResult.OK, "处理失败",false);
            }
        }
        return responseResult;
    }

    @ApiOperation(value = "删除已选模版", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/delTemplate", method = RequestMethod.POST)
    public ResponseResult delTemplate(@ApiParam(required =true, name = "delData", value = "模版对象")String delData) {
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
            boolean result = tableService.delTemplate(temp.getClassCode(),temp.getTableCode());
            if (result) {
                responseResult = new ResponseResult(ResponseResult.OK, "成功",true);
            } else {
                responseResult = new ResponseResult(ResponseResult.OK, "处理失败",false);
            }
        }
        return responseResult;
    }


}
