package com.sunny.zhrtc.bean;

/**
 * Created by Administrator on 2018/5/22.
 */

public class SosSwitchEntity {

    public int type;  //0 表示查询  1 表示查询结果   2 表示设置  3 表示设置结果
    public boolean open;  // 是否打开sos
    public String keep1;     // 保留字段
    public String keep2;     // 保留字段

    public SosSwitchEntity(int type, boolean open) {
        this.type = type;
        this.open = open;
    }

    public SosSwitchEntity() {

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


}
