package com.sunny.zhrtc.bean;

import java.util.ArrayList;
import java.util.List;

public class VideoPara {
    // 可用分辨率列表
    private List<String> resolutionList = new ArrayList<String>();

    // 最小码率范围
    private int minBitrate;
    // 最大码率范围
    private int maxBitrate;
    // 最小帧率范围
    private int minFramerate;
    // 最大帧率范围
    private int maxFramerate;

    //可设置的帧率
//	private List<String> framerateList = new ArrayList<String>();


    public VideoPara() {
        super();
    }

    public VideoPara(List<String> resolutionList, int minBitrate,
                     int maxBitrate, int minFramerate, int maxFramerate) {
        super();
        this.resolutionList = resolutionList;
        this.minBitrate = minBitrate;
        this.maxBitrate = maxBitrate;
        this.minFramerate = minFramerate;
        this.maxFramerate = maxFramerate;
    }

    public int getMinFramerate() {
        return minFramerate;
    }

    public void setMinFramerate(int minFramerate) {
        this.minFramerate = minFramerate;
    }

    public int getMaxFramerate() {
        return maxFramerate;
    }

    public void setMaxFramerate(int maxFramerate) {
        this.maxFramerate = maxFramerate;
    }

    public List<String> getResolutionList() {
        return resolutionList;
    }

    public void setResolutionList(List<String> resolutionList) {
        this.resolutionList = resolutionList;
    }

    public int getMinBitrate() {
        return minBitrate;
    }

    public void setMinBitrate(int minBitrate) {
        this.minBitrate = minBitrate;
    }

    public int getMaxBitrate() {
        return maxBitrate;
    }

    public void setMaxBitrate(int maxBitrate) {
        this.maxBitrate = maxBitrate;
    }
//	public List<String> getFramerateList() {
//		return framerateList;
//	}
//	public void setFramerateList(List<String> framerateList) {
//		this.framerateList = framerateList;
//	}


}
