package com.sunny.zhrtc.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ids.idtma.biz.core.IdsCallBack;
import com.ids.idtma.jni.IDTNativeApi;
import com.ids.idtma.util.IdsLog;
import com.sunny.zhrtc.mian.DataReceiver;
import com.sunny.zy.utils.LogUtil;


/**
 * Created by Administrator on 2018/5/18.
 */
@SuppressLint("InvalidWakeLockTag")
public class BaseActivity extends AppCompatActivity implements IdsCallBack {


    private DataReceiver dataReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acquireWakeLock(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(IDTNativeApi.IDT_ACTION);
        dataReceiver = new DataReceiver(this);
        registerReceiver(dataReceiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dataReceiver);
        releaseWakeLock();
    }

    @Override
    public void onGetData(String data, int type) {

    }

    public void toast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    private static PowerManager.WakeLock wakeLock = null;

    // 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行

    public static void acquireWakeLock(Context context) {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm != null) {
                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "IDTNativeApi");
                if (wakeLock != null && !wakeLock.isHeld()) {
                    wakeLock.acquire();
                    LogUtil.INSTANCE.e("申请电源锁");
                }
            }

        }
    }

    public static void acquireWakeLockTime(Context context, long time) {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm != null) {
                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "IDTNativeApi");
                if (wakeLock != null && !wakeLock.isHeld()) {
                    wakeLock.acquire(time);
                    LogUtil.INSTANCE.e("申请电源锁: " + time);
                }
            }

        }
    }

    public static void releaseWakeLock() {
        if (null != wakeLock) {
            wakeLock.release();
            wakeLock = null;
            IdsLog.d("WakeLock", "释放电源锁");
        }
    }
}
