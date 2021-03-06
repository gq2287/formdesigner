package com.wskj.project.service;

import java.util.List;
import java.util.Map;

public interface TableService {
    //T 模版列表
    List<Map<String,String>> getTemplateList();

    //已选择模版列表
    List<Map<String,String>> getOptionalTemplateList();

    //添加可选模版列表
    Boolean addTemplate(Map<String,String> parmMap);

    //删除已选模版列表
    Boolean delTemplate(String classCode,String tableCode);

    //当前可选择底层门类
    List<Map<String,Object>> getSelectTemplateList(Map<String,String> map);

    //当前可选择底层门类选中后查询实体表
    List<Map<String,String>> getSelectTableByClassCode(Map<String,String> map);

    //获取创建实体表字段信息
    List<Map<String,String>> getTableInfoByTableCode(Map<String,String> map);

    //数据表基本描述
    Boolean addTableDescription(Map<String,Object> parmMap);

    //获取要修改的信息
    Map<String,Object> getUpTreeByAttrs(Map<String,Object> parmMap);

    //判断修改表是否有数据
    Boolean getIsOkUpDataByTableName(String tableName);

    //添加表关系
    void addTableRelation(Map<String,String> tableRelationMap);
    //添加表新字段
    Boolean addField(Map<String,Object> field);
    //添加描述表信息
    Boolean upFieldTableDescription(Map<String,String> field);
    //修改表关系
    Boolean upFieldTableRelation(Map<String,Object> fieldRelation);
    //删除表新字段
    Boolean delField(Map<String,Object> field);
    //删除表关系
    Boolean delFieldTableRelation(String relationCode);
    //修改表新字段
    Boolean upField(Map<String,Object> field);

}
