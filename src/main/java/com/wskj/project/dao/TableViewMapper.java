package com.wskj.project.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TableViewMapper {
    //获取视图列表
    List<Map<String,Object>> getTableView(@Param("tableCode") String tableCode);
    //修改视图列表
    Integer upTableViewSelect(Map<String,String> parms);
}
