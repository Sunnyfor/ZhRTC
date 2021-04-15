package com.sunny.zhrtc.bean;

import java.util.ArrayList;
import java.util.List;

public class MediaChangeData {
    public int type;  ////0 表示设置转发用户 1 表示设置结果返回 2 表示需要下载toNum用户的视频
    public List<String> userList = new ArrayList<String>();  // 视频转发的用户列表
    private String toNum;//表示此人将视频上传到服务器
    private String toNumDesc;//表示此人描述

    public String keep1;     // 保留字段
    public String keep2;     // 保留字段
    public String keep3;     // 保留字段


    public MediaChangeData(int type, List<String> userList) {
        super();
        this.type = type;
        this.userList = userList;
    }

    public MediaChangeData(int type, String toNum) {
        super();
        this.type = type;
        this.toNum = toNum;
    }

    public MediaChangeData() {
        super();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }

    /**
     * @return the toNum
     */
    public String getToNum() {
        return toNum;
    }

    /**
     * @param toNum the toNum to set
     */
    public void setToNum(String toNum) {
        this.toNum = toNum;
    }

    /**
     * @return the toNumDest
     */
    public String getToNumDest() {
        return toNumDesc;
    }

    /**
     * @param toNumDest the toNumDest to set
     */
    public void setToNumDest(String toNumDest) {
        this.toNumDesc = toNumDest;
    }
}
