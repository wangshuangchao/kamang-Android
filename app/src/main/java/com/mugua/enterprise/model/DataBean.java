package com.mugua.enterprise.model;

public class DataBean {
    public DataBean(int shu,int icon, String text, String content) {
        this.shu = shu;
        this.icon = icon;
        this.text = text;
        this.content = content;
    }

    private int shu;
    private int icon;
    private String text;
    private String content;

    public int getShu() {
        return shu;
    }

    public void setShu(int shu) {
        this.shu = shu;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
