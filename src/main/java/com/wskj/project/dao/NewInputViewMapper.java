package com.wskj.project.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface NewInputViewMapper {
    //获取录入视图列表
    List<Map<String,String>> getAllInputView(@Param("tableCode") String tableCode);
    //添加视图录入列
    Boolean addInputViewColumn(Map<String,String> parms);
    //删除全部录入视图
    Integer delAllInputViewByTableCode(@Param("tableCode") String tableCode);
    Boolean addInputView(Map<String,String> parms);//添加模版录入列样式
}
