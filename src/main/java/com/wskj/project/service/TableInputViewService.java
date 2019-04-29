package com.wskj.project.service;

import com.wskj.project.model.UIBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TableInputViewService {


    //获取录入视图列表
    List<Map<String, String>> getTableInputView(@Param("tableCode") String tableCode);
}
