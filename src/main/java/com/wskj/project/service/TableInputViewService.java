package com.wskj.project.service;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TableInputViewService {


    //获取录入视图列表
    List<Map<String,Object>> getTableInputView(@Param("tableCode") String tableCode);
}
