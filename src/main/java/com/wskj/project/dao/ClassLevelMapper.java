package com.wskj.project.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 模板门类层级结构表
 */
public interface ClassLevelMapper {
    //查询门类顶级节点
    List<Map<String,String>> getClassITop();
    //I，目录树根节点
    List<Map<String,String>> getClassI(Map<String,String> map);

    //C，底层门类，中间门类
    List<Map<String,String>> getClassCL(@Param("nodeCode") String nodeCode);
    //获取底层门类实体表
//    List<Map<String,String>> getClassEByNodeCode(@Param("nodeCode") String nodeCode);
    //C，底层门类，中间门类 数量
    Integer getClassCLCount(@Param("nodeCode") String nodeCode);

    //删除中间门类 底层门类
    Integer delTreeL(Map<String,String> parmMap);
    Integer delTreeC(Map<String,String> parmMap);
    Integer delTreeE(Map<String,String> parmMap);


    //添加 中间门类 底层门类
    Integer addTreeLC(Map<String,String> parmMap);

    //获取数据字典表
    List<Map<String,String>> getAllDictionaryData();

    //根据父节点nodeCode获取实体表数据
    List<Map<String,String>> getTableByNodeCode(@Param("nodeCode") String nodeCode);


    //修改节点序号和名称
    Integer upTreeNameAndSerial(Map<String,String> parmMap);


    //修改父级节点
    Boolean upParentCode(@Param("parentCode") String parentCode,@Param("nodeCode") String nodeCode);

    Boolean upNameByNodeCode(Map<String,String> parmMap);
}
