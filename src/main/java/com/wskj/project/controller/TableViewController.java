package com.wskj.project.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.wskj.project.model.ResponseResult;
import com.wskj.project.model.Tree;
import com.wskj.project.service.impl.TableViewServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@RestController
@Api("表视图Controller")
public class TableViewController {
    private Logger logger = LoggerFactory.getLogger(TableViewController.class);
    @Resource
    private TableViewServiceImpl tableViewService;

    @ApiOperation(value = "获取视图节点", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getTableView", method = RequestMethod.POST)
    public ResponseResult getTableView(String tableCode) {
//        logger.info("获取视图节点---getTableView--参数--{}",tableCode);
        if (tableCode != null) {
            List<Map<String, Object>> parms = tableViewService.getTableView(tableCode);
            if (parms != null && parms.size() > 0) {
                return new ResponseResult(ResponseResult.OK, "查询成功 ", parms, true);
            } else {
                return new ResponseResult(ResponseResult.OK, "查询失败,该参数无字段列 ", "参数-" + tableCode, false);
            }
        } else {
            return new ResponseResult(ResponseResult.OK, "失败，参数无效 ", "参数-" + tableCode, false);
        }
    }


    @ApiOperation(value = "获取视图树", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getTableViewTree", method = RequestMethod.POST)
    public ResponseResult getTableViewTree() {
        Tree tree=tableViewService.getTreeMenu();
//        logger.info("获取视图树---getTableViewTree--参数--{}",tree);
        return new ResponseResult(ResponseResult.OK, "成功", tree, true);
    }

    @ApiOperation(value = "添加视图列表", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/getUpTableViewSelect", method = RequestMethod.POST)
    public ResponseResult getUpTableViewSelect(String parms) {
//        logger.info("添加视图列表---getUpTableViewSelect--参数--{}",parms);
        Type typeObj = new TypeToken<List<Object>>() {}.getType();
        List<Object>  pras= JSONObject.parseObject(parms,typeObj);//JSONObject转换map
        boolean bool=tableViewService.upTableViewSelect(pras);
        if(bool){
            return new ResponseResult(ResponseResult.OK, "成功", true);
        }else {
            return new ResponseResult(ResponseResult.OK, "失败",parms, false);
        }
    }

    @ApiOperation(value = "删处视图列表", notes = "返回信息 0成功，400失败 ")
    @RequestMapping(value = "/delTableViewByListCode", method = RequestMethod.POST)
    public ResponseResult delTableViewByListCode(@RequestParam(required = false, value = "parms[]") List<String> parms) {
//        logger.info("删处视图列表---delTableViewByListCode--参数--{}",parms);
        boolean bool=tableViewService.delTableViewColumn(parms);
        if(bool){
            return new ResponseResult(ResponseResult.OK, "成功", true);
        }else {
            return new ResponseResult(ResponseResult.OK, "失败",parms, false);
        }
    }

}
