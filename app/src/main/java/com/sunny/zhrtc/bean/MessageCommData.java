package com.sunny.zhrtc.bean;

public class MessageCommData {
    public int type;          // 0 会议相关信息  1 表示终端Gps设置相关  2 终端分辨率设置,3 用户头像等属性改变 4 视频转发
    // 5表示撤回语音消息  6表示sos消息 7.表示视频时截图8,表示台下静音 9,表示sos的开关控制 10,表示终端警情上报了
    //11,表示终端收到调度台的出警 //12 表示调度台发送的通知  13 调度台发起语音会议 14.预案相关信息
    public String messageId;  // 短信对应ID, 需要唯一, 年月日时分秒毫秒

    public String fromNumber;   // 发送号码
    public String fromDesc;     // 发送号码描述

    public String toNumber;   // 接受号码
    public String toDesc;     // 接受号码描述

    public String subPara;    // 根据类型来进行结构处理,
    // 比如 0 , 那么该值为会议相关的信息, 结构为 MessageData 的json值
    // 比如 1 , 那么该值为Gps设置的信息, 结构为 GpsData 的json值
    public String keep1;     // 保留字段
    public String keep2;     // 保留字段
    public String keep3;     // 保留字段

    public MessageCommData() {
        super();
    }

    public MessageCommData(int type, String messageId,
                           String fromNumber, String fromDesc,
                           String toNumber, String toDesc,
                           String subPara) {
        super();
        this.type = type;
        this.messageId = messageId;
        this.fromNumber = fromNumber;
        this.fromDesc = fromDesc;
        this.toNumber = toNumber;
        this.toDesc = toDesc;
        this.subPara = subPara;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFromNumber() {
        return fromNumber;
    }

    public void setFromNumber(String fromNumber) {
        this.fromNumber = fromNumber;
    }

    public String getFromDesc() {
        return fromDesc;
    }

    public void setFromDesc(String fromDesc) {
        this.fromDesc = fromDesc;
    }

    public String getToNumber() {
        return toNumber;
    }

    public void setToNumber(String toNumber) {
        this.toNumber = toNumber;
    }

    public String getToDesc() {
        return toDesc;
    }

    public void setToDesc(String toDesc) {
        this.toDesc = toDesc;
    }

    public String getSubPara() {
        return subPara;
    }

    public void setSubPara(String subPara) {
        this.subPara = subPara;
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
}



