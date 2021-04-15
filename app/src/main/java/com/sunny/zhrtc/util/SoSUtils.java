package com.sunny.zhrtc.util;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.ids.idtma.IdtLib;
import com.ids.idtma.biz.core.proxy.IDSApiProxyMgr;
import com.ids.idtma.jni.aidl.UserGroupData;
import com.ids.idtma.person.PersonCtrl;
import com.ids.idtma.util.Compat;
import com.ids.idtma.util.DateUtils;
import com.ids.idtma.util.IdsLog;
import com.sunny.zhrtc.MyApplication;
import com.sunny.zhrtc.bean.MessageCommData;
import com.sunny.zhrtc.bean.SoSData;
import com.sunny.zhrtc.bean.SosSubmitEntity;
import com.sunny.zhrtc.mian.SosActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.UUID;


/**
 * Created by Administrator on 2019/5/14.
 */

public class SoSUtils {

    public static void SOS(Context context) {
        String sosNum = Compat.readIni("SYSTEM", "SOSNUM");//表示发sos消息给调度台
        double latitude = 22.973103;
        double longitude = 112.853401;
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        SoSData soSData1 = new SoSData();
        soSData1.setType(4);
        soSData1.setOpen(true);
        soSData1.setSosNumber(sosNum);
        soSData1.setLatitude(latitude);
        soSData1.setLongitude(longitude);
        String time = DateUtils.formatDate(null, null);
        soSData1.setTime(time);
        soSData1.setUuid(uuid);

        Gson gson = new Gson();

        MessageCommData sosMessage = new MessageCommData(6, DateUtils.formatDate(null, null), MyApplication.Companion.getUserAccount(),
                "SOS报警", sosNum, sosNum, gson.toJson(soSData1));

        for (UserGroupData mGroupDatum : PersonCtrl.mGroupData) {
            IDSApiProxyMgr.getCurProxy().iMSend(0, 17, mGroupDatum.getUcNum(), gson.toJson(sosMessage),
                    null, "", null, 0);
        }

        submitSos(context, uuid, time, latitude, longitude);
        RingtoneUtils.playSos(context);
        //这里设置为自动接听语音视频

//        SharedPreferencesUtil.setStringPreferences(context, C.sp.GPS_UP_TIME, 2000 + "");
//        context.stopService(new Intent(context, MyLocationUpService.class));
//        context.startService(new Intent(context, MyLocationUpService.class));
//        SosStatus = true;
        Intent intent2 = new Intent(context, SosActivity.class);//跳转到sos界面,
        intent2.putExtra("uuid", uuid);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent2);
    }

    private static void submitSos(final Context context, final String uuid, final String time, final double latitude, final double longitude) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String urlAddress = "https://" + MyApplication.Companion.getServiceIp() + ":8443/comm/services/webServiceAppImpl?wsdl";
                try {
                    SosSubmitEntity entity = new SosSubmitEntity(uuid, MyApplication.Companion.getUserAccount(), time, latitude + "," + longitude);
                    SoapObject request1 = new SoapObject("http://webservice.comm.chatsure.cn/", "submitSOS");
                    request1.addProperty("arg0", MyApplication.Companion.getUserAccount());
                    request1.addProperty("arg1", Compat.readIni("SYSTEM", "TOKEN"));
                    request1.addProperty("arg2", new Gson().toJson(entity));
                    SoapSerializationEnvelope envelope1 = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope1.bodyOut = request1;
                    HttpTransportSE ht = new HttpTransportSE(urlAddress);
                    ht.debug = true;

                    ht.call(null, envelope1);
                    Object result = envelope1.bodyIn;
                    String notice = ((SoapObject) result).getProperty(0).toString();
                    IdsLog.d("xiezhixian", "submitSos==" + notice);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        IdtLib.myThreadPool.execute(runnable);
    }
}
