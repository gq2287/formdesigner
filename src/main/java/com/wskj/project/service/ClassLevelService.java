package com.wskj.project.service;

import com.wskj.project.model.Tree;

import java.util.List;
import java.util.Map;

public interface ClassLevelService {
    /**
     * 树菜单
     * @return
     */
    Tree getTreeMenu();
    /**
     * 添加树新节点
     * @return
     */
    int createTreeL(Map<String,Object> parmMap);
    /**
     * 删除树节点
     * @return
     */
    String delTreeLC(Map<String,String> parmMap);
    /**
     * 获取数据字典表
     * @return
     */
    List<Map<String,String>> getAllDictionaryData();

    Boolean upTreeNameAndSerial(Map<String,String> parmMap);

    /**
     * 修改当前拖动节点父级 parentCode
     */
    Boolean upDragDropNodeByparentCode(String parentCode,String nodeCode);

}
