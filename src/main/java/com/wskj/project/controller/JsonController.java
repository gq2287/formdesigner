package com.wskj.project.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Api("Json格式转换Controller")
public class JsonController {

    @ApiOperation(value = "跳转JSON转换页面")
    @RequestMapping("/getJsonHtml")
    public String getJsonHtml(){
        return "index";
    }

}
