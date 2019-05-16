package com.wskj.project.service.impl;

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
    @Override
    public List<Map<String, String>> getAllTemplates() {
        return tableCardTemplateMapper.getAllTableCardTemplate();
    }

    @Override
    public boolean addTableCardTemplate(Map<String, String> parms) {
        return tableCardTemplateMapper.addTableCardTemplate(parms);
    }
}
