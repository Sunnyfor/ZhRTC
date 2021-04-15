package com.sunny.zhrtc.mian;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import com.sunny.zy.utils.LogUtil;

/**
 * Created by Administrator on 2019/7/15.
 */

public class AlarmService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.INSTANCE.e("我是定时任务");
//        IdsLibUtils.acquireWakeLockTime(this,2000);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int anHour = 10 * 1000;
        long triggerAtMillis = SystemClock.elapsedRealtime() + anHour;
        Intent alarmIntent = new Intent(this, AlarmService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        alarmManager.cancel(pendingIntent);
//        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis,anHour, pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// 6.0
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
            alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//  4.4
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
