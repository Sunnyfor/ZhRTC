package com.sunny.zhrtc.bean;

public class GpsSetUpData {
    public int type;  //0 表示查询  1 表示查询结果   2 表示设置  3 表示设置结果
    public boolean open;  // 是否打开gps
    public int frequencyTime = 30;// gps上传间隔

    public GpsSetUpData() {
        super();
    }

    public GpsSetUpData(int type, boolean open) {
        super();
        this.type = type;
        this.open = open;
    }

    public GpsSetUpData(int type, boolean open, int frequencyTime) {
        super();
        this.type = type;
        this.open = open;
        this.frequencyTime = frequencyTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public int getFrequencyTime() {
        return frequencyTime;
    }

    public void setFrequencyTime(int frequencyTime) {
        this.frequencyTime = frequencyTime;
    }

    public String keep1;     // 保留字段
    public String keep2;     // 保留字段
    public String keep3;     // 保留字段
}
