package com.sunny.zhrtc.mian;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import com.sunny.zy.utils.LogUtil;


/**
 * 专用于系统广播接收:如耳机蓝牙网络开机电量等
 */
public class MyReceiver extends BroadcastReceiver {

    State wifiState = null;
    State mobileState = null;
    private int netStatus = -1;//0 无网络  1 wifi  2 流量

    private int subtype;//13 联通4g  10联通3g

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            // 获取手机的连接服务管理器，这里是连接管理器类
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (cm != null) {
                wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            }

            if (networkInfo != null) {
                mobileState = networkInfo.getState();
                subtype = networkInfo.getSubtype();
            }
            if (mobileState != null && State.CONNECTED != wifiState
                    && (State.CONNECTED == mobileState)) {
                LogUtil.INSTANCE.e("当前网络为流量,类型为:" + subtype);
                if (netStatus == 0 || netStatus == 1) {
                    netStatus = 2;
                } else if (wifiState != null && State.CONNECTED == wifiState && State.CONNECTED != mobileState) {
                    LogUtil.INSTANCE.e("当前网络为wifi");
                    if (netStatus == 0 || netStatus == 2) {
                        netStatus = 1;
                    } else if (wifiState != null && mobileState != null && State.CONNECTED != wifiState && State.CONNECTED != mobileState) {
                        LogUtil.INSTANCE.e("当前无网络连接no Net");
                        netStatus = 0;
                    }
                }
            }
        }
    }
}