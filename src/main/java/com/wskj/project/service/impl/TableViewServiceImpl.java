package com.wskj.project.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.wskj.project.dao.ClassLevelMapper;
import com.wskj.project.dao.TableViewMapper;
import com.wskj.project.model.Tree;
import com.wskj.project.service.TableViewService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.*;

@Service
public class TableViewServiceImpl implements TableViewService {

    @Resource
    private TableViewMapper tableViewMapper;
    @Resource
    private ClassLevelMapper classLevelMapper;
    @Resource
    private ClassLevelServiceImpl classLevelService;

    /**
     * 获取当前实体表列
     *
     * @param tableCode
     * @return
     */
    @Override
    public List<Map<String, Object>> getTableView(String tableCode) {
        List<Map<String, Object>> parms = null;
        try {
            parms = tableViewMapper.getTableView(tableCode);
        } catch (Exception e) {
            System.err.println("查询视图列失败：" + e.getMessage() + "tableCode");
        }
        return parms;
    }

    /**
     * 获取视图树
     *
     * @return
     */
    @Override
    public Tree getTreeMenu() {
        //获取树菜单，根据树菜单获取底层门类获取旗下实体表table
        Map<String, String> parmMap = new HashMap<>();//参数map
        parmMap.put("type", "I");
        parmMap.put("nodeCode", "D_DATA");
        List<Tree> treeList = new ArrayList<>();//节点集合
        Tree rootTree = new Tree();
        rootTree.setId("D_DATA");//编号code
        rootTree.setText("门类信息");//名称
        List<Map<String, String>> classI = classLevelMapper.getClassI(parmMap);//根节点 5棵
        for (int i = 0; i < classI.size(); i++) {
            Map<String, String> classIMap = classI.get(i);
            Tree treeI = new Tree();
            Map<String, String> attrIs = new HashMap<>();// treeI树其他属性
            for (String cI : classIMap.keySet()) {
                attrIs.put(cI, classIMap.get(cI));//当前树全部属性
                if ("NODECODE".equals(cI)) {
                    treeI.setId(classIMap.get("NODECODE"));//获取树编号
                    treeI.setText(classIMap.get("NAME"));//获取树名称
                    List<Map<String, String>> classCL = classLevelMapper.getClassCL(classIMap.get("NODECODE"));// 获取到 treeI 子节点
                    List<Tree> treeCLList = new ArrayList<>();//treeI二级节点集合
                    for (int j = 0; j < classCL.size(); j++) {
                        Tree treeCL = new Tree();//当前树
                        for (String jm : classCL.get(j).keySet()) {
                            treeCL.setText(classCL.get(j).get("NAME"));//获取treeCL树名称
                            treeCL.setId(classCL.get(j).get("NODECODE"));//获取树编号
                            if ("NODECODE".equals(jm)) {
                                List<Tree> newList = classLevelService.getCLBynodeCode(classCL.get(j).get(jm));//获取当前节点nodecode
                                if(newList!=null&&newList.size()>0){
                                    for (int k = 0; k < newList.size(); k++) {
                                        Map<String, String> newMap = (Map<String, String>) newList.get(k).getLi_attr();
                                        for (String et : newMap.keySet()) {
                                            if (newMap.get(et) != null && "C".equals(newMap.get(et))) {
                                                List<Tree> treeELists = new ArrayList<>();//treeE节点集合
                                                List<Map<String, String>> ETreeList = classLevelService.getTableByNodeCode(newMap.get("NODECODE"));//获取旗下实体表
                                                //获取nodecode全部实体
                                                for (int l = 0; l < ETreeList.size(); l++) {
                                                    Tree treeEE = new Tree();
                                                    treeEE.setId(ETreeList.get(l).get("NODECODE"));
                                                    treeEE.setText(ETreeList.get(l).get("NAME"));
                                                    treeEE.setLi_attr(ETreeList.get(l));//获取全部属性
                                                    treeELists.add(treeEE);
                                                }
                                                newList.get(k).setChildren(treeELists);
                                            } else {
                                                continue;
                                            }
                                        }
                                    }
                                    treeCL.setChildren(newList);
                                }else{
                                    if (classCL.get(j).get(jm) != null && "C".equals(classCL.get(j).get("TYPE"))) {
                                        List<Tree> treeELists = new ArrayList<>();//treeE节点集合
                                        List<Map<String, String>> ETreeList = classLevelService.getTableByNodeCode(classCL.get(j).get("NODECODE"));//获取旗下实体表
                                        //获取nodecode全部实体
                                        for (int l = 0; l < ETreeList.size(); l++) {
                                            Tree treeEE = new Tree();
                                            treeEE.setId(ETreeList.get(l).get("NODECODE"));
                                            treeEE.setText(ETreeList.get(l).get("NAME"));
                                            treeEE.setLi_attr(ETreeList.get(l));//获取全部属性
                                            treeELists.add(treeEE);
                                        }
                                        treeCL.setChildren(treeELists);
                                    } else {
                                        continue;
                                    }
                                }
                            } else {
                                continue;
                            }
                        }
                        treeCL.setLi_attr(classCL.get(j));
                        treeCLList.add(treeCL);
                        treeI.setChildren(treeCLList);
                    }
                }
            }
            treeI.setLi_attr(attrIs);
            treeList.add(treeI);
        }
        parmMap.put("type", "I");
        parmMap.put("nodeCode", "D_DATA");
        rootTree.setLi_attr(parmMap);
        rootTree.setChildren(treeList);//节点集合放入根节点
        return rootTree;
    }

    /**
     * 修改添加视图
     * @param parms
     * @return
     */
    @Override
    public Boolean upTableViewSelect(List<Object> parms) {
        boolean bool=true;
        Type typeObj = new TypeToken<Map<String,String>>() {}.getType();
        try {
            if(parms!=null&&parms.size()>0){
                for (int i = 0; i <parms.size() ; i++) {
                    Map<String,String>  pras= JSONObject.parseObject(String.valueOf(parms.get(i)),typeObj);//JSONObject转换map
                    System.err.println("----------"+pras);
                    String tableCode=pras.get("TABLECODE");
                    String listCode=pras.get("LISTCODE");//如果存在就是已存在的视图列
                    if(listCode!=null&&!"".equals(listCode)){
                        pras.put("SERIAL",i+"");//序号
                        int result=tableViewMapper.upTableViewSelect(pras);//更新视图列
                        if(result>0){
                            System.out.println("修改添加显示卡片列成功"+pras);
                        }
                    }else{
                        int result=tableViewMapper.upTableColumnSelect(pras);//修改纪录表
                        if(result>0){
                            System.out.println("修改纪录表列成功"+pras);
                        }
                        pras.put("SERIAL",i+"");//序号
                        pras.put("LISTCODE", String.valueOf((new Date()).getTime()) + (int)(100.0D + Math.random() * 1000.0D));
                        result=tableViewMapper.addTableViewColumn(pras);//添加视图列
                        if(result>0){
                            System.out.println("成功添加视图列"+pras);
                        }

                    }
                }
            }
        }catch (Exception e){
            bool=false;
            System.out.println("添加显示卡片列失败"+parms+"---"+e.getMessage());
        }
        return bool;
    }

    /**
     * 删除试图列
     * @param listCodes 唯一编号
     * @return
     */
    @Override
    public Boolean delTableViewColumn(List<String> listCodes) {
        Boolean bool=true;
        try {
           if(listCodes!=null&&listCodes.size()>0){
               for (int i = 0; i <listCodes.size() ; i++) {
                   int result=tableViewMapper.delTableViewColumnByListCode(listCodes.get(i));
                   if(result>0){
                       System.out.println("** "+i+" **删除视图列数据成功ListCode:"+listCodes.get(i));
                   }
               }
           }
        }catch (Exception e){
            bool=false;
            System.out.println("删除添加显示卡片列失败"+listCodes+"---"+e.getMessage());
        }
        return bool;
    }


}
