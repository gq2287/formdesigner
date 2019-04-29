package com.wskj.project.service;

import java.util.List;
import java.util.Map;

public interface TableInputViewService {
    //获取录入视图列表
    List<Map<String, Object>> getTableInputView(String tableCode);
    //获取录入视图列表
    Boolean saveTableInputView(Map<String,Object> map);
}
