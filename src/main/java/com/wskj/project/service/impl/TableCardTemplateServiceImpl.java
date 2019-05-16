package com.wskj.project.service.impl;

import com.wskj.project.dao.NewInputViewMapper;
import com.wskj.project.dao.TableCardTemplateMapper;
import com.wskj.project.service.TableCardTemplateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class TableCardTemplateServiceImpl implements TableCardTemplateService {
    @Resource
    private TableCardTemplateMapper tableCardTemplateMapper;
    @Resource
    private NewInputViewMapper newInputViewMapper;

    @Override
    public List<Map<String, String>> getAllTemplates() {
        return tableCardTemplateMapper.getAllTableCardTemplate();
    }

    @Override
    public boolean addTableCardTemplate(Map<String, String> parms) {

        return tableCardTemplateMapper.addTableCardTemplate(parms);
    }

    @Override
    public boolean delTableCardTemplateById(String id) {
        boolean bool = true;
        int result = tableCardTemplateMapper.delTableCardTemplateById(id);
        if (result < 0) {
          return  bool = false;
        }
        result=newInputViewMapper.delAllInputViewByTableCode(id);
        if (result < 0) {
            return  bool = false;
        }
        return bool;
    }
}
