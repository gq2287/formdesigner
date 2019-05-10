package com.wskj.project.service;

import java.util.List;
import java.util.Map;

public interface NewInputViewService {
    //获取录入视图列表
    Map<String,Object> getInputView(String tableCode);
    //保存录入视图列表
    Boolean saveInputView(String tableCode,List<Map<String,Object>> parasUIList);

    Boolean delInputView(String tableCode);

}
