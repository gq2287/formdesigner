package com.wskj.project.model;

/**
 * 模版表结构对象
 */
public class Template {
    private String classCode;//模板门类的编号
    private String classTableCode;//模版门类编号
    private String parentCode;//父级编号
    private String nodeCode;//自身编号
    private String name;//模版表名称
    private String tableCode;//模版表名称
    private String remark;//描述

    public Template() {
    }

    public Template(String classCode) {
        this.classCode = classCode;
    }
    public Template(String classCode, String classTableCode, String parentCode, String nodeCode, String name) {
        this.classCode = classCode;
        this.classTableCode = classTableCode;
        this.parentCode = parentCode;
        this.nodeCode = nodeCode;
        this.name = name;
    }

    public Template(String classCode, String classTableCode, String parentCode, String nodeCode, String name, String tableCode, String remark) {
        this.classCode = classCode;
        this.classTableCode = classTableCode;
        this.parentCode = parentCode;
        this.nodeCode = nodeCode;
        this.name = name;
        this.tableCode = tableCode;
        this.remark = remark;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassTableCode() {
        return classTableCode;
    }

    public void setClassTableCode(String classTableCode) {
        this.classTableCode = classTableCode;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

}
