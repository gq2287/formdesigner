package com.wskj.project.service.impl;

import com.wskj.project.dao.TableInputViewMapper;
import com.wskj.project.dao.TableMapper;
import com.wskj.project.model.Property;
import com.wskj.project.model.UIBean;
import com.wskj.project.service.TableInputViewService;
import com.wskj.project.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.*;

@Service
public class TableInputViewServiceImpl implements TableInputViewService {
    @Resource
    private TableInputViewMapper tableInputViewMapper;
    @Resource
    private TableMapper tableMapper;
    @Override
    public List<Map<String, String>> getTableInputView(String tableCode) {
        System.out.println(tableCode);
        List<Map<String, String>> inputList=null;
        List<Map<String, String>> tagMapList=new ArrayList<>();
        try {
            inputList=tableInputViewMapper.getTableInputView(tableCode);
            if(inputList!=null&&inputList.size()>0){
                getSplitTableInputView(tableCode,inputList,tagMapList);//转换成属性对象
                System.out.println("获取录入视图成功"+tableCode);
            }else{
                //如果没有就添加默认的
                List<Map<String, Object>> conlumnList=tableMapper.getEntityTableColumnByVisible(tableCode);//获取选中表数据列
                if(conlumnList!=null&&conlumnList.size()>0){
                    Boolean bool=getAddColumn(tableCode,conlumnList);//获取当前添加数据
                    if(bool){
                        System.out.println("添加录入数据成功"+conlumnList);
                        Map<String,String> parIndexMap=new HashMap<>();
                        parIndexMap.put("indexCode", StringUtil.getDate(2)+StringUtil.getRandomStr(6));
                        parIndexMap.put("tableCode",tableCode);
                        bool=tableInputViewMapper.addTableIndex(parIndexMap);
                        if(bool){
                            System.out.println("添加索引表数据成功"+parIndexMap);
                        }
                    }
                    inputList=tableInputViewMapper.getTableInputView(tableCode);//获取当前录入列表数据
                    if(inputList!=null&&inputList.size()>0){
                        getSplitTableInputView(tableCode,inputList,tagMapList);//转换成属性对象
                        System.out.println("获取录入视图成功");
                    }else{
                        System.out.println("获取异常"+tableCode);
                    }
                }
            }
        }catch (Exception e){
            System.err.println("获取录入视图失败！"+e.getMessage()+"---"+tableCode);
        }
        return tagMapList;
    }


    /**
     * 添加录入列默认数据
     * @param tableCode 表编号
     * @param listColumn 标签列
     * @return
     */
    private Boolean getAddColumn(String tableCode,List<Map<String,Object>> listColumn){
        Boolean bool=true;
        try {
            int top = 6;
            int step = 35;
            int labelLeft = 92;
            int ctrlLeft = 320;
            int CONTAINERINDEX = 1;
            int CONTROLTYPE = 0;
            int LOADNO = 6;
            int TABINDEX = 1;
            getDefaultCardDataParamBytableCode(tableCode);
            for (int i = 0; i < listColumn.size(); i++) {
                Map<String,Object> pstmtMap=listColumn.get(i);//临时列集合
                Map<String,Object> inputMap=new HashMap<>();//条件集合
                inputMap.put("INTERFACECARDCODE",String.valueOf((new Date()).getTime()) + (int)(100.0D + Math.random() * 1000.0D));//录入界面设置编号
                inputMap.put("TABLECODE",tableCode);//表编号
                inputMap.put("USERCODE","ISA");//用户编号
                String CONTROLNAME = null;
                String PROPERTIESINFO1 = null;
                String CONTAINER = null;
                String remark = null;
                for (String col:pstmtMap.keySet()) {
                    if("INPUTTYPE".equals(col)){
                        if ("T".equals(pstmtMap.get(col))) {
                            CONTROLNAME = "txtInfos";
                            CONTROLTYPE = 12;
                            CONTAINER = "picContainerBack";
                            PROPERTIESINFO1 = "@HEIGHT|2|315@WIDTH|2|8000@FORECOLOR|3|-2147483640@FONTSIZE|3|9@FONTNAME|1|宋体@BORDERSTYLE|2|1@LEFT|2|" + ctrlLeft * 15 + "@BACKCOLOR|3|-2147483643@APPEARANCE|2|1" + "@TOP|2|" + top * 15 + "@TEXT|1|" + pstmtMap.get("CHINESENAME") + "@TAG|1|" + pstmtMap.get("NAME");
                            remark = "文本信息";
                        } else if ("S".equals(pstmtMap.get(col))) {
                            CONTROLNAME = "cboInfos";
                            CONTROLTYPE = 13;
                            CONTAINER = "picContainerFront";
                            PROPERTIESINFO1 = "@APPEARANCE|2|1@LEFT|2|" + ctrlLeft * 15 + "@TOP|2|" + top * 15 + "@HEIGHT|2|330@WIDTH|2|2475@FONTNAME|1|宋体@FONTSIZE|3|9@FORECOLOR|3|2147483656" + "@BACKCOLOR|3|2147483653@TAG|1|" + pstmtMap.get("NAME") + "@TEXT|1|" + pstmtMap.get("CHINESENAME");
                            remark = "下拉框信息";
                        } else {
                            if (!"F".equals(pstmtMap.get(col))) {
                                continue;
                            }
                            CONTROLNAME = "txtInfos";
                            CONTROLTYPE = 12;
                            CONTAINER = "picContainerBack";
                            PROPERTIESINFO1 = "@HEIGHT|2|315@WIDTH|2|2500@FORECOLOR|3|-2147483640@FONTSIZE|3|9@FONTNAME|1|宋体@BORDERSTYLE|2|1@LEFT|2|" + ctrlLeft * 15 + "@BACKCOLOR|3|-2147483643@APPEARANCE|2|1" + "@TOP|2|" + top * 15 + "@TEXT|1|" + pstmtMap.get("CHINESENAME") + "@TAG|1|" + pstmtMap.get("NAME");
                            remark = "文本信息";
                        }
                        //添加录入列
                        inputMap.put("CONTROLNAME", CONTROLNAME);
                        inputMap.put("CONTROLINDEX", String.valueOf(CONTAINERINDEX));
                        inputMap.put("CONTROLTYPE", String.valueOf(CONTROLTYPE));
                        inputMap.put("ISEXIST", "T");
                        inputMap.put("CONTAINER", CONTAINER);
                        inputMap.put("CONTAINERINDEX", String.valueOf(-1));
                        inputMap.put("LOADNO", String.valueOf(LOADNO));
                        inputMap.put("TABINDEX", String.valueOf(TABINDEX));
                        inputMap.put("FIELDNAME",String.valueOf(pstmtMap.get("NAME")) );
                        inputMap.put("PROPERTIESINFO1", PROPERTIESINFO1);
                        inputMap.put("REMARK", remark);
                        int result =tableInputViewMapper.addTableInputViewColumn(inputMap);//添加到录入表
                        if(result>0){
                            System.out.println("主---添加成功！"+inputMap);
                        }else{
                            System.err.println("主---添加失败！"+inputMap);
                        }
                        //添加副表
                        inputMap.put("INTERFACECARDCODE",String.valueOf((new Date()).getTime()) + (int)(100.0D + Math.random() * 1000.0D));//录入界面设置编号
                        CONTROLNAME = "lblInfos";
                        CONTROLTYPE = 11;
                        CONTAINER = "picContainerBack";
                        PROPERTIESINFO1 = "@CAPTION|1|" + pstmtMap.get("CHINESENAME") + "@BORDERSTYLE|2|0@APPEARANCE|2|1@LEFT|2|" + labelLeft * 15 + "@TOP|2|" + top * 15 + "@HEIGHT|2|315@WIDTH|2|1575@FONTNAME|1|宋体@FONTSIZE|3|9@FORECOLOR|3|-2147483630@BACKCOLOR|3|-2147483633@TAG|1|";
                        remark = "注释信息";
                        inputMap.put("CONTROLNAME", CONTROLNAME);
                        inputMap.put("CONTROLINDEX", String.valueOf(CONTAINERINDEX));
                        inputMap.put("CONTROLTYPE", String.valueOf(CONTROLTYPE));
                        inputMap.put("ISEXIST", "T");
                        inputMap.put("CONTAINER", CONTAINER);
                        inputMap.put("CONTAINERINDEX", String.valueOf(-1));
                        ++LOADNO;
                        inputMap.put("LOADNO", String.valueOf(LOADNO));
                        inputMap.put("TABINDEX", String.valueOf(TABINDEX));
                        inputMap.put("FIELDNAME",String.valueOf(pstmtMap.get("NAME")) );
                        inputMap.put("PROPERTIESINFO1", PROPERTIESINFO1);
                        inputMap.put("REMARK", remark);
                        result =tableInputViewMapper.addTableInputViewColumn(inputMap);//添加到录入表
                        if(result>0){
                            System.out.println("副---添加成功！"+inputMap);
                        }else{
                            System.err.println("副---添加失败！"+inputMap);
                        }
                        CONTAINERINDEX=1+CONTAINERINDEX;
                        top = top+step;
                        System.out.println(top);
                        LOADNO=1+LOADNO;
                        TABINDEX=1+TABINDEX;
                    }else {
                        continue;
                    }
                }
            }
        }catch (Exception e){
            bool=false;
            System.out.println("添加录入列部分失败"+e.getMessage());
        }finally {
            return bool;
        }
    }

    /**
     * 添加默认的录入列
     * @param tableCode
     */
    private void getDefaultCardDataParamBytableCode(String tableCode)throws SQLException{
        Map<String,String> inputMap=new HashMap<>();//条件集合
        inputMap.put("INTERFACECARDCODE",String.valueOf((new Date()).getTime()) + (int)(100.0D + Math.random() * 1000.0D));//录入界面设置编号
        inputMap.put("TABLECODE",tableCode);//表编号
        inputMap.put("USERCODE","ISA");//用户编号
        inputMap.put("CONTROLNAME","frmRecordBrowse");//是否还存在
        inputMap.put("CONTROLINDEX",-1+"");//字段名
        inputMap.put("CONTROLTYPE",1+"");//图片容器索引号
        inputMap.put("ISEXIST","T");//控件索引号
        inputMap.put("LOADNO","1");//控件名称
        inputMap.put("PROPERTIESINFO1","@CAPTION|1|记录操作界面设计@ForeColor|3|-2147483630@BackColor|3|-2147483633@HEIGHT|2|11640@WIDTH|2|19320");//属性信息1
        int result =tableInputViewMapper.defAddTableInputViewColumn(inputMap);//添加到录入表
        if(result>0){
            System.out.println("1---添加成功！"+inputMap);
        }else{
            System.err.println("1---添加失败！"+inputMap);
        }
        inputMap.put("INTERFACECARDCODE",String.valueOf((new Date()).getTime()) + (int)(100.0D + Math.random() * 1000.0D));//录入界面设置编号
        inputMap.put("TABLECODE",tableCode);
        inputMap.put("CONTROLNAME","picContainerBack");
        inputMap.put("CONTROLINDEX",-1+"");
        inputMap.put("CONTROLTYPE",2+"");
        inputMap.put("ISEXIST","T");
        inputMap.put("CONTAINER","frmRecordBrowse");
        inputMap.put("CONTAINERINDEX","-1");
        inputMap.put("LOADNO","2");
        inputMap.put("PROPERTIESINFO1","@ALIGN|1|0@ForeColor|3|-2147483630@BackColor|3|-2147483633@HEIGHT|2|10070@WIDTH|2|19320");//属性信息1
        result =tableInputViewMapper.defAddTableInputViewColumn(inputMap);//添加到录入表
        if(result>0){
            System.out.println("2---添加成功！"+inputMap);
        }else{
            System.err.println("2---添加失败！"+inputMap);
        }
        inputMap.put("INTERFACECARDCODE",String.valueOf((new Date()).getTime()) + (int)(100.0D + Math.random() * 1000.0D));//录入界面设置编号
        inputMap.put("CONTROLNAME","picContainerFront");
        inputMap.put("CONTROLINDEX",-1+"");
        inputMap.put("CONTROLTYPE",3+"");
        inputMap.put("ISEXIST","T");
        inputMap.put("CONTAINER","picContainerBack");
        inputMap.put("CONTAINERINDEX","-1");
        inputMap.put("LOADNO","3");
        inputMap.put("PROPERTIESINFO1","@ALIGN|1|1@ForeColor|3|-2147483630@BackColor|3|-2147483633@HEIGHT|2|10070@WIDTH|2|19320");//属性信息1
        result =tableInputViewMapper.defAddTableInputViewColumn(inputMap);//添加到录入表
        if(result>0){
            System.out.println("3---添加成功！"+inputMap);
        }else{
            System.err.println("3---添加失败！"+inputMap);
        }

        inputMap.put("INTERFACECARDCODE",String.valueOf((new Date()).getTime()) + (int)(100.0D + Math.random() * 1000.0D));//录入界面设置编号
        inputMap.put("CONTROLNAME","picContainerFront");
        inputMap.put("CONTROLINDEX",-1+"");
        inputMap.put("CONTROLTYPE",4+"");
        inputMap.put("ISEXIST","T");
        inputMap.put("CONTAINER","picContainerBack");
        inputMap.put("CONTAINERINDEX","-1");
        inputMap.put("LOADNO","4");
        inputMap.put("PROPERTIESINFO1","@VALUE|2|0@VISIBLE|1|F@HEIGHT|2|4365@WIDTH|2|240");//属性信息1
        result =tableInputViewMapper.defAddTableInputViewColumn(inputMap);//添加到录入表
        if(result>0){
            System.out.println("4---添加成功！"+inputMap);
        }else{
            System.err.println("4---添加失败！"+inputMap);
        }
        inputMap.put("INTERFACECARDCODE",String.valueOf((new Date()).getTime()) + (int)(100.0D + Math.random() * 1000.0D));//录入界面设置编号
        inputMap.put("CONTROLNAME","vsbHScroll");
        inputMap.put("CONTROLINDEX",-1+"");
        inputMap.put("CONTROLTYPE",5+"");
        inputMap.put("ISEXIST","T");
        inputMap.put("CONTAINER","picContainerFront");
        inputMap.put("CONTAINERINDEX","-1");
        inputMap.put("LOADNO","1");
        inputMap.put("PROPERTIESINFO1","@VALUE|2|100@VISIBLE|1|F@HEIGHT|2|255@WIDTH|2|9225");//属性信息1
        result =tableInputViewMapper.defAddTableInputViewColumn(inputMap);//添加到录入表
        if(result>0){
            System.out.println("5---添加成功！"+inputMap);
        }else{
            System.err.println("5---添加失败！"+inputMap);
        }

    }

    /**
     * 获取分割后的数据
     * @param tableCode 表编号
     * @param mapList 数据库查询数据
     * @param tagMapList  样式集合
     * @return
     */
    private void getSplitTableInputView(String tableCode,List<Map<String,String>> mapList,List<Map<String,String>> tagMapList){
        List<UIBean> uiBeanList=new ArrayList<>();
        List<Map<String,String>> uiMapList=new ArrayList<>();//样式集合
        if(mapList!=null){
            for (int i = 0; i < mapList.size(); i++) {
                for (String prop:mapList.get(i).keySet()) {
                    if("PROPERTIESINFO1".equals(prop)&&mapList.get(i).get(prop)!=null&&!"".equals(mapList.get(i).get(prop))){
                        UIBean uiBean=getUIBean(mapList.get(i).get(prop));
                        uiBeanList.add(uiBean);
                    }else {
                        continue;
                    }
                }
            }
            getUIList(tableCode,uiBeanList,mapList,tagMapList);
        }else{
            System.err.println("参数为录入列数据集合为null");
        }
    }

    /**
     * 根据@分割字符
     * @param prop
     * @return
     */
    private UIBean getUIBean(String prop){
        UIBean uiBean=new UIBean();
        int start = 0;
        int end;
        for(int i = 0; i < prop.length(); i = end + 1) {
            start = prop.indexOf("@", start);//获取第一个@
            end = prop.indexOf("@", start + 1);//获取第二个@
            if (end == -1) {
                end = prop.length();
            }
            Property property = new Property();
            property.setPropertyString(prop.substring(start, end));
            property.getProperty();
            getUI(uiBean,property);
            start = end;
        }
        return uiBean;
    }

    /**
     * 获取录入样式
     * @param uiBean 录入标签样式
     * @param property 属性信息
     */
    private void getUI(UIBean uiBean,Property property){
        String[] name = new String[]{"CAPTION", "TAG", "FORECOLOR", "BACKCOLOR", "HEIGHT", "WIDTH", "ALIGN", "BORDERSTYLE", "APPEARANCE", "LEFT", "TOP", "FONT", "FONTNAME", "FONTSIZE", "FONTBOLD", "FONTITALIC", "TEXT", "VALUE", "VISIBLE", "ROWHEIGHT"};
        int index = -1;

        for(int i = 0; i < name.length; ++i) {
            if (property.getName().equals(name[i])) {
                index = i;
            }
        }

        switch(index) {
            case 0:
                uiBean.setCaption(property.getValue());
                break;
            case 1:
                uiBean.setTag(property.getValue());
                break;
            case 2:
                uiBean.setForeColor(property.getValue());
                break;
            case 3:
                uiBean.setBackColor(property.getValue());
                break;
            case 4:
                uiBean.setHeight(property.getValue());
                break;
            case 5:
                uiBean.setWidth(property.getValue());
                break;
            case 6:
                uiBean.setAlign(property.getValue());
                break;
            case 7:
                uiBean.setBorderStyle(property.getValue());
                break;
            case 8:
                uiBean.setAppearance(property.getValue());
                break;
            case 9:
                uiBean.setLeft(property.getValue());
                break;
            case 10:
                uiBean.setTop(property.getValue());
                break;
            case 11:
                uiBean.setFont(property.getValue());
                break;
            case 12:
                uiBean.setFontName(property.getValue());
                break;
            case 13:
                uiBean.setFontSize(property.getValue());
                break;
            case 14:
                uiBean.setFontBold(property.getValue());
                break;
            case 15:
                uiBean.setFontItalic(property.getValue());
                break;
            case 16:
                uiBean.setText(property.getValue());
                break;
            case 17:
                uiBean.setValue(property.getValue());
                break;
            case 18:
                if (property.getValue().equals("F")) {
                    uiBean.setVisible("False");
                } else {
                    uiBean.setVisible("True");
                }
                break;
            case 19:
                uiBean.setRowHeight( property.getValue());
        }
    }

    /**
     * 样式集合
     * @param uiBeanList 实例样式集合
     * @param mapList 数据库样式
     * @param uiMapList 存放录入独立样式
     * @return
     */
    private void getUIList(String tableCode,List<UIBean> uiBeanList,List<Map<String,String>> mapList, List<Map<String,String>> uiMapList){
        String backgroundColor = "white";
        float XPY = 1.0F;
        for (int i = 0; i <uiBeanList.size() ; i++) {
            Map<String,String> uiMap=new HashMap<>();//获取独立样式
            String[] as = new String[]{"frmRecordBrowse", "txtInfos", "cboInfos", "lblInfos"};//标签名称
            int index = -1;
            String name =mapList.get(i).get("CONTROLNAME");//获取名称
            for(int i2 = 0; i2 < as.length; ++i2) {
                if (name.equals(as[i2])) {
                    index = i2;
                }
            }
            UIBean uibean2 = (UIBean)uiBeanList.get(i);//获取一个UIBean
            String tempFieldValue = "示例文字";
            switch(index) {
                case 0://
                    uiMap.put("id","P@"+mapList.get(i).get("INTERFACECARDCODE")+"@"+name+ "@" + uibean2.getText() + "@" + uibean2.getCaption());//获取名称;
                    uiMap.put("style","TOP:0px;LEFT:0px;Z-INDEX:0;BACKGROUND-COLOR:" + backgroundColor);
                    if (uibean2.getFloat(uibean2.getHeight()) / 15.0F > 500.0F) {
                        uiMap.put("HEIGHT","500px");
                    } else {
                        uiMap.put("HEIGHT",uibean2.getFloat(uibean2.getHeight()) / 15.0F+"px");
                    }
                    if (uibean2.getFloat(uibean2.getWidth()) / 15.0F > 900.0F) {
                        uiMap.put("POSITION","absolute");
                        uiMap.put("WIDTH","900px");
                    } else {
                        uiMap.put("POSITION","absolute");
                        uiMap.put("WIDTH",uibean2.getFloat(uibean2.getWidth()) / 15.0F + "px");
                    }
                    break;
                case 1:
                    tempFieldValue = uibean2.getText();
                    if (uibean2.getFloat(uibean2.getHeight()) / 15.0F > 30.0F) {//标签类型v高度大于30F就是文本域，否则input框
                        uiMap.put("tag","textarea");
                    } else {
                        uiMap.put("tag","input");
                    }
                    uiMap.put("name",uibean2.getTag());
                    uiMap.put("maxlength", getMaxLength(uibean2.getTag(), tableCode));
                    uiMap.put("value", tempFieldValue);
                    uiMap.put("id","showinfo@" + mapList.get(i).get("INTERFACECARDCODE") + "@" + mapList.get(i).get("CONTROLNAME") + "@" + uibean2.getText() + "@" + uibean2.getCaption() );
                    uiMap.put("readonly", "readonly" );
                    uiMap.put("style","BORDER:1 solid gray;cursor: move;BORDER-STYLE:" + uibean2.getborder(uibean2.getBorderStyle(), uibean2.getAppearance()));
                    uiMap.put("LEFT",uibean2.getFloat(uibean2.getLeft()) / 15.0F + "px");
                    uiMap.put("WIDTH",uibean2.getFloat(uibean2.getWidth()) / 15.0F + "px;");
//                    uiMap.put("COLOR","#"+uibean2.getIntHex(uibean2.getForeColor()) );
                    uiMap.put("FONT-FAMILY",uibean2.getFont() );
                    uiMap.put("FONT-SIZE",uibean2.getFontSize() +"pt" );
                    uiMap.put("FONT-STYLE",uibean2.getFontItalic()  );
                    uiMap.put("FONT-WEIGHT",uibean2.getFontBold() );
                    uiMap.put("BACKGROUND-COLOR",backgroundColor );
                    uiMap.put("Z-INDEX",(i + 4) +"" );
                    uiMap.put("POSITION", "absolute" );
                    uiMap.put("TOP",(uibean2.getFloat(uibean2.getTop()) / 15.0F + XPY) + "px" );
                    uiMap.put("HEIGHT", uibean2.getFloat(uibean2.getHeight()) / 15.0F + "px" );
                    break;
                case 2:
                    tempFieldValue = uibean2.getText();
                    uiMap.put("id","showinfo@" +mapList.get(i).get("INTERFACECARDCODE") + "@" + mapList.get(i).get("CONTROLNAME") + "@" + uibean2.getText() + "@" + uibean2.getCaption() );//获取名称);
                    uiMap.put("readonly", "readonly" );
                    uiMap.put("name", uibean2.getTag() );
                    uiMap.put("size", 1+"" );
                    uiMap.put("style","BORDER:1 solid gray;cursor: move;BORDER-STYLE:"+uibean2.getborder(uibean2.getBorderStyle(), uibean2.getAppearance()));
                    uiMap.put("LEFT",uibean2.getFloat(uibean2.getLeft()) / 15.0F + "px");
                    uiMap.put("WIDTH",uibean2.getFloat(uibean2.getWidth()) / 15.0F + "px;");
//                    uiMap.put("COLOR","#"+uibean2.getIntHex(uibean2.getForeColor()) );
                    uiMap.put("FONT-FAMILY",uibean2.getFont() );
                    uiMap.put("FONT-SIZE",uibean2.getFontSize() +"pt" );
                    uiMap.put("FONT-STYLE",uibean2.getFontItalic()  );
                    uiMap.put("FONT-WEIGHT",uibean2.getFontBold() );
                    uiMap.put("BACKGROUND-COLOR",backgroundColor );
                    uiMap.put("Z-INDEX",(i + 4) +"" );
                    uiMap.put("POSITION", "absolute" );
                    uiMap.put("TOP",(uibean2.getFloat(uibean2.getTop()) / 15.0F + XPY) + "px" );
                    uiMap.put("HEIGHT", uibean2.getFloat(uibean2.getHeight()) / 15.0F + "px" );
                    uiMap.put("selected", "selected" );
                    uiMap.put("label",tempFieldValue );
                    uiMap.put("value",tempFieldValue );
                    uiMap.put("tag","select");
                    break;
                case 3:
                    uiMap.put("id","showinfo@" +mapList.get(i).get("INTERFACECARDCODE") + "@" + mapList.get(i).get("CONTROLNAME") + "@" + uibean2.getText() + "@" + uibean2.getCaption() );//获取名称);
                    uiMap.put("readonly", "readonly" );
                    uiMap.put("style","BORDER:1 solid gray;cursor: move");
                    uiMap.put("LEFT",uibean2.getFloat(uibean2.getLeft()) / 15.0F + "px");
                    uiMap.put("WIDTH",uibean2.getFloat(uibean2.getWidth()) / 15.0F + "px;");
//                    uiMap.put("COLOR","#"+uibean2.getIntHex(uibean2.getForeColor()) );
                    uiMap.put("FONT-FAMILY",uibean2.getFont() );
                    uiMap.put("FONT-SIZE",uibean2.getFontSize() +"pt" );
                    uiMap.put("FONT-STYLE",uibean2.getFontItalic()  );
                    uiMap.put("FONT-WEIGHT",uibean2.getFontBold() );
                    uiMap.put("BACKGROUND-COLOR",backgroundColor );
                    uiMap.put("Z-INDEX",(i + 4) +"" );
                    uiMap.put("POSITION", "absolute" );
                    uiMap.put("TOP",(uibean2.getFloat(uibean2.getTop()) / 15.0F + XPY) + "px" );
                    uiMap.put("HEIGHT", uibean2.getFloat(uibean2.getHeight()) / 15.0F + "px" );
                    uiMap.put("caption", uibean2.getCaption() );
                    uiMap.put("tag","label");
                    break;
            }
            if(uiMap!=null&&uiMap.size()>0){
                uiMapList.add(uiMap);
            }
        }
    }

    private String getMaxLength(String tag,String tableCode){
        String max=tableInputViewMapper.getEntityTableColumnByTag(tag,tableCode);
        String returnValue="";
        if (!"".equals(max)) {
            max = max.trim();
            if ("0".equalsIgnoreCase(max)) {
                returnValue = "25";
            } else {
                returnValue = max;
            }
        }
        return  returnValue;
    }
}
