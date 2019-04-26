package com.wskj.project.service.impl;

import com.wskj.project.dao.TableInputViewMapper;
import com.wskj.project.service.TableInputViewService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
@Service
public class TableInputViewServiceImpl implements TableInputViewService {
    @Resource
    private TableInputViewMapper tableInputViewMapper;
    @Override
    public List<Map<String, Object>> getTableInputView(String tableCode) {
        System.out.println(tableCode);
        List<Map<String, Object>> inputList=null;
        try {
            inputList=tableInputViewMapper.getTableInputView(tableCode);
            if(inputList!=null&&inputList.size()>0){
                System.out.println("获取录入视图成功");
            }
        }catch (Exception e){
            System.err.println("获取录入视图失败！"+e.getMessage());
        }
        return inputList;
    }
}
