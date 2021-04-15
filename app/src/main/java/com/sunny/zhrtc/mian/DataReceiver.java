package com.sunny.zhrtc.mian;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ids.idtma.biz.core.IdsCallBack;
import com.ids.idtma.jni.IDTNativeApi;


public class DataReceiver extends BroadcastReceiver {

    IdsCallBack idsCallBack;

    public DataReceiver(IdsCallBack idsCallBack) {
        this.idsCallBack = idsCallBack;
    }

    @Override
    public void onReceive(Context Context, Intent intent) {
        int type = intent.getIntExtra(IDTNativeApi.TYPE, 0);
        String data = intent.getStringExtra(IDTNativeApi.DATA);
        String action = intent.getAction();
        if (IDTNativeApi.IDT_ACTION.equals(action)) {
            idsCallBack.onGetData(data, type);
        }
    }

}
