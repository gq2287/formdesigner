package com.wskj.project.model;

public class ClassNode {
    private String nodeCode;
    private String parentCode;
    private String name;
    private String type;
    private String classCode;
    private String classTableCode;
    private String tableCode;
    private String serial;

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public ClassNode(String nodeCode, String parentCode, String name, String type, String classCode, String classTableCode, String tableCode, String serial) {
        this.nodeCode = nodeCode;
        this.parentCode = parentCode;
        this.name = name;
        this.type = type;
        this.classCode = classCode;
        this.classTableCode = classTableCode;
        this.tableCode = tableCode;
        this.serial = serial;
    }

    public ClassNode() {
    }
}
