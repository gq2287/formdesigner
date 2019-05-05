package com.wskj.project.model;

import io.swagger.annotations.ApiModel;

@ApiModel
public class Tree {
    private String id;//数据库表名
    private String text;//树名
    private Object children;//子节点
    private String icon;//如果是文件时就给text
    private Object li_attr;//当前树其他属性

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Object getChildren() {
        return children;
    }

    public void setChildren(Object children) {
        this.children = children;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Object getLi_attr() {
        return li_attr;
    }

    public void setLi_attr(Object li_attr) {
        this.li_attr = li_attr;
    }

    @Override
    public String toString() {
        return "Tree{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", children=" + children +
                ", icon='" + icon + '\'' +
                ", li_attr=" + li_attr +
                '}';
    }
}
