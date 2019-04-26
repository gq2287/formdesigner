package com.wskj.project.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TableViewMapper {
    //获取视图列表
    List<Map<String,Object>> getTableView(@Param("tableCode") String tableCode);
    //修改视图列表
    Integer upTableViewSelect(Map<String,String> parms);
    //获取最大的序号
    Integer getSerialMax(@Param("tableCode") String tableCode);

    //添加视图列
    Integer addTableViewColumn(Map<String,String> parms);
    //修改记录表是否展示
    Integer upTableColumnSelect(Map<String,String> parms);

    //删除视图列
    Integer delTableViewColumnByListCode(@Param("listCode") String listCode);
}
