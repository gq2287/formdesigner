package com.wskj.project.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TableMapper {
    //T 模版列表最大序号
    int getMaxTemplateSerial();
    //T 模版列表
    List<Map<String,String>> getTemplateList();
    //添加 已选模版数据
    Integer addTemplate(Map<String,String> parmMap);
    //删除已选模版数据
    Integer delTemplate(@Param("classCode") String classCode);
    //已选择模版列表
    List<Map<String,String>> getOptionalTemplateList();
    //当前可选择底层门类
    List<Map<String,Object>> getSelectTemplateList(Map<String,String> map);

    //当前可选择底层门类选中后查询实体表
    List<Map<String,String>> getSelectTableByClassCode(Map<String,String> map);
    //获取创建实体表字段信息
    List<Map<String,String>> getTableInfoByTableCode(Map<String,String> map);

    //数据表基本描述
    Integer addTableDescription(Map<String,String> parmMap);
    //表字段关系
    Integer addTableRelation(Map<String,String> parmMap);
    //添加门类树节点信息 实体类表类型为E C
    Integer addTableTreeInfo(Map<String,String> parmMap);
    //创建实体数据表 <>
    Integer addTable(Map<String,String> parmMap);
    //创建用户角色数据表 R角色 U用户
    Integer addSystemUseRRoleTable(Map<String,String> parmMap);
    //添加默认视图列表信息 VISIBLE为T的全部添加
    Integer addOneColumn(Map<String,String> parmMap);

    //根据表描述里的tableCode新建表字段属性 TableColumnDescription
    Integer addTableColumnDescription(@Param("sql")StringBuffer sql);

    //查询表描述
    List<Map<String,String>> getTableByTableCode(@Param("tableCode")String tableCode);
//    实体删除表
    Integer delTableByTableName(@Param("tableName")String tableName);
    //和表描述
    Integer delTableDescriptionByTableCode(@Param("tableCode")String tableCode);
    //权限表
    Integer delSystemUseRRoleTableByTableName(@Param("tableName")String tableName);
    //字段纪录表
    Integer delTableColumnDescription(@Param("tableCode")String tableCode);


    //获取实体表详情
    List<Map<String,Object>> getEntityTableInfo(@Param("tableCode")String tableCode);
    //获取实体表字段
    List<Map<String,Object>> getEntityTableColumn(@Param("tableCode")String tableCode);

    //获取实体表字段
    List<Map<String,Object>> getEntityTableColumnByVisible(@Param("tableCode")String tableCode);
    //获取实体表关系
    List<Map<String,Object>> getEntityTableRelation(@Param("tableName")String tableName);

    //当前可选择底层门类 getSelectTemplateByDataType
    List<Map<String,Object>> getSelectTemplateByDataType(Map<String,Object> parmMap);


    //判断要修改的表是否是空表
    Integer getIsOkUpDataByTableName(@Param("tableName")String tableName);



    //添加表新字段
    Integer addField(Map<String,Object> field);

    //修改描述表字段
    Boolean upFieldTableDescription(Map<String,String> field);
    //修改表新字段
    Integer upFieldTableColumnDescription(Map<String,String> field);
    //修改实体表
    Integer upFieldEntityTable(Map<String,String> field);
    //修改表字段关系
    Integer upFieldTableRelation(Map<String,Object> field);
    //删除表字段描述
    Integer delFieldTableColumnDescription(Map<String,Object> field);
    //删除视图表字段
    Integer delOneColumn(Map<String,Object> field);
    //删除表字段关系
    Integer delFieldTableRelation(Map<String,Object> field);
    //删除关系
    Integer delFieldRelation(@Param("relationCode")String relationCode);
    //删除实体表字段
    Integer delFieldTable(Map<String,Object> field);




    //添加模版旗下的实体表到classleve内
    Boolean addClassLevel(Map<String,String> map);
//   添加模版字段纪录
    Boolean addClassColumnDescription(Map<String,String> map);
//    实体表字段纪录
    List<Map<String,String>> getTableColumnInfoByTableCode(Map<String,String> map);
//    删除纪录
    Boolean delClassColumnDes(@Param("tableCode")String tableCode);

    //删除等级
    Boolean delClassLeveByClassCode(@Param("classCode")String classCode);
//    添加T模版
    Boolean addTableTempT(Map<String,String> map);
}
