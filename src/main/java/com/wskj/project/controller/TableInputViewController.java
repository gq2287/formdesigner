package com.wskj.project.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.wskj.project.model.ResponseResult;
import com.wskj.project.service.impl.TableInputViewServiceImpl;
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
@Api("表录入视图Controller")
public class TableInputViewController {
    private Logger logger = LoggerFactory.getLogger(TableInputViewController.class);
    @Resource
    private TableInputViewServiceImpl tableInputViewService;

    @ApiOperation(value = "获取表录入视图", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getTableInputView", method = RequestMethod.POST)
    public ResponseResult getTableInputView(@ApiParam(required =true, name = "tableCode", value = "表编号")String tableCode) {
        List<Map<String, Object>>  mapList=tableInputViewService.getTableInputView(tableCode);
        if(mapList!=null&&mapList.size()>0){
            return new ResponseResult(ResponseResult.OK, "成功", mapList, true);
        }else{
            return new ResponseResult(ResponseResult.OK, "失败 参数内部错误", tableCode, false);
        }
    }

    @ApiOperation(value = "获取保存录入视图", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getSaveTableInputView", method = RequestMethod.POST)
    public ResponseResult getSaveTableInputView(@ApiParam(required =true, name = "tableInput", value = "录入字段集合")String tableInput) {
        Type typeObj = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object>  pras= JSONObject.parseObject(tableInput,typeObj);//JSONObject转换map
        List UIList=(JSONArray)pras.get("layout");
        Map<String, Object>  prasUI=new HashMap<>();
        List<Map<String,Object>>  parasUIList=new ArrayList<>();
        String tableCode="";
        if(UIList!=null&&UIList.size()>0){
            for (int i = 0; i <UIList.size() ; i++) {
                prasUI=JSONObject.parseObject(String.valueOf(UIList.get(i)),typeObj);//JSONObject转换map
                if("".equals(tableCode)){
                    tableCode=(String) prasUI.get("TABLECODE");
                }
                parasUIList.add(prasUI);
            }
        }
        boolean  bool=tableInputViewService.saveTableInputView(tableCode,parasUIList);
        if(bool){
            return new ResponseResult(ResponseResult.OK, "成功", bool, true);
        }else{
            return new ResponseResult(ResponseResult.OK, "失败 参数内部错误", bool, false);
        }
    }
}
