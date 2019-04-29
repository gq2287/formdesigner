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
import java.util.*;

@Service
public class TableServiceImpl implements TableService
{
    @Resource
    private TableMapper tableMapper;
    @Resource
    private ClassLevelServiceImpl classLevelService;

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
     *
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
        boolean bool = true;
        try {
            //创建底层门类
            Map<String, String> attrs = (Map<String, String>) parmMap.get("attrs");
            String uuid = "ucls" + StringUtil.getDate(2) + StringUtil.getRandom(1000, 10000) + 1;
            Map<String, String> treeT = new HashMap<>();//参数
            treeT.put("NODECODE", uuid);//唯一编号
            treeT.put("PARENTCODE", attrs.get("NODECODE") + "");
            treeT.put("NAME", parmMap.get("name").toString());
            treeT.put("TYPE", parmMap.get("type").toString());//C
            treeT.put("CLASSCODE", "");//表
            treeT.put("CLASSTABLECODE", attrs.get("templateCode") + "");
            treeT.put("TABLECODE", "");
            treeT.put("SERIAL", "");//最大的
            int result = tableMapper.addTableTreeInfo(treeT);//父节点
            if (result > 0) {
                System.out.println("底层门类父节点创建成功");
            }
            String tableName = "";
            Map<String, String> dataType = new HashMap();//数据库为Oracle字段类型
            dataType.put("1", "VARCHAR2");
            dataType.put("2", "NUMBER");
            dataType.put("3", "NUMBER");
            dataType.put("4", "DATE");
            dataType.put("5", "VARCHAR2");
            String isKey = null;
            for (String str : parmMap.keySet()) {
                if ("tableDescriptions".equals(str)) {//表描述
                    Map<String, Object> tableDescriptionTempMap = (Map<String, Object>) parmMap.get(str);//临时表描述
                    if (tableDescriptionTempMap != null && tableDescriptionTempMap.size() != 0) {
                        for (String obr : tableDescriptionTempMap.keySet())
                            if ("tableDescription".equals(obr)) {
                                JSONArray jsonArray = (JSONArray) tableDescriptionTempMap.get(obr);
                                List tempListMap = (List) jsonArray;//获取tableDescription集合
                                if (tempListMap != null && tempListMap.size() != 0) {
                                    List<String> listUUID = new ArrayList<>();//保存uuid
                                    listUUID.add(uuid);//root  uuid
                                    for (int ci = 0; ci < tempListMap.size(); ci++) {
                                        listUUID.add("ucls" + StringUtil.getDate(2) + StringUtil.getRandom(1000, 10000) + 1);//nodeCodeList
                                    }
                                    for (int i = 0; i < tempListMap.size(); i++) {
                                        Type typeObj = new TypeToken<Map<String, Object>>() {}.getType();
                                        Map<String, Object> tempMap = JSONObject.parseObject(String.valueOf(tempListMap.get(i)), typeObj);
                                        tableName = tempMap.get("name") + "";//获取创建的表名
                                        //创建用户角色表权限
                                        Map<String, String> sysMap = new HashMap<>();
                                        sysMap.put("userCode", listUUID.get(i + 1));
                                        sysMap.put("tableName", tableName);
                                        int result2 = tableMapper.addSystemUseRRoleTable(sysMap);//添加表权限
                                        if (result2 > 0) {
                                            System.out.println("添加表权限创建成功");
                                        }
                                        //添加tree节点
                                        treeT.put("NODECODE", listUUID.get(i + 1));//唯一编号
                                        treeT.put("PARENTCODE", listUUID.get(i));
                                        treeT.put("NAME", (String) tempMap.get("chineseName"));
                                        treeT.put("TYPE", "E");//E
                                        treeT.put("CLASSTABLECODE", attrs.get("NODECODE") + "");
                                        treeT.put("SERIAL", "");//最大的
                                        StringBuffer sql = new StringBuffer("CREATE TABLE ");
                                        sql.append(" " + tableName + "(");//获取里面的数据列字段
                                        String tableCode = StringUtil.getDate(2) + StringUtil.getRandom(1000, 10000) + 1;//获取tableCode
                                        Map<String, String> tableDescriptionMap = new HashMap<>();//表描述
                                        boolean isOK = true;//判断是只有一个主键bi
                                        for (String temp : tempMap.keySet()) {
                                            treeT.put("CLASSCODE", tempMap.get("tableCode") + "");//底层门类字段
                                            if ("tableColumns".equals(temp)) {
                                                if (tempMap.get(temp) != null) {
                                                    JSONArray tableColumnsArray = (JSONArray) tempMap.get(temp);//jsonArray
                                                    List tableColumnsList = (List) tableColumnsArray;//获取到tableColumns 数据库列
                                                    if (tableColumnsList != null) {
                                                        for (int j = 0; j < tableColumnsList.size(); j++) {
                                                            StringBuffer sb = new StringBuffer();
                                                            Map<String, Object> tempColumnsMap = JSONObject.parseObject(String.valueOf(tableColumnsList.get(j)), typeObj);
                                                            Map<String, Object> tableColumnDMap = tempColumnsMap;//字段记录表集合//添加到纪录表
                                                            tableColumnDMap.put("TABLECODE", tableCode);
                                                            tableColumnDMap.put("COLUMNCODE", tableColumnDMap.get("COLUMNCODE"));//防止主键重复添加失败，把clomnCode修改为数据库编号
                                                            tableColumnDMap.put("SERIAL",String.valueOf(j));
                                                            StringBuffer stringBuffer = getSQLTableColumnDescription(tableColumnDMap);
                                                            int count = tableMapper.addTableColumnDescription(stringBuffer);//保存字段到纪录表
                                                            if (count > 0) {
                                                                System.out.println("保存字段到纪录表成功--" + stringBuffer);
                                                            }
                                                            //保存视图列表
                                                            String visible = tempColumnsMap.get("VISIBLE") + "";//是否可见 T全部添加数据
                                                            if("T".equals(visible)){
                                                                Map<String, String> viewMap = new HashMap<>();//字段记录表集合//添加到纪录表
                                                                viewMap.put("listCode", String.valueOf((new Date()).getTime()) + (int)(100.0D + Math.random() * 1000.0D));
                                                                viewMap.put("columnCode", String.valueOf(tableColumnDMap.get("COLUMNCODE")));
                                                                viewMap.put("serial", String.valueOf(j));
                                                                viewMap.put("title", String.valueOf(tableColumnDMap.get("CHINESENAME")));
                                                                count=tableMapper.addOneColumn(viewMap);
                                                                if (count > 0) {
                                                                    System.out.println("保存列到视图表成功--" + viewMap);
                                                                }
                                                            }
                                                            String mc =  String.valueOf(tempColumnsMap.get("NAME") );//字段名称
                                                            String lx =  String.valueOf(tempColumnsMap.get("TYPE") );//数据类型
                                                            String cd =  String.valueOf(tempColumnsMap.get("WIDTH") );//数据长度
                                                            String zj =  String.valueOf(tempColumnsMap.get("ISKEY") );//是否为主键
                                                            String sfwk =  String.valueOf(tempColumnsMap.get("CANNULL") );//是否为空
                                                            String mrz = String.valueOf(tempColumnsMap.get("VALUEDEFAULT")) ;//默认值 CANREPEAT
                                                            sb.append(mc + " ");
                                                            sb.append(dataType.get(lx) + " ");
                                                            if (!"0".equals(cd) && !"DATE".equals(dataType.get(lx)) && !"NUMBER".equals(dataType.get(lx))) {
                                                                sb.append("(");
                                                                sb.append(cd);
                                                                sb.append(") ");
                                                            }
                                                            if (mrz != null && !"".equals(mrz)) {
                                                                if("NUMBER".equals(dataType.get(lx))){
                                                                    sb.append(" DEFAULT(" + mrz + ") ");
                                                                }else if("DATE".equals(dataType.get(lx))){
                                                                    sb.append(" DEFAULT(to_date('"+mrz+"','yyyy-mm-dd hh24:mi:ss'))");
                                                                }else{
                                                                    sb.append(" DEFAULT('" + mrz + "') ");
                                                                }
                                                            }
                                                            if ("T".equals(zj)) {
                                                                if (isOK) {
                                                                    isKey = "primary key(" + mc + "))";//主键 PRIMARY KEY ("RECORDCODE")
                                                                    isOK = false;
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
                                                        SQLMap.put("SQL", sql.toString());//添加表
                                                        int result4 = tableMapper.addTable(SQLMap);//创建实体表
                                                        if (result4 > 0) {
                                                            System.out.println("创建实体表创建成功");
                                                        }
                                                    }
                                                }
                                            } else if ("tableRelation".equals(temp)) {//获取里面的数据列字段
                                                JSONArray tableColumnsArray = (JSONArray) tempMap.get(temp);//jsonArray
                                                List tableRelationList = (List) tableColumnsArray;//获取到tableColumns 表关联
                                                if (tableRelationList != null && tableRelationList.size() != 0) {
                                                    for (int j = 0; j < tableRelationList.size(); j++) {
                                                        Map<String, String> tableRelationMap = JSONObject.parseObject(String.valueOf(tableRelationList.get(j)), typeObj);//旧编号
                                                        //处理表字段关系
                                                        addTableRelation(tableRelationMap);//添加关系
                                                    }

                                                }
                                            } else {
                                                tableDescriptionMap.put(temp, tempMap.get(temp) + "");//表描述
                                            }
                                        }
                                        treeT.put("TABLECODE", tableCode);
                                        //底层门类
                                        int result6 = tableMapper.addTableTreeInfo(treeT);
                                        if (result6 > 0) {
                                            System.out.println("底层门类子节点创建成功");
                                        }
                                        //调用添加表描述  唯一表编号
                                        tableDescriptionMap.put("tableCode", tableCode);
                                        tableDescriptionMap.put("systemType", "D");//追加条件
                                        tableDescriptionMap.put("aggregate", "A");//追加条件
                                        int result7 = tableMapper.addTableDescription(tableDescriptionMap);
                                        if (result7 > 0) {
                                            System.out.println("添加表描述 创建成功");
                                        }
                                    }

                                }

                            }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            bool = false;
        } finally {
            System.out.println("是否成功：" + bool);
            return bool;
        }
    }

    /**
     * 添加表关系
     *
     */
    @Override
    public void addTableRelation(Map<String,String> tableRelationMap) {
        try {
            tableRelationMap.put("relationCode",StringUtil.getDate(2)+StringUtil.getRandom(1000,10000)+1);//关联编号
            tableRelationMap.put("NO","1");//追加条件
            tableRelationMap.put("TYPE","A");//追加条件
            tableRelationMap.put("CANCHANGE","A");//追加条件
            int result=tableMapper.createTableRelation(tableRelationMap);//添加字段关系
            if(result>0){
                System.out.println("添加字段关系创建成功--"+tableRelationMap);
            }
        }catch (Exception e){
            System.out.println("添加字段关系失败--"+e.getMessage());
        }
    }
    /**
     * 获取要修改的信息
     *
     * @param attrs
     * @return
     */
    @Override
    public Map<String, Object> getUpTreeByAttrs(Map<String, Object> attrs) {
        Map<String, Object> parms = new HashMap<>();
        //底层门类
        String nodeCode = attrs.get("NODECODE") + "";
        String parentCode = attrs.get("PARENTCODE") + "";
        List<Map<String, Object>> newList = new ArrayList<>();//存放全部门类节点旗下子节点信息
        List<Map<String, String>> listMap = classLevelService.getTableByNodeCode(nodeCode);//获取实体表 tableCode
        if (listMap != null && listMap.size() > 0) {
            for (int i = 0; i < listMap.size(); i++) {
                String tableCode = listMap.get(i).get("TABLECODE");
                List<Map<String, Object>> etableMapList = new ArrayList<>();//参数
                etableMapList = tableMapper.getEntityTableInfo(tableCode);//当前表描述
                if (etableMapList != null && etableMapList.size() > 0) {
                    for (int j = 0; j < etableMapList.size(); j++) {
                        String tableName = String.valueOf(etableMapList.get(j).get("NAME"));
                        etableMapList.get(j).put("tableColumns", tableMapper.getEntityTableColumn(tableCode));//当前实体表字段
                        etableMapList.get(j).put("tableRelations", tableMapper.getEntityTableRelation(tableName));//当前实体表关系
                        newList.add(etableMapList.get(j));//获取旗下实体类
                    }
                }
            }
        }
        parms.put("table", newList);
        Map<String, Object> parmMap = new HashMap<>();
        parmMap.put("classTableCode", attrs.get("CLASSTABLECODE"));
        parmMap.put("parentCode", attrs.get("PARENTCODE"));
        parms.put("template", tableMapper.getSelectTemplateByDataType(parmMap));
        return parms;
    }


    /**
     * 添加字段
     * @param field
     * @return
     */
    @Override
    public Boolean addField(Map<String, Object> field) {
        Boolean bool = null;
        try {
            Map<String, String> dataType = new HashMap();//数据库为Oracle字段类型
            dataType.put("1", "VARCHAR2");
            dataType.put("2", "NUMBER");
            dataType.put("3", "NUMBER");
            dataType.put("4", "DATE");
            dataType.put("5", "VARCHAR2");
            Map<String, Object> parms = new HashMap<>();//存放添加参数
            String tableName = String.valueOf(field.get("tableName"));//放入表名
            Type typeObj = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> objectMap = JSONObject.parseObject(String.valueOf(field.get("tableColumns")), typeObj);//获取
            //添加表纪录列
            StringBuffer sql = new StringBuffer(" ");
            String lx = objectMap.get("TYPE") + "";//数据类型
            sql.append(objectMap.get("NAME") + " "); //列名
            sql.append(dataType.get(lx) + " "); //数据类型
            if (!"0".equals(dataType.get(lx)) && !"DATE".equals(dataType.get(lx)) && !"NUMBER".equals(dataType.get(lx))) { //长度
                sql.append("(");
                sql.append(objectMap.get("WIDTH"));
                sql.append(") ");
            }
            if (objectMap.get("VALUEDEFAULT") != null && !"".equals(objectMap.get("VALUEDEFAULT"))) {//默认值
                if("NUMBER".equals(dataType.get(lx))){
                    sql.append(" DEFAULT(" + objectMap.get("VALUEDEFAULT") + ") ");
                }else if("DATE".equals(dataType.get(lx))){
//                    to_date(sysdate,'yyyy-mm-dd hh24:mi:ss')
                    sql.append(" DEFAULT(to_date('"+objectMap.get("VALUEDEFAULT")+"','yyyy-mm-dd hh24:mi:ss'))");
                }else{
                    sql.append(" DEFAULT('" + objectMap.get("VALUEDEFAULT") + "') ");
                }
            }

            if ("F".equals(objectMap.get("CANNULL"))) {
                sql.append(" not null");//非空
            }

            parms.put("tableName", tableName);//表名
            parms.put("sql", sql);//修改的sql
            int result1 = tableMapper.addField(parms);//向表内添加字段
            if (result1 >= 0) {
                System.err.println("追加新字段成功--" + sql);
            }

            //判断字段是否为空 如果为空那么添加时就不需要添加该字段
            sql = getSQLTableColumnDescription(objectMap);
            int result2 = tableMapper.addTableColumnDescription(sql);
            if (result2 > 0) {
                System.err.println("追加新表纪录列成功--" + objectMap);
            }
            //保存视图列表
            String visible = objectMap.get("VISIBLE") + "";//是否可见 T全部添加数据
            if("T".equals(visible)){
                Map<String, String> viewMap = new HashMap<>();//字段记录表集合//添加到纪录表
                viewMap.put("listCode", String.valueOf((new Date()).getTime()) + (int)(100.0D + Math.random() * 1000.0D));
                viewMap.put("columnCode", String.valueOf(objectMap.get("COLUMNCODE")));
                int SERIAL=objectMap.get("SERIAL")!=null&&!"".equals(String.valueOf(objectMap.get("SERIAL")))? Integer.valueOf((String)objectMap.get("SERIAL")):6;
                viewMap.put("serial", ++SERIAL+"");
                viewMap.put("title", String.valueOf(objectMap.get("CHINESENAME")));
                int count=tableMapper.addOneColumn(viewMap);
                if (count > 0) {
                    System.out.println("保存列到视图表成功--" + viewMap);
                }
            }
            bool = true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            bool = false;
        } finally {
            return bool;
        }
    }
    /**
     * 修改描述表信息
     * @param field
     * @return
     */
    @Override
    public Boolean upFieldTableDescription(Map<String, String> field) {
        boolean bool=true;
        try {
            int result=tableMapper.upFieldTableDescription(field);
            if(result>1){
                System.err.println("修改描述表信息"+field);
            }
        }catch (Exception e){
            System.err.println("修改描述表信息"+e.getMessage());
            bool=false;
        }finally {
            return bool;
        }
    }

    /**
     * 修改表关系
     * @param fieldRelation  slaveColumnCode masterColumnCode
     * @return
     */
    @Override
    public Boolean upFieldTableRelation(Map<String, Object> fieldRelation) {
        boolean bool=true;
        try {
            int result=tableMapper.upFieldTableRelation(fieldRelation);
            if(result>1){
                System.err.println("表关系修改成功"+fieldRelation);
            }
        }catch (Exception e){
            System.err.println("表关系修改"+e.getMessage());
            bool=false;
        }finally {
            return bool;
        }
    }

    /**
     * 删表字段
     * @param field
     * @return
     */
    @Override
    public Boolean delField(Map<String, Object> field) {
        Boolean bool = true;
        try{
            int res1=tableMapper.delFieldTableColumnDescription(field);//受影响行数
            if(res1>0){
                System.out.println("删除表字段描述成功"+field);
            }
            res1=tableMapper.delFieldTableRelation(field);
            if(res1>0){
                System.out.println("删除表字段关系成功"+field);
            }
            res1=tableMapper.delFieldTable(field);
            if(res1>0){
                System.out.println("删除实体表字段成功"+field);
            }
            res1=tableMapper.delOneColumn(field);
            if(res1>0){
                System.out.println("删除视图字段成功"+field);
            }
        }catch (Exception e){
            bool=false;
            System.err.println(e.getMessage());
        }finally {
            return bool;
        }
    }

    /**
     * 删除关系
     * @param relationCode
     * @return
     */
    @Override
    public Boolean delFieldTableRelation(String relationCode) {
        Boolean bool = true;
        try{
            int res2=tableMapper.delFieldRelation(relationCode);
            if(res2>0){
                System.out.println("删除表关系成功"+relationCode);
            }

        }catch (Exception e){
            bool=false;
            System.err.println(e.getMessage());
        }finally {
            return bool;
        }
    }


    @Override
    public Boolean upField(Map<String, Object> field) {
        String tableName=String.valueOf(field.get("tableName"));
        String tableCode=String.valueOf(field.get("tableCode"));
        String columnCode=String.valueOf(field.get("columnCode"));
        Map<String,String> newColumnMap=(Map<String,String>)field.get("tableColumns");
        boolean bool=getClolumnInfo(tableName,tableCode,columnCode,newColumnMap);
        //修改表字段信息
        System.out.println("修改表字段信息:"+bool);
        return bool;
    }

    /**
     * 查询数据表是否有数据
     *
     * @param tableName
     * @return
     */
    @Override
    public Boolean getIsOkUpDataByTableName(String tableName) {
        boolean bool = true;
        int count = tableMapper.getIsOkUpDataByTableName(tableName);
        if (count > 1) {
            bool = false;
            System.out.println("存在数据"+tableName);
        }
        return bool;
    }


    /**
     * 根据map生成对应sql语句
     *
     * @param objectMap 字段列
     * @return
     */
    public StringBuffer getSQLTableColumnDescription(Map<String, Object> objectMap) {
        //判断字段是否为空 如果为空那么添加时就不需要添加该字段
        StringBuffer sql = new StringBuffer("INSERT INTO TABLECOLUMNDESCRIPTION(");
        Map<String, Object> newObjectMap = new HashMap<>();
        for (String str : objectMap.keySet()) {
            if (objectMap.get(str) != null && !"".equals(objectMap.get(str))) {
                newObjectMap.put(str, objectMap.get(str));
            }
        }
        int index = 1;
        for (String ne : newObjectMap.keySet()) {
            if (newObjectMap.size() == index) {//如果是最后一个就不要，
                sql.append(ne + ")VALUES(");
                int index2 = 1;
                for (String value : newObjectMap.keySet()) {
                    if (newObjectMap.size() == index2) {//如果是最后一个就不要，
                        sql.append("'" + newObjectMap.get(value) + "')");
                    } else {
                        sql.append("'" + newObjectMap.get(value) + "',");
                    }
                    index2++;
                }
            } else {
                sql.append(ne + ",");
            }
            index++;
        }
        System.err.println("sql---" + sql);
        return sql;
    }

    /**
     * 获取创建字段sql
     * @param tableName 实体表名
     * @param newColumnMap 新字段
     * @return
     */
    public Boolean getClolumnInfo(String tableName,String tableCode,String columnCode,Map<String, String> newColumnMap){
        boolean bool=true;
        Map<String, String> dataType = new HashMap();//数据库为Oracle字段类型
        dataType.put("1", "VARCHAR2");
        dataType.put("2", "NUMBER");
        dataType.put("3", "NUMBER");
        dataType.put("4", "DATE");
        dataType.put("5", "VARCHAR2");
        StringBuffer sql = new StringBuffer("ALTER TABLE "+tableName+" MODIFY(");//sql实体表
        StringBuffer columnSql = new StringBuffer(" SET ");//描述表列
        String new_mc = newColumnMap.get("NAME");//字段名称
        String new_lx = newColumnMap.get("TYPE");//数据类型
        String new_cd = newColumnMap.get("WIDTH");//数据长度
        String new_mrz = newColumnMap.get("VALUEDEFAULT");//默认值
        if(new_mc!=null&&!"".equals(new_mc)){
            sql.append(new_mc+" ");
        }
        sql.append(dataType.get(new_lx)+"(");
        if(new_cd!=null&&!"".equals(new_cd)){
            sql.append(new_cd+") ");
        }
        if(new_mrz!=null&&!"".equals(new_mrz)){
            sql.append("default '"+new_mrz+"')");
        }else{
            sql.append(")");
        }
        Map<String,String> newmap=new HashMap<>();
        newmap.put("sql",sql.toString());

        for (String column:newColumnMap.keySet()) {
            if(newColumnMap.get(column)!=null&&!"".equals(newColumnMap.get(column))&&!"COLUMNCODE".equals(column)&&!"TABLECODE".equals(column)){
                columnSql.append(column+"='"+String.valueOf(newColumnMap.get(column))+"',");
            }
        }
        columnSql=new StringBuffer(columnSql.substring(0,columnSql.length()-1));//删除,
        Map<String,String> columns=new HashMap<>();
        columns.put("sql",columnSql.toString());
        columns.put("columnCode",columnCode);
        columns.put("tableCode",tableCode);
        try {
            int res1=tableMapper.upFieldEntityTable(newmap);//修改实体表字段
            if(res1>0){
                System.out.println("修改实体表"+sql);
            }
            int res2=tableMapper.upFieldTableColumnDescription(columns);//修改列纪录表内容
            if(res2>0){
                System.out.println("修改纪录表"+columnSql);
            }
        }catch (Exception e){
            bool=false;
        }finally {
            return bool;
        }
    }
}