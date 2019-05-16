package com.wskj.project.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.wskj.project.model.ResponseResult;
import com.wskj.project.service.impl.NewInputViewServiceImpl;
import com.wskj.project.service.impl.TableServiceImpl;
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
import java.util.List;
import java.util.Map;

@RestController
@Api("表录入视图Controller")
public class NewInputViewController {
    private Logger logger = LoggerFactory.getLogger(TableInputViewController.class);

    @Resource
    private NewInputViewServiceImpl newInputViewService;
    @Resource
    private TableServiceImpl tableService;

    @ApiOperation(value = "获取表录入视图", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getInputView", method = RequestMethod.POST)
    public ResponseResult getInputView(@ApiParam(required =true, name = "tableCode", value = "表编号")String tableCode) {
        Map<String,Object>  mapList= newInputViewService.getInputView(tableCode);
        if(mapList!=null&&mapList.size()>0){
            return new ResponseResult(ResponseResult.OK, "成功", mapList, true);
        }else{
            return new ResponseResult(ResponseResult.OK, "失败 参数内部错误", tableCode, false);
        }
    }

    @ApiOperation(value = "获取保存录入视图", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getSaveInputView", method = RequestMethod.POST)
    public ResponseResult getSaveInputView(@ApiParam(required =true, name = "tableCode", value = "表编号")String tableCode,
                                           @ApiParam(required =true, name = "tableInput", value = "录入样式字段集合")String tableInput) {
        List<Map<String,Object>> mapList=new ArrayList<>();
        Type typeObj = new TypeToken<List<Object>>() {}.getType();
        List<Object> pras = JSONObject.parseObject(tableInput,typeObj);//JSONObject转换map
        Type typeMap = new TypeToken<Map<String,Object>>() {}.getType();
        if(pras!=null&&pras.size()>0){
            for (int i = 0; i < pras.size(); i++) {
                Map<String,Object> map = JSONObject.parseObject(String.valueOf(pras.get(i)),typeMap);
                mapList.add(map);
            }
        }else {//说明全部删除了
            int result=newInputViewService.delInputView(tableCode);//删除
            return new ResponseResult(ResponseResult.OK, "成功", true, true);
        }
        int rr=newInputViewService.delInputView(tableCode);//删除
        if(rr>=0){
            Boolean bool=newInputViewService.saveInputView(tableCode,mapList,null);//保存视图
            if(bool){
                return new ResponseResult(ResponseResult.OK, "成功", bool, true);
            }else{
                return new ResponseResult(ResponseResult.OK, "失败 参数内部错误", bool, false);
            }
        }else {
            return new ResponseResult(ResponseResult.OK, "失败 参数内部错误", false, false);
        }
    }

    @ApiOperation(value = "获取模版", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getTemplateViewByNodeCode", method = RequestMethod.POST)
    public ResponseResult getTemplateViewByNodeCode( @ApiParam(required =true, name = "nodeCode", value = "唯一编号")String nodeCode){
        List<Object> lists=newInputViewService.getTemplateViewByNodeCode(nodeCode);
        if(lists!=null&&lists.size()>0){
            return new ResponseResult(ResponseResult.OK, "成功", lists, true);
        }else{
            return new ResponseResult(ResponseResult.OK, "成功", false, false);
        }
    }
}
