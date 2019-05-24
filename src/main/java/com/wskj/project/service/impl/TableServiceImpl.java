package com.wskj.project.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.wskj.project.dao.ClassLevelMapper;
import com.wskj.project.dao.NewInputViewMapper;
import com.wskj.project.dao.TableMapper;
import com.wskj.project.dao.TemplatCardMapper;
import com.wskj.project.service.TableService;
import com.wskj.project.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TableServiceImpl implements TableService
{
    @Resource
    private TableMapper tableMapper;
    @Resource
    private ClassLevelServiceImpl classLevelService;
    @Resource
    private ClassLevelMapper classLevelMapper;

    @Resource
    private NewInputViewServiceImpl newInputView  ;
    @Resource
    private NewInputViewMapper newInputViewMapper  ;
    @Resource
    private TemplatCardMapper templatCardMapper;

    @Override
    public List<Map<String, String>> getTemplateList() {
        return tableMapper.getTemplateList();
    }

    @Override
    public List<Map<String, String>> getOptionalTemplateList() {
        return tableMapper.getOptionalTemplateList();
    }

//    /**
//     * 添加已选模版
//     *
//     * @param parmMap
//     * @return
//     */
//    @Override
//    public Boolean createTemplate(Map<String, String> parmMap) {
//        Boolean bool=true;
//        Map<String, String> map=new HashMap<>();
//
//        map.put("NODECODE",parmMap.get("nodeCode"));
//        map.put("PARENTCODE",parmMap.get("parentCode"));
//        map.put("CLASSTABLECODE",parmMap.get("classTableCode"));
//        map.put("CLASSCODE",parmMap.get("classCode"));
//        int result=tableMapper.createTemplate(parmMap);
//        if(result>0){
//            if(!getAttrs(map)){
//                bool=false;
//            }
//        }else {
//            bool=false;
//        }
//        return bool;
//    }

    /**
     * 添加已选模版
     *
     * @param parmMap
     * @return
     */
    @Override
    public Boolean createTemplate(Map<String, String> parmMap) {
        Boolean bool=true;
        if(!addTemplatCard(parmMap)){
            bool=false;
        }
        return bool;
    }
    /**
     * 添加模版表关联纪录表信息
     * @param parmMap 存放模版关联表信息
     * @return
     */
    public Boolean addTemplatCard(Map<String, String> parmMap){
        boolean bool=true;
        String templateClassTableCode=new SimpleDateFormat("yyyyMMdd").format(new Date())+"-C-"+StringUtil.getRandomStr(6);//关联编号
        parmMap.put("tableCode",templateClassTableCode);
        parmMap.put("tableCode",templateClassTableCode);
        int result=tableMapper.createTemplate(parmMap);//添加模版表纪录
        if(result>=1){
            String nodeCode = parmMap.get("nodeCode");
            String classCode = parmMap.get("classCode");
            List<Map<String, String>> listMap = classLevelService.getTableByNodeCode(nodeCode);//查询旗下实体表
            Map<String,String> templCardMap=new HashMap<>();//添加的参数
            if(listMap!=null&&listMap.size()>0){
                for (int i = 0; i <listMap.size() ; i++) {

                    String templateInputTableCode=StringUtil.getUuid();//录入表编号
                    String templateEntityTableCode=StringUtil.getUuid();//模版实体表编号
                    String templateEntityName=listMap.get(i).get("NAME");//模版实体表中文名
                    Map<String,String> mmm=new HashMap<>();
                    mmm.put("LEVELCODE",String.valueOf((new Date()).getTime()) + (int)(100.0D + Math.random() * 1000.0D));
                    mmm.put("CLASSCODE",classCode);
                    mmm.put("RELATIONLEVEL",i+"");
                    mmm.put("TABLECODE",templateEntityTableCode);//实体表编号
                    mmm.put("ISNECESSARY","T");
                    mmm.put("CHINESENAME",listMap.get(i).get("NAME"));
                    bool=tableMapper.createClassLevel(mmm);//添加模版级别

                    templCardMap.put("templateId",StringUtil.getUuid());
                    templCardMap.put("templateClassTableCode",templateClassTableCode);//模版表纪录表编号
                    templCardMap.put("templateInputTableCode",templateInputTableCode);//录入样式表编号
                    templCardMap.put("templateTemplateName",parmMap.get("chineseName"));//模版表名称
                    templCardMap.put("templateType",parmMap.get("classTableCode"));//类型
                    templCardMap.put("templateEntityTableCode",templateEntityTableCode);//模版实体表编号
                    templCardMap.put("templateEntityName",templateEntityName);//模版实体表名称
                    if(templatCardMapper.addTemplateInputCard(templCardMap)){//添加模版实体表数据及录入样式列
                        List<Map<String,String>> inputMap=newInputViewMapper.getAllInputView(listMap.get(i).get("TABLECODE")+"");//录入视图列
                        if(inputMap!=null&&inputMap.size()>0){
                            for (int j = 0; j <inputMap.size() ; j++) {
                                inputMap.get(j).put("INTERFACECARDCODE",StringUtil.getUuid());
                                inputMap.get(j).put("TABLECODE",templateInputTableCode);
                                newInputView.saveTemplatInput(inputMap.get(j));//添加录入样式列
                            }
                        }
                        List<Map<String,Object>> columnMap=tableMapper.getEntityTableColumn(listMap.get(i).get("TABLECODE")+"");//查询旗下数据列
                        if(columnMap!=null&&columnMap.size()>0){
                            for (int j = 0; j < columnMap.size(); j++) {
                                Map<String,String> cdMap=new HashMap<>();
                                for (String sr:columnMap.get(j).keySet()) {
                                    cdMap.put(sr,columnMap.get(j).get(sr)+"");
                                }
                                cdMap.put("COLUMNCODE",StringUtil.getUuid());
                                cdMap.put("TABLECODE",templateEntityTableCode);
                                tableMapper.createClassColumnDescription(cdMap);//添加模版表列描述
                            }
                        }
                    }


                }

            }else{
                bool=false;
            }
        }else {
            bool=false;
        }
        return bool;
    }

//    //添加模版classLeve
//    public boolean getAttrs(Map<String, String> attrs) {
//        boolean bool=true;
//        String classCode="";
//        List<String> tableList=new ArrayList<>();
//        try {
//            //底层门类
//            String nodeCode = attrs.get("NODECODE") + "";
//            classCode= attrs.get("CLASSCODE") + "";
//            List<Map<String, String>> listMap = classLevelService.getTableByNodeCode(nodeCode);//获取全部实体表 tableCode
//            if (listMap != null && listMap.size() > 0) {
//                for (int i = 0; i < listMap.size(); i++) {
//                    String tableCode= listMap.get(i).get("TABLECODE");
////                    String tableCode= StringUtil.getRandomStr(6);
//                    tableList.add(tableCode);
//                    Map<String,String> mmm=new HashMap<>();
//                    mmm.put("LEVELCODE",String.valueOf((new Date()).getTime()) + (int)(100.0D + Math.random() * 1000.0D));
//                    mmm.put("CLASSCODE",classCode);
//                    mmm.put("RELATIONLEVEL",i+"");
//                    mmm.put("TABLECODE",tableCode);
//                    mmm.put("ISNECESSARY","T");
//                    mmm.put("CHINESENAME",listMap.get(i).get("NAME"));
//                    bool=tableMapper.createClassLevel(mmm);//添加模版
//                    Map<String,String> tableMap=new HashMap<>();
//                    tableMap.put("tableCode",tableCode);
//                    List<Map<String,String>> columnMap=tableMapper.getTableColumnInfoByTableCode(tableMap);
//                    for (int j = 0; j < columnMap.size(); j++) {
//                        columnMap.get(j).put("COLUMNCODE",String.valueOf((new Date()).getTime()) + (int)(100.0D + Math.random() * 1000.0D));
//                         bool=tableMapper.createClassColumnDescription(columnMap.get(j));
//                         if(!bool){
//                             tableMapper.delClassColumnDes(tableCode);
//                         }
//                    }
//                }
//            }
//        }catch (Exception e){
//            bool=false;
//            System.out.println("模版添加ClassLeve失败"+e.getMessage());
//            tableMapper.delTemplate(classCode);
//            for (int i = 0; i <tableList.size() ; i++) {
//                tableMapper.delClassColumnDes(tableList.get(i));
//            }
//            return bool;
//        }
//        return bool;
//    }

    /**
     * 删除已选模版
     *
     * @param
     * @return
     */
    @Override
    public Boolean delTemplate(String classCode,String tableCode) {
        boolean bool=true;
        try {
            tableMapper.delClassLeveByClassCode(classCode);//删除模版纪录
            tableMapper.delTemplate(classCode);
            Map<String,String> templatMap=templatCardMapper.getTemplateCardByTableCode(tableCode);
//        删除模版表 删除模版实体列表 模版录入列表 CLASSTABLECODE,INPUTTABLECODE,TEMPLATENAME,TYPE,ENTITYTABLECODE,ENTITYNAME
            for (String str: templatMap.keySet()) {
                if("INPUTTABLECODE".equals(str)){
                    String INPUTTABLECODE=templatMap.get("INPUTTABLECODE");
                    newInputViewMapper.delAllInputViewByTableCode(INPUTTABLECODE);//模版录入列信息
                }else if("ENTITYTABLECODE".equals(str)){
                    String ENTITYTABLECODE=templatMap.get("ENTITYTABLECODE");//模版实体列信息
                    tableMapper.delClassColumnDes(ENTITYTABLECODE);//删除模版表字段列
                }
            }
            bool=templatCardMapper.delTemplatColumns(tableCode);
            if(bool){
                System.out.println("删除模版成功"+bool);
            }

        }catch (Exception e){
            bool=false;
        }
        return bool;//删除模版
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
        List<Map<String,String>> mapList=tableMapper.getSelectTableByClassCode(parmMap);
        return mapList;
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
        List<String> tableCodeList=new ArrayList<>();
        boolean bool = true;
        Map<String, String> tableCodeMap=new HashMap<>();//存放tableCode
        String uuid="";
        try {
            String classcodeNO=String.valueOf((new Date()).getTime()) + (int)(100.0D + Math.random() * 1000.0D);
            //创建底层门类
            Map<String, String> attrs = (Map<String, String>) parmMap.get("attrs");
            uuid= "ucls" + StringUtil.getDate(2) + StringUtil.getRandom(1000, 10000) + 1;
            Map<String, String> treeT = new HashMap<>();//参数
            treeT.put("NODECODE", uuid);//唯一编号
            treeT.put("PARENTCODE", attrs.get("NODECODE") + "");
            treeT.put("NAME", parmMap.get("name").toString());
            treeT.put("TYPE", parmMap.get("type").toString());//C
            treeT.put("CLASSCODE", classcodeNO);//表
            treeT.put("CLASSTABLECODE", attrs.get("templateCode") + "");
            treeT.put("TABLECODE", "");
            treeT.put("SERIAL", "");//最大的
            int result = tableMapper.addTableTreeInfo(treeT);//父节点

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
                                        tableCodeList.add(tempMap.get("tableCode")+"");//获取表默认样式tableCode
                                        tableName = tempMap.get("name") + "";//获取创建的表名
                                        //创建用户角色表权限
                                        Map<String, String> sysMap = new HashMap<>();
                                        sysMap.put("userCode", listUUID.get(i + 1));
                                        sysMap.put("tableName", tableName);
                                        int result2 = tableMapper.addSystemUseRRoleTable(sysMap);//添加表权限
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
                                        tableCodeMap.put(tableCode,tableCode);//存放tableCode
                                        Map<String, String> tableDescriptionMap = new HashMap<>();//表描述
                                        boolean isOK = true;//判断是只有一个主键bi
                                        for (String temp : tempMap.keySet()) {
                                            treeT.put("CLASSCODE", classcodeNO);//底层门类字段
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
                                                            int count = tableMapper.addTableColumnDescription(stringBuffer);//保存字段到纪录表 CLASSCOLUMNDESCRIPTION
                                                            //保存视图列表
                                                            String visible = tempColumnsMap.get("VISIBLE") + "";//是否可见 T全部添加数据
                                                            if("T".equals(visible)){
                                                                Map<String, String> viewMap = new HashMap<>();//字段记录表集合//添加到纪录表
                                                                viewMap.put("listCode", String.valueOf((new Date()).getTime()) + (int)(100.0D + Math.random() * 1000.0D));
                                                                viewMap.put("columnCode", String.valueOf(tableColumnDMap.get("COLUMNCODE")));
                                                                viewMap.put("serial", String.valueOf(j));
                                                                viewMap.put("title", String.valueOf(tableColumnDMap.get("CHINESENAME")));
                                                                count=tableMapper.addOneColumn(viewMap);
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
                                                        Map<String, String> SQLMap = new HashMap<>();
                                                        SQLMap.put("SQL", sql.toString());//添加表
                                                        int result4 = tableMapper.addTable(SQLMap);//创建实体表
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
                                        //调用添加表描述  唯一表编号
                                        tableDescriptionMap.put("tableCode", tableCode);
                                        tableDescriptionMap.put("systemType", "D");//追加条件
                                        tableDescriptionMap.put("aggregate", "A");//追加条件
                                        tableMapper.addTableDescription(tableDescriptionMap);
                                    }

                                }

                            }
                    }
                }//for
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            bool = false;
        } finally {
            System.out.println("是否成功：" + bool);
            if(bool){
                for (String tableCode:tableCodeMap.keySet()) {
                    if(tableCodeList.size()>0){
                        for (int i = 0; i <tableCodeList.size() ; i++) {
                            Map<String,String> templatMap=templatCardMapper.getTemplateInputCardByTableCode(tableCodeList.get(i));//获取模版关联表
                            String inputTableCode=templatMap.get("INPUTTABLECODE");
                            List<Map<String,String>> listInputMap=newInputViewMapper.getAllInputView(inputTableCode);
                            System.out.println(inputTableCode);
                            if(listInputMap!=null&&listInputMap.size()>0){
                                for (int j = 0; j <listInputMap.size() ; j++) {
                                    try {
                                        listInputMap.get(j).put("INTERFACECARDCODE",StringUtil.getUuid());
                                        listInputMap.get(j).put("TABLECODE",tableCode);
                                        newInputView.saveTemplatInput(listInputMap.get(j));//添加录入样式列
                                    }catch (Exception e){
                                        System.out.println(tableCode+"录入失败"+e.getMessage());
                                        Map<String,String> nodecode=new HashMap<>();
                                        nodecode.put("NODECODE",uuid);
                                        nodecode.put("TYPE","C");
                                        classLevelService.delTreeLC(nodecode);
                                        newInputViewMapper.delAllInputViewByTableCode(tableCode);//失败删除全部
                                        bool=false;
                                        System.out.println(e.getMessage());
                                    }
                                }
                            }

                        }

                    }else{
                        boolean bool12=newInputView.saveInputView(tableCode,null,uuid);
                        if(bool12){
                            System.err.println("创建默认录入列表成功！--"+bool12);
                        }else{
                            bool=false;
                        }
                    }
                }
            }
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
//            if(result>0){
//                System.out.println("添加字段关系创建成功--"+tableRelationMap);
//            }
        }catch (Exception e){
            System.err.println("添加字段关系失败--"+e.getMessage());
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

            //判断字段是否为空 如果为空那么添加时就不需要添加该字段
            sql = getSQLTableColumnDescription(objectMap);
            int result2 = tableMapper.addTableColumnDescription(sql);
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
            bool=tableMapper.upFieldTableDescription(field);
            if(!bool){
                System.out.println("修改描述表失败-1");
                return bool;
            }
            bool=classLevelMapper.upNameByNodeCode(field);
            if(!bool){
                System.out.println("修改节点表ClassNode失败-1");
                return bool;
            }
        }catch (Exception e){
            System.err.println("修改描述表信息"+e.getMessage());
            bool=false;
            return bool;
        }
        return bool;
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
//            if(result>1){
//                System.err.println("表关系修改成功"+fieldRelation);
//            }
        }catch (Exception e){
            System.err.println("表关系修改失败"+e.getMessage());
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
//                System.out.println("删除表字段描述成功"+field);
            }
            res1=tableMapper.delFieldTableRelation(field);
            if(res1>0){
//                System.out.println("删除表字段关系成功"+field);
            }
            res1=tableMapper.delFieldTable(field);
            if(res1>0){
//                System.out.println("删除实体表字段成功"+field);
            }
            res1=tableMapper.delOneColumn(field);
            if(res1>0){
//                System.out.println("删除视图字段成功"+field);
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
//            if(res2>0){
//                System.out.println("删除表关系成功"+relationCode);
//            }

        }catch (Exception e){
            bool=false;
            System.err.println(e.getMessage());
        }finally {
            return bool;
        }
    }


    /**
     * 修改字段信息
     * @param field
     * @return
     */
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
//        System.err.println("sql---" + sql);
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
//            if(res1>0){
//                System.out.println("修改实体表"+sql);
//            }
            int res2=tableMapper.upFieldTableColumnDescription(columns);//修改列纪录表内容
//            if(res2>0){
//                System.out.println("修改纪录表"+columnSql);
//            }
        }catch (Exception e){
            bool=false;
        }finally {
            return bool;
        }
    }


}