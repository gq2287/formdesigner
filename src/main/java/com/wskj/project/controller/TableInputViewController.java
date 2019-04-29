package com.wskj.project.controller;

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
        List<Map<String, String>>  mapList=tableInputViewService.getTableInputView(tableCode);
        if(mapList!=null&&mapList.size()>0){
            return new ResponseResult(ResponseResult.OK, "成功", mapList, true);
        }else{
            return new ResponseResult(ResponseResult.OK, "失败 参数内部错误", tableCode, false);
        }
    }
}
