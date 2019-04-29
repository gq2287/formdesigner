package com.wskj.project.model;

/**
 * 样式详细
 */
public class UIBean {
    private String caption = "";//说明文字
    private String tag = "";//标签
    private String foreColor = "";//设置文本时的前景色
    private String backColor = "";//背景色
    private String height = "";//高度
    private String width = "";//宽度
    private String align = "";//文本对齐方式
    private String borderStyle = "";//边框样式
    private String appearance = "";//外观
    private String left = "";//左
    private String top = "";//顶部
    private String font = "";//字体
    private String fontName = "";//字体名称
    private String fontSize = "";//字体大小
    private String fontBold = "";//字体粗细
    private String fontItalic = "";//是否倾斜
    private String text = "";//文本
    private String value = "";//值
    private String visible = "";//是否可见
    private String rowHeight = "";//行高
    private String properties = "";//属性详情
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getForeColor() {
        return foreColor;
    }

    public void setForeColor(String foreColor) {
        this.foreColor = foreColor;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getBorderStyle() {
        return borderStyle;
    }

    public void setBorderStyle(String borderStyle) {
        this.borderStyle = borderStyle;
    }

    public String getAppearance() {
        return appearance;
    }

    public void setAppearance(String appearance) {
        this.appearance = appearance;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontBold() {
        return fontBold;
    }

    public void setFontBold(String fontBold) {
        this.fontBold = fontBold;
    }

    public String getFontItalic() {
        return fontItalic;
    }

    public void setFontItalic(String fontItalic) {
        this.fontItalic = fontItalic;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(String rowHeight) {
        this.rowHeight = rowHeight;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }


    //获取边框样式
    public String getborder(String s, String s1) {
        if ("0".equals(s)) {
            return "none";
        } else {
            return "0".equals(s1) ? "solid" : "inset";
        }
    }

    //获取16进制
    public String getIntHex(String s) {
        if ("".equals(s)) {
            return "";
        } else if (s == null) {
            return "";
        } else if (Float.valueOf(s) > 1.677722E7F) {
            return "";
        } else {
            s=s.substring(1,s.length()-1);
            s = Integer.toString(Integer.parseInt(s), 16);

            for(int i = s.length(); i < 6; ++i) {
                s = "0" + s;
            }

            return s;
        }
    }

    //获取浮点
    public float getFloat(String s) {
        if ("".equals(s)) {
            return 0.0F;
        } else {
            return s == null ? 0.0F : Float.valueOf(s);
        }
    }


}
