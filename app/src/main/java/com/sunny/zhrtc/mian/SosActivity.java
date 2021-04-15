package com.sunny.zhrtc.mian;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ids.idtma.IdtLib;
import com.ids.idtma.biz.core.proxy.IDSApiProxyMgr;
import com.ids.idtma.jni.aidl.CallInEntity;
import com.ids.idtma.jni.aidl.CallTalkingEntity;
import com.ids.idtma.jni.aidl.IDTMsgType;
import com.ids.idtma.jni.aidl.UserGroupData;
import com.ids.idtma.person.PersonCtrl;
import com.ids.idtma.util.Compat;
import com.ids.idtma.util.DateUtils;
import com.ids.idtma.util.IdsLog;
import com.sunny.zhrtc.MyApplication;
import com.sunny.zhrtc.R;
import com.sunny.zhrtc.base.BaseActivity;
import com.sunny.zhrtc.bean.MessageCommData;
import com.sunny.zhrtc.bean.SoSData;
import com.sunny.zhrtc.bean.SosSubmitEntity;
import com.sunny.zhrtc.constants.CallConstants;
import com.sunny.zhrtc.util.RingtoneUtils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SosActivity extends BaseActivity {
    @BindView(R.id.talkGroupName)
    TextView talkGroupName;
    @BindView(R.id.talkMemberName)
    TextView talkMemberName;
    @BindView(R.id.channelVal)
    TextView channelVal;
    @BindView(R.id.frequencyVal)
    TextView frequencyVal;
    private MyBroadCastReceiver myBroadCastReceiver;
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; //需要自己定义标志
    private String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代码
        ButterKnife.bind(this);
        uuid = getIntent().getStringExtra("uuid");
        myBroadCastReceiver = new MyBroadCastReceiver();
    }

    @Override
    public void onGetData(String data, int type) {
        super.onGetData(data, type);
        if (type == IDTMsgType.CALL_IN) {
            CallInEntity callInEntity = new Gson().fromJson(data, CallInEntity.class);
            callInGroup(callInEntity);
        } else if (type == IDTMsgType.GROUP_CALL_SPEAK_TIP) {//讲话人提示
            speakTip(data);
        }
    }

    private void speakTip(String data) {
        //讲话人提示
        CallTalkingEntity callTalkingEntity = new Gson().fromJson(data, CallTalkingEntity.class);
        String speakName = callTalkingEntity.getSpeakName();
        String speakNum = callTalkingEntity.getSpeakNum();
        if (speakNum == null || "".equals(speakNum)) {
            CallHelper.CURRENT_GROUP_CALL_STATE = getResources().getString(R.string.now_take_user);
            talkGroupName.setText(getResources().getString(R.string.intercom_group_name) + CallHelper.CURRENT_GROUP_NAME);
            talkMemberName.setText(CallHelper.CURRENT_GROUP_CALL_STATE);
        } else {
            CallHelper.CURRENT_GROUP_CALL_STATE = getResources().getString(R.string.now_take_user22) + speakName;
            talkGroupName.setText(getResources().getString(R.string.intercom_group_name) + CallHelper.CURRENT_GROUP_NAME);
            talkMemberName.setText(CallHelper.CURRENT_GROUP_CALL_STATE);
        }
    }

    //收到组呼
    private void callInGroup(CallInEntity callInEntity) {
        int srvType = callInEntity.getSrvType();
        int iAudioRecv = callInEntity.getiAudioRecv();
        int iAudioSend = callInEntity.getiAudioSend();
        int iVideoRecv = callInEntity.getiVideoRecv();
        int iVideoSend = callInEntity.getiVideoSend();
        String pcPeerNum = callInEntity.getPcPeerNum();
        String pcPeerName = callInEntity.getPcPeerName();
//        String stringPreference = SharedPreferencesUtil.getStringPreference(this, ChatSureApplication.userAccount + "LockNum", "#");
//        if (!stringPreference.equals("#") && !stringPreference.equals(pcPeerNum)) {
//            return;
//        }
        // 组呼
        if ((srvType == CallConstants.CALL_TYPE_GROUP_CALL || srvType == CallConstants.SRV_TYPE_CONF_JOIN)
                && (iAudioRecv == 1 && iAudioSend == 0 && iVideoRecv == 0 && iVideoSend == 0)) {
            List<UserGroupData> mGroupData = PersonCtrl.mGroupData;
            CallHelper.CURRENT_GROUP_NUM = pcPeerNum;
            CallHelper.CURRENT_GROUP_NAME = pcPeerName;
            for (int i = 0; i < mGroupData.size(); i++) {
                mGroupData.get(i).setGroupCallIng(false);
            }
            for (int i = 0; i < mGroupData.size(); i++) {
                if (mGroupData.get(i).getUcNum().equals(pcPeerNum)) {
                    mGroupData.get(i).setGroupCallIng(true);
                }
            }
            talkGroupName.setText(getResources().getString(R.string.intercom_group_name) + pcPeerName);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        System.out.println("I'm coming, myBroadCastReceiver注册了!");
        registerReceiver(myBroadCastReceiver, intentFilter);
    }

    private class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //你自己先把 reasons == homekey 和 长按homekey 排除，剩下的做下面的处理
            String reason = intent.getStringExtra("reason");
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                System.out.println("Intent.ACTION_CLOSE_SYSTEM_DIALOGS : " + intent.getStringExtra("reason"));

                if (intent.getExtras() != null && intent.getExtras().getBoolean("myReason")) {
                    myBroadCastReceiver.abortBroadcast();
                } else if (reason != null) {

                    if (reason.equalsIgnoreCase("globalactions")) {

                        //屏蔽电源长按键的方法：
                        Intent myIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                        myIntent.putExtra("myReason", true);
                        context.sendOrderedBroadcast(myIntent, null);
//                        System.out.println("电源  键被长按");

                    } else if (reason.equalsIgnoreCase("homekey")) {

                        //屏蔽Home键的方法
                        //在这里做一些你自己想要的操作,比如重新打开自己的锁屏程序界面，这样子就不会消失了
                        Intent myIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                        myIntent.putExtra("myReason", true);
                        context.sendOrderedBroadcast(myIntent, null);

                    } else if (reason.equalsIgnoreCase("recentapps")) {

                        //屏蔽Home键长按的方法
                        Intent myIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                        myIntent.putExtra("myReason", true);
                        context.sendOrderedBroadcast(myIntent, null);
//                        System.out.println("Home 键被长按");
                    }
                }
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("I get out, myBroadCastReceiver注销了!");
        unregisterReceiver(myBroadCastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeSOS();
            RingtoneUtils.stopRingtone();
            AudioManager audioManager = (AudioManager) SosActivity.this.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.setMode(AudioManager.MODE_NORMAL);//MODE_NORMAL
                audioManager.setSpeakerphoneOn(true);
            }
            SoSData soSData1 = new SoSData();
            soSData1.setType(8);
            soSData1.setOpen(false);
            Gson gson = new Gson();
            MessageCommData sosMessage = new MessageCommData(6, DateUtils.formatDate(null, null),
                    MyApplication.Companion.getUserAccount(),
                    "SOS报警", "", "", gson.toJson(soSData1));
            for (UserGroupData mGroupDatum : PersonCtrl.mGroupData) {
                IDSApiProxyMgr.getCurProxy().iMSend(0, 17, mGroupDatum.getUcNum(), gson.toJson(sosMessage), null, "", null, 0);
            }
            finish();
        }
        return true;
    }


    private void closeSOS() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String urlAddress = "https://" + MyApplication.Companion.getServiceIp() + ":8443/comm/services/webServiceAppImpl?wsdl";
                String time = DateUtils.formatDate(null, null);
                try {
                    SosSubmitEntity entity = new SosSubmitEntity();
                    entity.setId(uuid);
                    entity.setSolve_content("自己关闭");
                    entity.setSolve_time(time);
                    entity.setSolve_user(MyApplication.Companion.getUserAccount());
                    SoapObject request1 = new SoapObject("http://webservice.comm.chatsure.cn/", "closeSOS");
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
                    IdsLog.d("xiezhixian", "closeSos==" + notice);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        IdtLib.myThreadPool.execute(runnable);
    }

}
