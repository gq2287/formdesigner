package com.wskj.project.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TableInputViewMapper {
    //获取录入视图列表
    List<Map<String,String>> getTableInputView(@Param("tableCode") String tableCode);
    //获取最大的序号
    Integer getLoadNOMax(@Param("tableCode") String tableCode);
    //添加视图录入列
    Integer addTableInputViewColumn(Map<String,Object> parms);
    //默认添加视图录入列
    Integer addDefaultTableInputViewColumn(Map<String,String> parms);

    //修改录入列
    Integer upTableInputColumnSelect(Map<String,String> parms);
    //删除录入列
    Boolean delTableInputViewByTableCode(@Param("tableCode") String tableCode);

    //添加表索引
    Boolean addTableIndex(Map<String,String> parms);
    //删除表索引
    Boolean delTableIndex(@Param("tableCode") String tableCode);

    String getEntityTableColumnByTag(@Param("tag") String tag,@Param("tableCode") String tableCode);

    //获取录入视图列表字段名
    List<Map<String,Object>> getInputColumnByTableCode(@Param("tableCode") String tableCode);

}
