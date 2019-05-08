package com.wskj.project.model;

public class Template {
    private String classCode;
    private String classTableCode;
    private String parentCode;
    private String nodeCode;
    private String name;
    private String remark;

    public Template() {
    }

    public Template(String classCode) {
        this.classCode = classCode;
    }
    public Template(String classCode, String classTableCode, String tableCode, String nodeCode, String name) {
        this.classCode = classCode;
        this.classTableCode = classTableCode;
        this.parentCode = parentCode;
        this.nodeCode = nodeCode;
        this.name = name;
    }

    public Template(String classCode, String classTableCode,  String parentCode, String nodeCode, String name, String remark) {
        this.classCode = classCode;
        this.classTableCode = classTableCode;
        this.parentCode = parentCode;
        this.nodeCode = nodeCode;
        this.name = name;
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
}
