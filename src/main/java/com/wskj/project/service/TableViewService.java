package com.wskj.project.service;

import com.wskj.project.model.Tree;

import java.util.List;
import java.util.Map;

public interface TableViewService {

    List<Map<String,Object>> getTableView(String tableCode);

    Tree getTreeMenu();

    Boolean upTableViewSelect(Map<String,String> parms);
}
