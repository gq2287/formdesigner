package com.wskj.project.dao;

import java.util.List;
import java.util.Map;

public interface TableCardTemplateMapper {
    List<Map<String,String>> getAllTableCardTemplate();

    boolean addTableCardTemplate(Map<String,String> parms);
}
