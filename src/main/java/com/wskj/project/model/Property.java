package com.wskj.project.model;

/**
 * 属性信息
 */
public class Property {
    private String propertyString = "";
    private String name = "";
    private String dataType = "";
    private String value = "";

    public String getPropertyString() {
        return propertyString;
    }

    public void setPropertyString(String propertyString) {
        this.propertyString = propertyString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    /**
     * 获取属性信息 分解 |
     */
//    public void getProperty() {
//        int start = 0;
//        int end = this.propertyString.indexOf("|", start);
//        this.name = this.propertyString.substring(start + 1, end);
//        start = end + 1;
//        end = this.propertyString.indexOf("|", start);
//        this.dataType = this.propertyString.substring(start, end);
//        start = end + 1;
//        end = this.propertyString.length();
//        this.value = this.propertyString.substring(start, end);
//    }
}
