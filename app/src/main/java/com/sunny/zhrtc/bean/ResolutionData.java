package com.sunny.zhrtc.bean;

public class ResolutionData {
    public int type;  //0 表示查询  1 表示查询结果   2 表示设置  3 表示设置结果

    // 当前参数
    private String resolution; //分辨率 (宽度*高度)
    private int bitrate;   //码率
    private int framerate; //帧率
    private VideoPara videoPara;

    public ResolutionData() {
        super();
    }

    public ResolutionData(int type, String resolution, int bitrate,
                          int framerate, VideoPara videoPara) {
        super();
        this.type = type;
        this.resolution = resolution;
        this.bitrate = bitrate;
        this.framerate = framerate;
        this.videoPara = videoPara;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getFramerate() {
        return framerate;
    }

    public void setFramerate(int framerate) {
        this.framerate = framerate;
    }

    public VideoPara getVideoPara() {
        return videoPara;
    }

    public void setVideoPara(VideoPara videoPara) {
        this.videoPara = videoPara;
    }

    public String keep1;     // 保留字段
    public String keep2;     // 保留字段
    public String keep3;     // 保留字段
}
