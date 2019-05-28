package com.wskj.project.service;

import java.util.List;
import java.util.Map;

public interface NewInputViewService {
    //获取录入视图列表
    Map<String,Object> getInputView(String tableCode);
    //保存录入视图列表
    Boolean addInputView(String tableCode,List<Map<String,Object>> parasUIList,String nodeCode);

    int delInputView(String tableCode);

    List<Object> getTemplateViewByNodeCode(String nodeCode);


    boolean addTemplatInput(Map<String,String> stringMap);

}
