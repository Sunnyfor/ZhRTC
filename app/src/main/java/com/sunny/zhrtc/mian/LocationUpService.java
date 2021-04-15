package com.sunny.zhrtc.mian;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.ids.idtma.biz.core.proxy.IDSApiProxyMgr;
import com.ids.idtma.util.IdsLog;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("MissingPermission")
public class LocationUpService extends Service {
    private static int GPS_REPORT = 10;
    private Timer timer = new Timer(true);
    private Location gpsLocation = null;
    private static int starNum = 0;
    private int longTime = 30000;

    public LocationUpService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressWarnings("deprecation")
        public void handleMessage(android.os.Message msg) {
            if (GPS_REPORT == msg.what) {
                double lat = 0;
                double lng = 0;
                double speed = 0;
                double direction = 0;
                int year = 0;
                int day = 0;
                int hour = 0;
                int month = 0;
                int minute = 0;
                int second = 0;
                String tag = "";
                if (starNum >= 6 && gpsLocation != null) {
                    lat = gpsLocation.getLatitude();
                    lng = gpsLocation.getLongitude();
                    speed = gpsLocation.getSpeed();
                    direction = gpsLocation.getBearing();
                    Date date = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    year = calendar.get(Calendar.YEAR);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    hour = date.getHours();
                    month = date.getMonth();
                    minute = date.getMinutes();
                    second = date.getSeconds();
                    tag = "上报---GPS  ";
                    IdsLog.d(TAG, "get GPS location");
                }
                if (lat != 0 && lng != 0) {
                    IdsLog.d(TAG, tag + " lat : " + lat + ",lng : " + lng + ",speed : " + speed + ",direction : " + direction + ",year : " + year + ",day : " + day);
                    IDSApiProxyMgr.getCurProxy().GpsReport((float) lng, (float) lat, (float) speed, (float) direction, year, day, hour, month, minute, second);
                }
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();

//        String timeStr = SharedPreferencesUtil.getStringPreference(this, C.sp.GPS_UP_TIME, "30");
        registerGPS();
        initTimer();

    }


    private void initNotice() {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        builder.build().flags = Notification.FLAG_ONGOING_EVENT;
        builder.build().flags |= Notification.FLAG_NO_CLEAR; //
        builder.build().flags |= Notification.FLAG_FOREGROUND_SERVICE; //
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            startForeground(2001, builder.build());
        } else {
            startForeground(2001, builder.build());
        }
    }

    private void initTimer() {
        TimerTask task = new TimerTask() {
            public void run() {
                handler.sendEmptyMessage(GPS_REPORT);
            }
        };
        timer.schedule(task, 0, longTime);
    }

    /**
     * 查询当前工作模块的未读数量
     */


    public static void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (CanceledException e) {
            e.printStackTrace();
        }
    }

    public LocationManager lm;
    private static final String TAG = "LocationUpService";

    private void registerGPS() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lm == null) return;
        List<String> allProviders = lm.getAllProviders();
        boolean gpsFlag = true;
        for (String provider : allProviders) {
            if (LocationManager.GPS_PROVIDER.equals(provider)) {
                gpsFlag = false;
            }
        }
        if (gpsFlag) return;

        String bestProvider = lm.getBestProvider(getCriteria(), true);
        if (TextUtils.isEmpty(bestProvider)) {
            IdsLog.d("LocationUpService", "获取gps失败,可能开关未开");
            return;
        }
        Location location = lm.getLastKnownLocation(bestProvider);
        updateView(location);
        lm.addGpsStatusListener(listener);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, longTime, 1, locationListener); // 10s一次

    }

    // 位置监听
    private LocationListener locationListener = new LocationListener() {

        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            updateView(location);
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    IdsLog.i("LocationUpService", "当前GPS状态为可见状态");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    IdsLog.i("LocationUpService", "当前GPS状态为服务区外状态");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    IdsLog.i("LocationUpService", "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            Location location = lm.getLastKnownLocation(provider);
            updateView(location);
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
            updateView(null);
        }
    };

    // 状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                // 第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.e(TAG, "第一次定位");
                    break;
                // 卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    // Log.i(TAG, "卫星状态改变");
                    // 获取当前状态
                    GpsStatus gpsStatus = lm.getGpsStatus(null);
                    // 获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    // 创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
                    starNum = count;
//                    Log.e(TAG, "搜索到：" + count + "颗卫星");
                    break;
                // 定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.e(TAG, "定位启动");
                    break;
                // 定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.e(TAG, "定位结束");
                    starNum = 0;
                    break;
            }
        }

    };

    private void updateView(final Location location) {
        if (location != null) {
//            Location loc = GpsAlgorithm(location);
//            if (loc != null) {
//                gpsLocation = loc;
//            }
            gpsLocation = location;
            float accuracy = location.getAccuracy();
            IdsLog.d("MyLoctionUpService", "源头的gps经纬度为:" + location.getLatitude() + "," + location.getLongitude() + "准确度" + accuracy + "速度:" + location.getSpeed());
        }

    }

    /**
     * 返回查询条件
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lm != null && locationListener != null) {
            lm.removeUpdates(locationListener);
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        IdsLog.d("GPS", "关闭gps定时上传");
    }
}