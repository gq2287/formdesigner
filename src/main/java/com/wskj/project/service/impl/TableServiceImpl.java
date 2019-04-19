package com.wskj.project.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.wskj.project.dao.TableMapper;
import com.wskj.project.service.TableService;
import com.wskj.project.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TableServiceImpl implements TableService {
    @Resource
    private TableMapper tableMapper;
    @Resource
    private ClassLevelServiceImpl classLevelService;
    @Resource
    private TableServiceImpl tableService;

    @Override
    public List<Map<String, String>> getTemplateList() {
        return tableMapper.getTemplateList();
    }

    @Override
    public List<Map<String, String>> getOptionalTemplateList() {
        return tableMapper.getOptionalTemplateList();
    }

    /**
     * 添加已选模版
     *
     * @param parmMap
     * @return
     */
    @Override
    public int createTemplate(Map<String, String> parmMap) {
        return tableMapper.createTemplate(parmMap);
    }

    /**
     * 删除已选模版
     *
     * @param
     * @return
     */
    @Override
    public int delTemplate(String classCode) {
        return tableMapper.delTemplate(classCode);
    }

    /**
     * 获取选中节点模版
     * getSelectTemplateList
     * @param parmMap
     * @return
     */
    @Override
    public List<Map<String, Object>> getSelectTemplateList(Map<String, String> parmMap) {//parentCode
        List<Map<String, Object>> mapList = tableMapper.getSelectTemplateList(parmMap);
        if (mapList.size() == 0) {//如果为空，就去查询默认的自由门类
            parmMap.put("parentCode", "12");
            mapList = tableMapper.getSelectTemplateList(parmMap);
        }
        return mapList;
    }


    @Override
    public List<Map<String, String>> getSelectTableByClassCode(Map<String, String> parmMap) {
        return tableMapper.getSelectTableByClassCode(parmMap);
    }

    @Override
    public List<Map<String, String>> getTableInfoByTableCode(Map<String, String> map) {
        return tableMapper.getTableInfoByTableCode(map);
    }

    /**
     * 创建表基本描述(只有创建底层门类才调用)
     *
     * @param parmMap
     * @return
     */
    @Override
    public Boolean addTableDescription(Map<String, Object> parmMap) {
        boolean bool=true;
        try {
            //创建底层门类
            Map<String,String> attrs=(Map<String,String>)parmMap.get("attrs");
            String uuid="ucls"+StringUtil.getDate(2)+StringUtil.getRandom(1000,10000)+1;
            Map<String,String> treeT=new HashMap<>();//参数
            treeT.put("NODECODE",uuid);//唯一编号
            treeT.put("PARENTCODE",attrs.get("NODECODE")+"");
            treeT.put("NAME",parmMap.get("name").toString());
            treeT.put("TYPE",parmMap.get("type").toString());//C
            treeT.put("CLASSCODE","");//表
            treeT.put("CLASSTABLECODE",attrs.get("templateCode")+"");
            treeT.put("TABLECODE","");
            treeT.put("SERIAL","");//最大的
            int result=tableMapper.addTableTreeInfo(treeT);//父节点
            if(result>0){
                System.out.println("底层门类父节点创建成功");
            }
            String tableName = "";
            Map<String, String> dataType = new HashMap();//数据库为Oracle字段类型
            dataType.put("1", "VARCHAR2");
            dataType.put("2", "NUMBER");
            dataType.put("3", "NUMBER");
            dataType.put("4", "DATE");
            dataType.put("5", "VARCHAR2");
            String isKey =null;
            for (String str : parmMap.keySet()) {
                if ("tableDescriptions".equals(str)) {//表描述
                    Map<String, Object> tableDescriptionTempMap = (Map<String, Object>) parmMap.get(str);//临时表描述
                    if (tableDescriptionTempMap != null && tableDescriptionTempMap.size() != 0) {
                        for (String obr : tableDescriptionTempMap.keySet())
                            if ("tableDescription".equals(obr)) {
                                JSONArray jsonArray = (JSONArray) tableDescriptionTempMap.get(obr);
                                List tempListMap = (List) jsonArray;//获取tableDescription集合
                                if (tempListMap != null && tempListMap.size() != 0) {
                                    List<String> listUUID=new ArrayList<>();//保存uuid
                                    listUUID.add(uuid);//root  uuid
                                    for (int ci = 0; ci < tempListMap.size(); ci++) {
                                        listUUID.add("ucls"+StringUtil.getDate(2)+StringUtil.getRandom(1000,10000)+1);//nodeCodeList
                                    }
                                    for (int i = 0; i < tempListMap.size(); i++) {
                                        Type typeObj = new TypeToken<Map<String, Object>>() {}.getType();
                                        Map<String, Object> tempMap = JSONObject.parseObject(String.valueOf(tempListMap.get(i)), typeObj);
                                        tableName = tempMap.get("name") + "";//获取创建的表名
                                        //创建用户角色表权限
                                        Map<String,String> sysMap=new HashMap<>();
                                        sysMap.put("userCode",listUUID.get(i+1));
                                        sysMap.put("tableName",tableName);
                                       int result2= tableMapper.addSystemUseRRoleTable(sysMap);//添加表权限
                                        if(result2>0){
                                            System.out.println("添加表权限创建成功");
                                        }
                                        //添加tree节点
                                        treeT.put("NODECODE",listUUID.get(i+1));//唯一编号
                                        treeT.put("PARENTCODE",listUUID.get(i));
                                        treeT.put("NAME", (String) tempMap.get("chineseName"));
                                        treeT.put("TYPE","E");//E
                                        treeT.put("CLASSTABLECODE",attrs.get("NODECODE")+"");
                                        treeT.put("SERIAL","");//最大的
                                        StringBuffer sql = new StringBuffer("CREATE TABLE ");
                                        sql.append(" " + tableName + "(");//获取里面的数据列字段
                                        String tableCode= StringUtil.getDate(2)+StringUtil.getRandom(1000,10000)+1;//获取tableCode
                                        Map<String, String> tableDescriptionMap=new HashMap<>();//表描述
                                        for (String temp : tempMap.keySet()) {
                                            boolean isOK=true;//判断是只有一个主键
                                            treeT.put("CLASSCODE",tempMap.get("tableCode")+"");//底层门类字段
                                            if ("tableColumns".equals(temp)) {
                                                if (tempMap.get(temp) != null) {
                                                    JSONArray tableColumnsArray = (JSONArray) tempMap.get(temp);//jsonArray
                                                    List tableColumnsList = (List) tableColumnsArray;//获取到tableColumns 数据库列
                                                    if (tableColumnsList != null) {
                                                        for (int j = 0; j < tableColumnsList.size(); j++) {
                                                            StringBuffer sb = new StringBuffer();
                                                            Map<String, Object> tempColumnsMap = JSONObject.parseObject(String.valueOf(tableColumnsList.get(j)), typeObj);
                                                            Map<String,Object> tableColumnDMap=tempColumnsMap;//字段记录表集合//添加到纪录表
                                                            tableColumnDMap.put("TABLECODE",tableCode);
                                                            tableColumnDMap.put("COLUMNCODE",StringUtil.getDate(2)+StringUtil.getRandom(100,1000)+1);//防止主键重复添加失败，把clomnCode修改为数据库编号

                                                            int count=tableMapper.addTableColumnDescription(tableColumnDMap);//保存字段到纪录表
                                                            if(count>0){
                                                                System.out.println("保存字段到纪录表成功");
                                                            }
                                                            String mc = tempColumnsMap.get("NAME") + "";//字段名称
                                                            String lx = tempColumnsMap.get("TYPE") + "";//数据类型
                                                            String cd = tempColumnsMap.get("WIDTH") + "";//数据长度
                                                            String zj = tempColumnsMap.get("ISKEY") + "";//是否为主键
                                                            String sfwk = tempColumnsMap.get("CANNULL") + "";//是否为空
                                                            String mrz = tempColumnsMap.get("VALUEDEFAULT") + "";//默认值
                                                            sb.append(mc + " ");
                                                            sb.append(dataType.get(lx)+" ");
                                                            if(!"0".equals(cd)&&!"DATE".equals(dataType.get(lx))&&!"NUMBER".equals(dataType.get(lx))){
                                                                sb.append("(");
                                                                sb.append(cd);
                                                                sb.append(") ");
                                                            }
                                                            if(mrz!=null&&!"".equals(mrz)){
                                                                sb.append(" DEFAULT("+mrz+") ");
                                                            }
                                                            if ("T".equals(zj)) {
                                                                if(isOK){
                                                                    isKey="primary key("+mc+"))";//主键 PRIMARY KEY ("RECORDCODE")
                                                                    isOK=false;
                                                                }
                                                            }
                                                            if ("F".equals(sfwk)) {
                                                                sb.append(" not null,");//非空
                                                            } else {
                                                                sb.append(",");
                                                            }
                                                            sql.append(sb);
                                                        }
                                                        sql.append(isKey);
                                                        System.out.println(sql);
                                                        Map<String, String> SQLMap = new HashMap<>();
                                                        SQLMap.put("SQL",sql.toString());//添加表
                                                        int result4=tableMapper.addTable(SQLMap);//创建实体表
                                                        if(result4>0){
                                                            System.out.println("创建实体表创建成功");
                                                        }
                                                    }
                                                }
                                            }else if("tableRelation".equals(temp)){//获取里面的数据列字段
                                                JSONArray tableColumnsArray = (JSONArray) tempMap.get(temp);//jsonArray
                                                List tableRelationList = (List) tableColumnsArray;//获取到tableColumns 表关联
                                                if(tableRelationList!=null&&tableRelationList.size()!=0){
                                                    Map<String, String> tableRelationMap=new HashMap<>();
                                                    tableRelationMap = JSONObject.parseObject(String.valueOf(tableRelationList), typeObj);
                                                    tableRelationMap.put("relationCode",StringUtil.getDate(2)+StringUtil.getRandom(1000,10000)+1);//关联编号
                                                    tableRelationMap.put("NO","1");//追加条件
                                                    tableRelationMap.put("TYPE","A");//追加条件
                                                    tableRelationMap.put("CANCHANGE","A");//追加条件
                                                    int result5=tableMapper.createTableRelation(tableRelationMap);//添加字段关系
                                                    if(result5>0){
                                                        System.out.println("添加字段关系创建成功");
                                                    }
                                                }
                                            }else{
                                                tableDescriptionMap.put(temp,tempMap.get(temp)+"");//表描述
                                            }
                                        }
                                        treeT.put("TABLECODE",tableCode);
                                        //底层门类
                                        int result6=tableMapper.addTableTreeInfo(treeT);
                                        if(result6>0){
                                            System.out.println("底层门类子节点创建成功");
                                        }
                                        //调用添加表描述  唯一表编号
                                        tableDescriptionMap.put("tableCode", tableCode);
                                        tableDescriptionMap.put("systemType","D");//追加条件
                                        tableDescriptionMap.put("aggregate","A");//追加条件
                                        int result7= tableMapper.addTableDescription(tableDescriptionMap);
                                        if(result7>0){
                                            System.out.println("添加表描述 创建成功");
                                        }
                                    }

                                }
                            }
                    }
                }
            }
        }catch (Exception e){
            System.err.println(e.getMessage());
            bool=false;
        }finally{
            System.out.println("是否成功："+bool);
            return bool;
        }
    }

    /**
     * 获取要修改的信息
     * @param attrs
     * @return
     */
    @Override
    public Map<String,Object> getUpTreeByAttrs(Map<String, Object> attrs) {
        Map<String,Object> parms=new HashMap<>();
        //底层门类
        String nodeCode=attrs.get("NODECODE")+"";
        String parentCode=attrs.get("PARENTCODE")+"";
        List<Map<String,Object>> newList=new ArrayList<>();//存放全部门类节点旗下子节点信息
        List<Map<String,String>> listMap=classLevelService.getTableByNodeCode(nodeCode);//获取实体表 tableCode
        if(listMap!=null&&listMap.size()>0){
            for (int i = 0; i <listMap.size(); i++) {
                String tableCode=listMap.get(i).get("TABLECODE");
                List<Map<String,Object>> etableMapList=new ArrayList<>();//参数
                etableMapList=tableMapper.getEntityTableInfo(tableCode);
                if(etableMapList!=null&&etableMapList.size()>0){
                    for (int j = 0; j <etableMapList.size() ; j++) {
                        etableMapList.get(j).put("tableColumns",tableMapper.getEntityTableColumn(tableCode));//当前实体表字段
                        etableMapList.get(j).put("tableRelations",tableMapper.getEntityTableRelation(tableCode));//当前实体表关系
                        newList.add(etableMapList.get(j));//获取旗下实体类
                    }
                }
            }
        }
        parms.put("table",newList);
        Map<String,Object> parmMap=new HashMap<>();
        parmMap.put("classTableCode",attrs.get("CLASSTABLECODE"));
        parmMap.put("parentCode",attrs.get("PARENTCODE"));
        parms.put("template",tableMapper.getSelectTemplateByDataType(parmMap));
        return parms;
    }

    @Override
    public Boolean getIsOkUpDataByTableName(String tableName) {
        boolean bool=true;
        int count=tableMapper.getIsOkUpDataByTableName(tableName);
        if(count >1){
            bool=false;
        }
        return bool;
    }
}