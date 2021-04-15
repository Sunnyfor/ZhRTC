package com.sunny.zhrtc.bean;

public class Person {

    private String number;//号码
    private String name;//名字
    private String picUrl;//头像地址
    public boolean isOnline = false;
    private String context;//
    private String workCard;//工作证
    private String department;//部门
    private String Duties;//职务
    private String phoneNum;//手机号
    private String carCard;//车牌号
    private int type;
    private int attr;

    public int getAttr() {
        return attr;
    }

    public void setAttr(int attr) {
        this.attr = attr;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getWorkCard() {
        return workCard;
    }

    public void setWorkCard(String workCard) {
        this.workCard = workCard;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDuties() {
        return Duties;
    }

    public void setDuties(String duties) {
        Duties = duties;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getCarCard() {
        return carCard;
    }

    public void setCarCard(String carCard) {
        this.carCard = carCard;
    }

    public Person() {
        super();
    }

    public String getNumber() {
        return number;
    }

    public Person(String number, String name, String picUrl) {
        super();
        this.number = number;
        this.name = name;
        this.picUrl = picUrl;
    }

    public Person(String number, String name, String picUrl, boolean isOnline) {
        super();
        this.number = number;
        this.name = name;
        this.picUrl = picUrl;
        this.isOnline = isOnline;
    }


    public Person(String name, String workCard, String department,
                  String duties, String phoneNum, String carCard) {
        super();
        this.name = name;
        this.workCard = workCard;
        this.department = department;
        Duties = duties;
        this.phoneNum = phoneNum;
        this.carCard = carCard;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

}
