package com.wskj.project.service.impl;

import com.wskj.project.dao.NewInputViewMapper;
import com.wskj.project.dao.TableInputViewMapper;
import com.wskj.project.dao.TableMapper;
import com.wskj.project.service.NewInputViewService;
import com.wskj.project.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class NewInputViewImpl implements NewInputViewService {

    @Resource
    private NewInputViewMapper newInputViewMapper;
    @Resource
    private TableInputViewMapper tableInputViewMapper;
    @Resource
    private TableMapper tableMapper;

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
     * @return
     */
    @Override
    public Boolean saveInputView(String tableCode,List<Map<String,Object>> parasUIList) {
        boolean bool=true;
        try {
            if(parasUIList==null){//不是录入界面进入
                //查询实体表数据列
                parasUIList=tableMapper.getEntityTableColumnByVisible(tableCode);//获取选中默认添加表数据列
            }
            if(parasUIList!=null&&parasUIList.size()>0){
                for (int i = 0; i <parasUIList.size() ; i++) {
                    Map<String, Object> columnMap=parasUIList.get(i);
                    String columncode=String.valueOf(columnMap.get("COLUMNCODE"));//字段中文名称
                    String name=String.valueOf(columnMap.get("NAME"));//字段名称
                    String chineseName=String.valueOf(columnMap.get("CHINESENAME"));//字段中文名称
                    String inputType=String.valueOf(columnMap.get("INPUTTYPE"));//字段名称
                    Map<String,String> inputView=new HashMap<>();//录入集合
                    inputView.put("INTERFACECARDCODE", StringUtil.getDate(2)+StringUtil.getRandom(1000,10000));//列编号
                    inputView.put("TABLECODE",tableCode);
                    inputView.put("FIELDNAME",name);//字段英文名
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
                    inputView.put("ROWNO",1+"");
                    inputView.put("COLUMNNO",1+"");
                    inputView.put("LOADNO",i+"");
                    newInputViewMapper.addInputViewColumn(inputView);
                }
            }
        }catch (Exception e){
            System.out.println(tableCode+"录入失败"+e.getMessage());
            newInputViewMapper.delAllInputViewByTableCode(tableCode);//失败删除全部
            bool=false;
        }
        return bool;
    }

    @Override
    public Boolean delInputView(String tableCode) {
        boolean bool=newInputViewMapper.delAllInputViewByTableCode(tableCode);//删除全部
        return bool;
    }


}
