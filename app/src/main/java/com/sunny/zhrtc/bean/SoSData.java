package com.sunny.zhrtc.bean;

/**
 * Created by Administrator on 2017/8/29.
 */

public class SoSData {
    // 0 表示查询终端SOS设置  1 表示查询结果
    // 2 表示设置终端SOS   3 表示设置结果
    //0 1 2 3 表是设置调度台号码

    // 4 终端sos启动       5 终端sos启动收到确认       -- 如果终端没有收到确认, 需要重复发送
    // 6调度台sos终止    7调度台sos终止收到确认   -- 如果调度台没有收到确认, 需要重复发送
    //8 终端sos主动终止
    public int type;
    public boolean open;         // 是否打开sos
    public String sosNumber;     // sos 号码
    public double latitude;      // 告警位置 经度
    public double longitude;
    public String time;


    public String uuid;//sos表中的id

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String keep1;     // 保留字段
    public String keep2;     // 保留字段
    public String keep3;     // 保留字段

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

    public String getSosNumber() {
        return sosNumber;
    }

    public void setSosNumber(String sosNumber) {
        this.sosNumber = sosNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKeep1() {
        return keep1;
    }

    public void setKeep1(String keep1) {
        this.keep1 = keep1;
    }

    public String getKeep2() {
        return keep2;
    }

    public void setKeep2(String keep2) {
        this.keep2 = keep2;
    }

    public String getKeep3() {
        return keep3;
    }

    public void setKeep3(String keep3) {
        this.keep3 = keep3;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
