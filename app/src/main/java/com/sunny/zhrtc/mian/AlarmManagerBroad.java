package com.sunny.zhrtc.mian;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sunny.zhrtc.util.AlarmManagerUtils;
import com.sunny.zy.utils.LogUtil;

public class AlarmManagerBroad extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.ids.idtma.service.AlarmManagerBroad".equals(intent.getAction())) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                setAlarmTime(context, System.currentTimeMillis() + 15, "com.ids.idtma.service.AlarmManagerBroad");
//            }
//            IdsLibUtils.releaseWakeLock();
            AlarmManagerUtils.getInstance(context).getUpAlarmManagerWorkOnReceiver();
//            IdsLibUtils.acquireWakeLockTime(context,3000);
            String extra = intent.getStringExtra("msg");
            LogUtil.INSTANCE.e("我收到了闹钟的唤醒通知:  " + extra);
        }
    }


//    @SuppressLint("NewApi")
//    public static void setAlarmTime(Context context, long timeInMillis, String action) {
//        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        if (am == null)
//            return;
//        Intent intent = new Intent(action);
//        int anHour=10*1000;
//        long triggerAtMillis = SystemClock.elapsedRealtime()+anHour;
//        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        am.cancel(sender);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, sender);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, sender);
//        } else {
//            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeInMillis, triggerAtMillis, sender);
//        }
//
//    }

//    public static void canalAlarm(Context context, String action) {
//        Intent intent = new Intent(action);
//        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        am.cancel(pi);
//    }

}
