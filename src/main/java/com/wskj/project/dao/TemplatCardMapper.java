package com.wskj.project.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface TemplatCardMapper {
    Map<String,String> getTemplateCardByTableCode(@Param("tableCode")String tableCode);
    Boolean addTemplateInputCard(Map<String,String> map);//添加模版表关联
    Map<String,String> getTemplateEntityCardByTableCode(@Param("tableCode")String tableCode);

    //删除模版关联信息列
    Boolean delTemplatColumns(@Param("tableCode")String tableCode);
}
