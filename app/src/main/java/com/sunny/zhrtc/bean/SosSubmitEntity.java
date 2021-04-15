package com.sunny.zhrtc.bean;

/**
 * Created by Administrator on 2019/3/6.
 */

public class SosSubmitEntity {
    private String id;
    private String alarm_user;
    private String alarm_time;
    private String alarm_addr;


    private String solve_user;
    private String solve_time;
    private String solve_content;

    public String getSolve_user() {
        return solve_user;
    }

    public void setSolve_user(String solve_user) {
        this.solve_user = solve_user;
    }

    public String getSolve_time() {
        return solve_time;
    }

    public void setSolve_time(String solve_time) {
        this.solve_time = solve_time;
    }

    public String getSolve_content() {
        return solve_content;
    }

    public void setSolve_content(String solve_content) {
        this.solve_content = solve_content;
    }

    public SosSubmitEntity() {

    }

    public SosSubmitEntity(String id, String alarm_user, String alarm_time, String alarm_addr) {
        this.id = id;
        this.alarm_user = alarm_user;
        this.alarm_time = alarm_time;
        this.alarm_addr = alarm_addr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlarm_user() {
        return alarm_user;
    }

    public void setAlarm_user(String alarm_user) {
        this.alarm_user = alarm_user;
    }

    public String getAlarm_time() {
        return alarm_time;
    }

    public void setAlarm_time(String alarm_time) {
        this.alarm_time = alarm_time;
    }

    public String getAlarm_addr() {
        return alarm_addr;
    }

    public void setAlarm_addr(String alarm_addr) {
        this.alarm_addr = alarm_addr;
    }

}
