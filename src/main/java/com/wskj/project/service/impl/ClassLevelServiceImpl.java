package com.wskj.project.service.impl;

import com.wskj.project.dao.ClassLevelMapper;
import com.wskj.project.dao.NewInputViewMapper;
import com.wskj.project.dao.TableInputViewMapper;
import com.wskj.project.dao.TableMapper;
import com.wskj.project.model.Tree;
import com.wskj.project.service.ClassLevelService;
import com.wskj.project.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClassLevelServiceImpl implements ClassLevelService {
    @Resource
    private ClassLevelMapper classLevelMapper;
    @Resource
    private TableMapper tableMapper;

    @Resource
    private TableServiceImpl tableService;
    @Resource
    private TableInputViewMapper tableInputViewMapper;
    @Resource
    private NewInputViewMapper newInputViewMapper;


    @Override
    public Tree getTreeMenu() {
        Map<String, String> parmMap = new HashMap<>();//参数map
        parmMap.put("type", "I");
        parmMap.put("nodeCode", "D_DATA");
        List<Tree> treeList=new ArrayList<>();//节点集合
        Tree rootTree = new Tree();
        rootTree.setId("D_DATA");//编号code
        rootTree.setText("门类信息");//名称
        List<Map<String, String>> classI = classLevelMapper.getClassI(parmMap);//根节点 5棵
        for (int i = 0; i < classI.size(); i++) {
            Map<String, String> classIMap = classI.get(i);
            Tree treeI=new Tree();
            Map<String,String> attrIs=new HashMap<>();// treeI树其他属性
            for (String cI : classIMap.keySet()) {
                attrIs.put(cI,classIMap.get(cI));//当前树全部属性
                treeI.setText(classIMap.get("NAME"));//获取树名称
                if("NODECODE".equals(cI)){
                    treeI.setId(classIMap.get("NODECODE"));//获取树编号
                    List<Map<String, String>> classCL = classLevelMapper.getClassCL(classIMap.get("NODECODE"));// 获取到 treeI 子节点
                    List<Tree> treeCLList=new ArrayList<>();//treeI二级节点集合
                    for (int j = 0; j < classCL.size(); j++) {
                        Tree treeCL=new Tree();//当前树
                        for (String jm:classCL.get(j).keySet()) {
                            treeCL.setText(classCL.get(j).get("NAME"));//获取treeCL树名称
                            treeCL.setId(classCL.get(j).get("NODECODE"));//获取树编号
                            if("NODECODE".equals(jm)){
                                Integer count=classLevelMapper.getClassCLCount(classCL.get(j).get(jm));
                                if(count!=null&&count>0){
                                    List<Tree> newList=getCLBynodeCode( classCL.get(j).get(jm));//获取当前节点nodecode
                                    treeCL.setChildren(newList);
                                }
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
     * 添加底层或者中间门类
     * @param parmMap
     * @return
     */
    @Override
    public int createTreeL(Map<String, Object> parmMap) {
        int result=0;
        try {
            Map<String,String> treeT=new HashMap<>();
            treeT.put("NODECODE", "ucls"+StringUtil.getDate(2)+StringUtil.getRandom(1000,10000)+1);//唯一编号
            treeT.put("CLASSCODE","");
            treeT.put("CLASSTABLECODE","");
            treeT.put("TABLECODE","");
            treeT.put("SERIAL","");
            if(parmMap.get("name")!=null){
                treeT.put("NAME",parmMap.get("name").toString());
            }
            if(parmMap.get("type")!=null){
                treeT.put("TYPE",parmMap.get("type").toString());
            }
            if(parmMap.get("type")!=null){
                if("L".equals(parmMap.get("type").toString())){ //中间门类
                    for (String obj:parmMap.keySet()) {
                        if(parmMap.get("attrs")!=null){
                            Map<String,String> map=(Map<String,String>)parmMap.get("attrs");//获取当前
                            treeT.put("PARENTCODE",map.get("NODECODE"));
                        }
                    }
                    result=classLevelMapper.addTreeLC(treeT);
                }else if("C".equals(parmMap.get("type").toString())){ //底层门类
                    boolean bool=tableService.addTableDescription(parmMap);
                    if(bool){
                        result=1;
                        System.out.println("增加底层门类成功");
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            return result;
        }
    }

    /**
     * 删除门类CL
     * @param parmMap
     * @return
     */
    @Override
    public String delTreeLC(Map<String,String> parmMap) {
        String result="";
        try {
            if(parmMap!=null){
                String type=parmMap.get("TYPE");
                if("L".equals(type)){
                    int re=classLevelMapper.delTreeL(parmMap);
                    if(re>0){
                        result="删除成功";
                    }
                }else if("C".equals(type)){
                    List<Map<String,String>> listMap=getTableByNodeCode(parmMap.get("NODECODE"));//实体表节点集合
                    List<Map<String,String>> tableListMap=new ArrayList<>();//实体表描述集合
                    if(listMap!=null&&listMap.size()!=0){
                        for (int i = 0; i <listMap.size() ; i++) {
                            int re=0;//接收返回值
                            //查询实体表tableCode
                            String tableCode=listMap.get(i).get("TABLECODE")+"";//根据实体表节点集合里的tableCode获取实体表描述
                            List<Map<String,String>> tableList=tableMapper.getTableByTableCode(tableCode);//查询表描述
                            re=classLevelMapper.delTreeE(listMap.get(i));//删除实体E节点
                            if(re>0){
                                System.err.println("删除成功CLASSNODE里Type为E的成功--");
                            }
                            re=classLevelMapper.delTreeC(parmMap);//删除C节点
                            if(re>0){
                                System.err.println("删除成功CLASSNODE里Type为C的成功--");
                            }
                            if(tableList!=null&&tableList.size()!=0){
                                for (int j = 0; j <tableList.size() ; j++) {
                                    String tableName=tableList.get(j).get("NAME");//获取表名称，
                                    re=tableMapper.delTableByTableName(tableName);//删除实体表
                                    if(re>0){
                                        System.err.println("删除实体表成功---"+tableName);
                                    }
                                    re=tableMapper.delSystemUseRRoleTableByTableName(tableName);//删除权限表列
                                    if(re>0){
                                        System.err.println("删除权限表列成功----"+tableCode);
                                    }
                                    re=tableMapper.delTableDescriptionByTableCode(tableCode);//删除描述表的列
                                    if(re>0){
                                        System.err.println("删除描述的实体表成功----"+tableCode);
                                    }
                                    boolean booll=tableInputViewMapper.delTableInputViewByTableCode(tableCode);
                                    if(booll){
                                        System.err.println("删除录入表成功----"+tableCode);
                                        booll=tableInputViewMapper.dexTableIndex(tableCode);
                                        if(booll){
                                            System.err.println("删除索引表成功----"+tableCode);

                                            booll=tableInputViewMapper.dexTableIndex(tableCode);
                                        }else{
                                            System.err.println("删除索引表失败----"+tableCode+"---未有数据或内部错误");
                                        }
                                    }else{
                                        System.err.println("删除录入表失败----"+tableCode+"---未有数据或内部错误");
                                    }

                                    int resulsst=newInputViewMapper.delAllInputViewByTableCode(tableCode);
                                    if(resulsst>=0){
                                        System.out.println("底层门类---视图删除成功");
                                    }
                                    re=tableMapper.delTableColumnDescription(tableCode);//删除字纪录表数据
                                    if(re>0){
                                        System.err.println("删除字纪录表数据成功----"+tableCode);
                                    }
                                }
                            }
                        }
                    }else{
                        int re=classLevelMapper.delTreeL(parmMap);
                        if(re>0){
                            result="删除成功";
                        }
                    }
                }
            }
        }catch (Exception e){
            System.err.println(e.getMessage());
            result=e.getMessage();
        }finally {
            return result;//删除中间或底层门类
        }
    }

    /**
     * 根据nodecode获取其子节点
     * @param nodeCode
     * @return
     */
    public  List<Tree> getCLBynodeCode(String nodeCode){
        List<Map<String, String>> treeList = classLevelMapper.getClassCL(nodeCode);//获取子节点
        List<Tree> trees=new ArrayList<>();
        for (int i = 0; i <treeList.size() ; i++) {
            Tree tree1=new Tree();
            for (String cls:treeList.get(i).keySet()) {
                tree1.setId(treeList.get(i).get("NODECODE"));
                tree1.setText(treeList.get(i).get("NAME"));
                break;
            }
            tree1.setLi_attr(treeList.get(i));
            trees.add(tree1);
        }
        return trees;
    }

    /**
     * 获取字典集合
     * @param
     * @return
     */
    @Override
    public List<Map<String, String>> getAllDictionaryData() {
        return classLevelMapper.getAllDictionaryData();
    }
    /**
     * 修改classnode
     * @parmMap
     * @return
     */
    @Override
    public Boolean upTreeNameAndSerial(Map<String, String> parmMap) {
        boolean bool=true;
        try {
            int result= classLevelMapper.upTreeNameAndSerial(parmMap);
//            if (result>0){
//                System.out.println("修改classnode名称及序号成功--"+parmMap);
//            }
        }catch (Exception e){
            bool=false;
            System.out.println("修改classnode名称及序号--"+e.getMessage());
        }finally {
            return bool;
        }
    }

    /**
     * 更新父级节点值
     * @param parentCode
     * @return
     */
    @Override
    public Boolean upDragDropNodeByparentCode(String parentCode,String nodeCode) {
        Boolean bool=true;
        try {
             bool =classLevelMapper.upParentCode(parentCode,nodeCode);
//             if(bool){
//                 System.err.println("修改成功参数："+parentCode+"--"+nodeCode);
//             }
        }catch (Exception e){
            System.err.println("修改失败参数："+parentCode+"--"+nodeCode+"--"+e.getMessage());
            bool=false;
        }finally {
            return bool;
        }
    }

    /**
     * 根据父节点nodecode获取旗下实体表
     * @param nodeCode
     * @return
     */
    public  List<Map<String,String>> getTableByNodeCode(String nodeCode){
        List<Map<String,String>> listMap=new ArrayList<>();
        for(int i=1;i>0;i++){
            List<Map<String,String>> list=classLevelMapper.getTableByNodeCode(nodeCode);
            if(list!=null&&list.size()!=0){
                for (int j = 0; j <list.size() ; j++) {
                    nodeCode=list.get(j).get("NODECODE");
                    listMap.add(list.get(j));
                    continue;
                }
            }else{
                return  listMap;
            }
        }
        return listMap;
    }

}