package com.wskj.project.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.wskj.project.model.ResponseResult;
import com.wskj.project.service.impl.NewInputViewServiceImpl;
import com.wskj.project.service.impl.TableCardTemplateServiceImpl;
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
@Api("设置录入模版Controller")
public class TableCardTemplateController {
    private Logger logger = LoggerFactory.getLogger(TableCardTemplateController.class);
    @Resource
    private TableCardTemplateServiceImpl tableCardTemplateService;

    @Resource
    private NewInputViewServiceImpl newInputViewService;


    @ApiOperation(value = "添加录入模版", notes = "返回信息 0成功，400失败")

    @RequestMapping(value = "/addCardTemplate", method = RequestMethod.POST)
    public ResponseResult addCardTemplate(@ApiParam(required =true, name = "chinaName", value = "模版中文名称")String chinaName,
                                          @ApiParam(required =true, name = "tableInput", value = "模版字段集合") String tableInput) {
        //模版唯一编号
        String id= StringUtil.getUuid();
        //获取字段
        List<Map<String,Object>> mapList=new ArrayList<>();//全部字段
        Type typeObj = new TypeToken<List<Object>>() {}.getType();
        List<Object> pras = JSONObject.parseObject(tableInput,typeObj);//JSONObject转换map
        Type typeMap = new TypeToken<Map<String,Object>>() {}.getType();
        if(pras!=null&&pras.size()>0){
            for (int i = 0; i < pras.size(); i++) {
                Map<String,Object> map = JSONObject.parseObject(String.valueOf(pras.get(i)),typeMap);
                mapList.add(map);
            }
        }
        if(chinaName!=null){
            Map<String,String> parms=new HashMap<>();
            parms.put("id",StringUtil.getUuid());
            parms.put("tableCode",id);
            parms.put("chinaName",chinaName);
            boolean bool=tableCardTemplateService.addTableCardTemplate(parms);
            if(bool){
                bool=newInputViewService.saveInputView(id,mapList,null);//保存模版视图

                return new ResponseResult(ResponseResult.OK, "添加录入模版成功", bool);
            }else{
                return new ResponseResult(ResponseResult.OK, "添加录入模版失败", bool);
            }
        }else{
            return new ResponseResult(ResponseResult.OK, "添加录入模版失败,提交数据不正确", false);
        }
    }

    @ApiOperation(value = "获取录入模版", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getCardTemplateList", method = RequestMethod.GET)
    public ResponseResult getCardTemplateList() {
        List<Map<String,String>> templates=tableCardTemplateService.getAllTemplates();
        if(templates!=null&&templates.size()>0){
            return new ResponseResult(ResponseResult.OK, "获取录入模版成功", templates,true);
        }else{
            return new ResponseResult(ResponseResult.OK, "暂无录入模版数据", false);
        }
    }

    @ApiOperation(value = "删除录入模版", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/delCardTemplate", method = RequestMethod.GET)
    public ResponseResult delCardTemplate(String id) {
        boolean bool=tableCardTemplateService.delTableCardTemplateById(id);
        if(bool){

            return new ResponseResult(ResponseResult.OK, "删除录入模版成功",true);
        }else{
            return new ResponseResult(ResponseResult.OK, "删除录入模版失败", false);
        }
    }
}
