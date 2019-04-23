package com.wskj.project.controller;

import com.wskj.project.model.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api("表视图Controller")
public class TableViewController {
    private Logger logger = LoggerFactory.getLogger(TableViewController.class);

    @ApiOperation(value = "获取视图节点", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getTableView", method = RequestMethod.POST)
    public ResponseResult getTableView() {

        return new ResponseResult(ResponseResult.OK, "失败 ");
    }
}
