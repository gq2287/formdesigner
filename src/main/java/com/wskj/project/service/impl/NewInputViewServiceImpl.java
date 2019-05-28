package com.wskj.project.service.impl;

import com.wskj.project.dao.ClassLevelMapper;
import com.wskj.project.dao.NewInputViewMapper;
import com.wskj.project.dao.TableInputViewMapper;
import com.wskj.project.dao.TableMapper;
import com.wskj.project.service.NewInputViewService;
import com.wskj.project.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NewInputViewServiceImpl implements NewInputViewService {

    @Resource
    private NewInputViewMapper newInputViewMapper;
    @Resource
    private TableInputViewMapper tableInputViewMapper;
    @Resource
    private TableMapper tableMapper;
    @Resource
    private ClassLevelServiceImpl classLevelService;
    @Resource
    private ClassLevelMapper classLevelMapper;

    @Override
    public Map<String,Object> getInputView(String tableCode) {
        Map<String,Object> objectList=new HashMap<>();
        objectList.put("tagMapList",newInputViewMapper.getAllInputView(tableCode));//已添加列表
        objectList.put("noTagMapList",tableInputViewMapper.getInputColumnByTableCode(tableCode));//未添加列表
        return objectList;
    }

    /**
     * 添加底层门类成功后就创建录入界面
     * @param tableCode
     * @param parasUIList 有值录入界面进来 没值底层新建进来
     * @param nodeCode
     * @return
     */
    @Override
    public Boolean addInputView(String tableCode,List<Map<String,Object>> parasUIList,String nodeCode) {
        boolean bool=true;
        boolean trOk=true;//判断是默认创建还是录入界面二次调整(true录入界面。false底层添加)
        try {
            if(parasUIList==null){//不是录入界面进入
                //查询实体表数据列
                parasUIList=tableMapper.getEntityTableColumnByVisible(tableCode);//获取选中默认添加表数据列
                trOk=false;//默认创建时
            }
            if(parasUIList!=null&&parasUIList.size()>0){
                int index=1;
                for (int i = 0; i <parasUIList.size() ; i++) {
                    Map<String, Object> columnMap=parasUIList.get(i);
                     //字段名称
                    String fieldName=columnMap.get("NAME")!=null? String.valueOf(columnMap.get("NAME")):String.valueOf(columnMap.get("FIELDNAME"));
                    //字段中文名称
                    String chineseName=columnMap.get("CHINESENAME")!=null?String.valueOf(columnMap.get("CHINESENAME")):String.valueOf(columnMap.get("PROPERTIESINFO1"));
                    //字段类型
                    String inputType=String.valueOf(columnMap.get("INPUTTYPE"));
                    //行
                    String row=columnMap.get("ROWNO")!=null?String.valueOf(columnMap.get("ROWNO")):"1";
                    //列
                    String column=columnMap.get("COLUMNNO")!=null?String.valueOf(columnMap.get("COLUMNNO")):"1";

                    Map<String,String> inputView=new HashMap<>();//录入集合
                    inputView.put("INTERFACECARDCODE",StringUtil.getUuid());//列编号
                    inputView.put("TABLECODE",tableCode);
                    inputView.put("FIELDNAME",fieldName);//字段英文名
                    if("T".equals(inputType)){
                        inputView.put("CONTROLNAME","txtInfos");
                        inputView.put("CONTROLTYPE","11");
                        inputView.put("REMARK","文本信息");
                    }else if("S".equals(inputType)){
                        inputView.put("CONTROLNAME","cboInfos");
                        inputView.put("CONTROLTYPE","12");
                        inputView.put("REMARK","下拉框信息");
                    }else{
                        inputView.put("CONTROLNAME","txtInfos");
                        inputView.put("CONTROLTYPE","13");
                        inputView.put("REMARK","文本信息");
                    }
                    inputView.put("PROPERTIESINFO1",chineseName);//中文名称
                    inputView.put("ROWNO",row);
                    inputView.put("COLUMNNO",column);
                    if(trOk){
                        inputView.put("trNum",String.valueOf(columnMap.get("TRNUM")));
                    }else{
                        if((i+1)%3==0){//默认每排3个字段
                            inputView.put("trNum",""+index++);
                        }else{
                            inputView.put("trNum",index+"");
                        }
                    }
                    inputView.put("LOADNO",i+"");
                    newInputViewMapper.addInputViewColumn(inputView);
                }
            }
        }catch (Exception e){
            System.out.println(tableCode+"录入失败"+e.getMessage());
            Map<String,String> nodecode=new HashMap<>();
            nodecode.put("NODECODE",nodeCode);
            nodecode.put("TYPE","C");
            classLevelService.delTreeLC(nodecode);
            newInputViewMapper.delAllInputViewByTableCode(tableCode);//失败删除全部
            bool=false;
        }
        return bool;
    }

    @Override
    public int delInputView(String tableCode) {
        int result=newInputViewMapper.delAllInputViewByTableCode(tableCode);//删除全部
        return result;
    }

    @Override
    public List<Object> getTemplateViewByNodeCode(String nodeCode) {
        List<Map<String, String>> classCL = classLevelMapper.getClassCL(nodeCode);// 获取到 treeI 子节点
        List<Object> EView=new ArrayList<>();
        for (int j = 0; j < classCL.size(); j++) {
            for (String jm:classCL.get(j).keySet()) {
                String CLnodeCode=classCL.get(j).get("NODECODE");
                List<Map<String,String>> treesList=classLevelService.getTableByNodeCode(CLnodeCode);
                if(treesList!=null&&treesList.size()>0){
                    for (int i = 0; i < treesList.size(); i++) {
                        if("C".equals(treesList.get(i).get("TYPE"))){
                            if(!treesList.get(i).get("NODECODE").equals(treesList.get(i+1).get("PARENTCODE"))){
                                List<Map<String,String>> nn=classLevelService.getTableByNodeCode(treesList.get(i).get("NODECODE"));
                                for (int k = 0; k <nn.size() ; k++) {
                                    EView.add(nn.get(k));
                                }
                            }
                        }else{
                            EView.add(treesList.get(i));
                        }
                    }
                }
                break;
            }
        }
        return EView;
    }

    /**
     * 添加模版录入界面样式信息
     * @return
     */
    @Override
    public boolean addTemplatInput(Map<String,String> inputView){
        boolean bool=true;
        if(!newInputViewMapper.addInputView(inputView)){
            bool=false;
        }
        return bool;
    }
}
