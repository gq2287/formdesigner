package com.wskj.project.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TableInputViewMapper {
    //获取录入视图列表
    List<Map<String,Object>> getTableInputView(@Param("tableCode") String tableCode);

}
