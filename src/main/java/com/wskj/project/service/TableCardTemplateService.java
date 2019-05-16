package com.wskj.project.service;

import java.util.List;
import java.util.Map;

public interface TableCardTemplateService {
    List<Map<String,String>> getAllTemplates();

    //添加录入模版
    boolean addTableCardTemplate(Map<String,String> parms);
}
