package com.wskj.project.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.wskj.project.model.ResponseResult;
import com.wskj.project.service.impl.TableInputViewServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.Type;
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
    public ResponseResult getTableInputView(String tableCode) {
        logger.info("获取表录入视图getTableInputView--",tableCode);
        List<Map<String, Object>>  mapList=tableInputViewService.getTableInputView(tableCode);
        if(mapList!=null&&mapList.size()>0){
            return new ResponseResult(ResponseResult.OK, "成功", mapList, true);
        }else{
            return new ResponseResult(ResponseResult.OK, "失败 参数内部错误", tableCode, false);
        }
    }

    @ApiOperation(value = "获取保存录入视图", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getSaveTableInputView", method = RequestMethod.POST)
    public ResponseResult getSaveTableInputView(String tableInput) {
        logger.info("获取保存录入视图---getSaveTableInputView--",tableInput);
        Type typeObj = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object>  pras= JSONObject.parseObject(tableInput,typeObj);//JSONObject转换map
        boolean  bool=tableInputViewService.saveTableInputView(pras);
        if(bool){
            return new ResponseResult(ResponseResult.OK, "成功", bool, true);
        }else{
            return new ResponseResult(ResponseResult.OK, "失败 参数内部错误", bool, false);
        }
    }
}
