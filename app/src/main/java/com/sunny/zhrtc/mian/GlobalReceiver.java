package com.sunny.zhrtc.mian;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.ids.idtma.biz.core.proxy.IDSApiProxyMgr;
import com.ids.idtma.jni.IDTNativeApi;
import com.ids.idtma.jni.aidl.AudioCodecEntity;
import com.ids.idtma.jni.aidl.CallInEntity;
import com.ids.idtma.jni.aidl.CallReleaseEntity;
import com.ids.idtma.jni.aidl.CallTalkingEntity;
import com.ids.idtma.jni.aidl.GpsReceptionEntity;
import com.ids.idtma.jni.aidl.IMMsgEntity;
import com.ids.idtma.jni.aidl.LoginResult;
import com.ids.idtma.jni.aidl.MediaAttribute;
import com.ids.idtma.jni.aidl.TakeRightEntity;
import com.ids.idtma.jni.aidl.UserData;
import com.ids.idtma.jni.aidl.UserGroupData;
import com.ids.idtma.jni.aidl.UserOptResponse;
import com.ids.idtma.media.CSAudio;
import com.ids.idtma.media.MediaState;
import com.ids.idtma.util.IdsLog;
import com.ids.idtma.util.constants.Constant;
import com.ids.idtma.util.constants.IDTMsgType;
import com.ids.idtma.util.constants.IMType;
import com.sunny.zhrtc.MyApplication;
import com.sunny.zhrtc.R;
import com.sunny.zhrtc.constants.CallConstants;
import com.sunny.zhrtc.constants.UiCauseConstants;
import com.sunny.zhrtc.util.RingtoneUtils;
import com.sunny.zhrtc.util.SpeakerPhoneUtils;
import com.sunny.zhrtc.util.Utils;
import com.sunny.zy.utils.LogUtil;


/**
 * 全局接收器
 */
public class GlobalReceiver extends BroadcastReceiver {

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        String action = intent.getAction();
        if (IDTNativeApi.IDT_ACTION.equals(action)) {
            int type = intent.getIntExtra(IDTNativeApi.TYPE, -1);
            String data = intent.getStringExtra(IDTNativeApi.DATA);
            if (type == IDTMsgType.IM_RECV) {
                // TODO 接收到IM消息
                RingtoneUtils.playRing(mContext);
                IMMsgEntity imEntity = new Gson().fromJson(data, IMMsgEntity.class);
                GlobalReceiverUtils.receiverMessage(imEntity, data, context);
            } else if (type == IDTMsgType.CALL_IN) {
                // TODO 呼叫进入
                receiveCall(data);
            } else if (type == IDTMsgType.GROUP_CALL_Talk_RIGHT) {
                // TODO 话权提示 用于播放提示音
                groupCallTakeRight(data, context);
                IdsLog.d("xiezhixian", "话权提示");
            } else if (type == IDTMsgType.GROUP_CALL_SPEAK_TIP) {//讲话人提示
                // TODO 讲话人提示
                IdsLog.d("xiezhixian", "讲话人提示");
                CallTalkingEntity callTalkingEntity = new Gson().fromJson(data, CallTalkingEntity.class);
                String temp = callTalkingEntity.getSpeakNum();
                if (TextUtils.isEmpty(temp)) {
                    temp = "##$$%%??";
                    CallHelper.CURRENT_GROUP_CALL_STATE = context.getApplicationContext().getResources().getString(R.string.now_take_user);
                }
                CallHelper.CURRENT_GROUP_CALL_STATE = context.getApplicationContext().getResources().getString(R.string.now_take_user22) + callTalkingEntity.getSpeakName();
                if (!temp.equals(tallingNumber)) {
                    RingtoneUtils.playRing(context);
                }
                tallingNumber = temp;
            } else if (type == IDTMsgType.IDT_RECEIVE_GPSINFO) {
                //TODO 服务器返回的gps数据
                Gson gson = new Gson();
                GpsReceptionEntity gpsReceptionEntity = gson.fromJson(data, GpsReceptionEntity.class);
            } else if (type == IDTMsgType.CALL_RELEASE) {
                CallReleaseEntity callReleaseEntity = new Gson().fromJson(data, CallReleaseEntity.class);
                //TODO 呼叫挂断的回调(一般是对方或者服务器切断)
                if (callReleaseEntity.getUiCause() == 58) {
                    MediaState.setAudioState(false);
                    //TODO 这个表示在 正在会议的组发起组呼,会被服务器切断并返回原因值58 表示存在会议的时候组呼,则接进会议
                    CallHelper.getInstance(context).callMeetingVideo(CallHelper.CURRENT_GROUP_NUM, CallHelper.CURRENT_GROUP_NAME, IMType.IM_TYPE_MEETING, -1, CallConstants.meetingFromGroup);
                }
            } else if (type == IDTMsgType.USER_ACTION_RESPONSE) {
                UserOptResponse userOptResponse = new Gson().fromJson(data, UserOptResponse.class);
                UserData userData = userOptResponse.getUserData();
                //TODO 用户信息查询反馈
            } else if (type == IDTMsgType.LOGIN) {
                LoginResult loginResult = new Gson().fromJson(data, LoginResult.class);
                LogUtil.INSTANCE.e("当前登录回调: Result : " + loginResult.getResult() + " State:  " + loginResult.getState());
            } else if (type == IDTMsgType.AUDIO_CODEC_SETTING) {
                AudioCodecEntity audioCodec = new Gson().fromJson(data, AudioCodecEntity.class);
                //TODO 语音设置反馈
            } else if (type == IDTMsgType.LOAD_GROUP_MEMBER) {
                UserGroupData userGroupData = new Gson().fromJson(data, UserGroupData.class);
                //TODO 查找组成员信息反馈
            } else if (type == IDTMsgType.USER_ACTION_RESPONSE) {
                //TODO 查询用户信息以及用户被改后的回调
                UserOptResponse userOptResponse = new Gson().fromJson(data, UserOptResponse.class);
                UserData userData = userOptResponse.getUserData();
                String workInfo = userData.getWorkInfo();
                if (userOptResponse.getDwSn() == GroupDataActivity.QUERY_THE_MEMBER) {

                }
            }
        }

    }

    private static String tallingNumber = "##$$%%??";
    private static boolean isCallMicInd = true;
//    private static String phoneNumber;

    private void groupCallTakeRight(String data, Context context) {

        TakeRightEntity takeRightEntity = new Gson().fromJson(data, TakeRightEntity.class);
        int uiInd = takeRightEntity.getUiInd();
        long l = takeRightEntity.getpUsrCtx();
        IdsLog.d("xiezhixian", "话权指示: uiInd = " + uiInd + " 上下文 : " + l);
        if (uiInd > 0) {
            // 获取到话权
            if (!MyApplication.Companion.getUserAccount().equals(tallingNumber)) {
                RingtoneUtils.playRing(context);
            }
            tallingNumber = MyApplication.Companion.getUserAccount();
        } else {
            // 释放话权
            if (tallingNumber.equals(MyApplication.Companion.getUserAccount())) {
                RingtoneUtils.playRing(context);
                tallingNumber = "##$$%%??";
            }
            if (isCallMicInd) {
                isCallMicInd = false;
            }
        }
    }

    /**
     * 收到呼叫的回调
     *
     * @param data json数据
     */
    private void receiveCall(String data) {
        //收到呼叫 这里进行处理
        CallInEntity callInEntity = new Gson().fromJson(data, CallInEntity.class);
        int srvType = callInEntity.getSrvType();
        int iAudioRecv = callInEntity.getiAudioRecv();
        int iAudioSend = callInEntity.getiAudioSend();
        int iVideoRecv = callInEntity.getiVideoRecv();
        int iVideoSend = callInEntity.getiVideoSend();
        String pfCallIn = callInEntity.getPfCallIn();
        // 组呼
        if ((srvType == CallConstants.CALL_TYPE_GROUP_CALL || srvType == CallConstants.SRV_TYPE_CONF_JOIN)
                && (iAudioRecv == 1 && iAudioSend == 0 && iVideoRecv == 0 && iVideoSend == 0)) {
            UserGroupData userGroupData = Utils.getUserGroupData(callInEntity.getPcPeerNum());
//            if(userGroupData == null){//表示不是组号码
//                enterCall(callInEntity);
//                return;
//            }

//            String lock_group_num = SharedPreferencesUtil.getStringPreference(mContext, MyApplication.Companion.getUserAccount() + "LockNum", "#");
//            String lock_group_num = "#";//TODO 表示锁定的组
//            if (lock_group_num.equals("#") || lock_group_num.equals(callInEntity.getPcPeerNum())) {
            MediaAttribute mediaAttribute = new MediaAttribute();
            mediaAttribute.setUcAudioRecv(iAudioRecv);
            mediaAttribute.setUcAudioSend(iAudioSend);
            mediaAttribute.setUcVideoRecv(iVideoRecv);
            mediaAttribute.setUcVideoSend(iVideoSend);
            SpeakerPhoneUtils speakerPhoneUtils = new SpeakerPhoneUtils(mContext);
            speakerPhoneUtils.OpenSpeaker();

            CSAudio.getInstance().callAnswer(callInEntity.getId(), mediaAttribute, IMType.IM_TYPE_GROUP_CALL, callInEntity.getPcPeerNum());
            CallHelper.CURRENT_GROUP_NUM = callInEntity.getPcPeerNum();
            CallHelper.CURRENT_GROUP_NAME = callInEntity.getPcPeerName();
//            } else {
//                CSAudio.getInstance().audioHandUp(callInEntity.getId(), IMType.IM_TYPE_GROUP_CALL, 22);
//                Toast.makeText(mContext, mContext.getResources().getString(R.string.youRecvACall), Toast.LENGTH_SHORT).show();
//            }
        } else if ((srvType == CallConstants.CALL_TYPE_SINGLE_CALL || srvType == CallConstants.SRV_TYPE_WATCH_DOWN
                || srvType == CallConstants.SRV_TYPE_WATCH_UP || srvType == CallConstants.FORCE_INSERT)) {
            enterCall(callInEntity);
        } else if ((srvType == CallConstants.CALL_TYPE_GROUP_CALL || srvType == CallConstants.SRV_TYPE_CONF_JOIN)
                && (iAudioRecv == 1 && iAudioSend == 0 && iVideoRecv == 1 && iVideoSend == 0)) {
            CallHelper.getInstance(mContext).callMeetingVideo(callInEntity.getPcPeerNum(), callInEntity.getPcPeerName(),
                    IMType.IM_TYPE_MEETING, callInEntity.getId(), CallConstants.meetingFromIdt);
        }
    }

    private void enterCall(CallInEntity callInEntity) {
        int id = callInEntity.getId();
        int iAudioSend = callInEntity.getiAudioSend();
        int iAudioRecv = callInEntity.getiAudioRecv();
        int iVideoSend = callInEntity.getiVideoSend();
        int iVideoRecv = callInEntity.getiVideoRecv();
        String pcMyNum = callInEntity.getPcMyNum();
        String pcPeerNum = callInEntity.getPcPeerNum();
        String pcPeerName = callInEntity.getPcPeerName();
        MediaAttribute mediaAttribute = new MediaAttribute();
        mediaAttribute.setUcAudioRecv(iAudioRecv);
        mediaAttribute.setUcAudioSend(iAudioSend);
        mediaAttribute.setUcVideoRecv(iVideoRecv);
        mediaAttribute.setUcVideoSend(iVideoSend);
        CSAudio csAudio = CSAudio.getInstance();
        if (callInEntity.getSrvType() == CallConstants.CALL_TYPE_GROUP_CALL) {
            if (MediaState.getAudioState()) {
                csAudio.audioHandUp(callInEntity.getId(), IMType.IM_TYPE_GROUP_CALL, 22);
                return;
            }
            CallHelper.getInstance(mContext).receiveCall(IMType.IM_TYPE_AUDIO_CALL, id, pcPeerNum, pcPeerName, mediaAttribute);
            MediaState.setCurrentState(Constant.IN_Half_SINGLE_CALL);
            MediaState.setAudioState(true);
            return;
        }
        if ((iVideoRecv == 0 && iVideoSend == 0 && iAudioRecv == 1 && iAudioSend == 1)
                || (iVideoRecv == 0 && iVideoSend == 0 && iAudioRecv == 0 && iAudioSend == 1)
                || (iVideoRecv == 0 && iVideoSend == 0 && iAudioRecv == 1 && iAudioSend == 0)) {//语音单呼
            String topActivityName = "";
            try {
                ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                topActivityName = mActivityManager.getRunningTasks(1).get(0).topActivity.getClassName();
            } catch (Exception e) {
                IdsLog.d("xiezhixian", e.getMessage() + "");
            }
            if (MediaState.getAudioState() || topActivityName.contains("AudioHalfSingleCallActivity")) {
                csAudio.audioHandUp(id, IMType.IM_TYPE_AUDIO_CALL, UiCauseConstants.CAUSE_CALL_CONFLICT);
            } else {
                CallHelper.getInstance(mContext).receiveCall(IMType.IM_TYPE_AUDIO_CALL, id, pcPeerNum, pcPeerName, mediaAttribute);
                MediaState.setAudioState(true);
                MediaState.setCurrentState(Constant.IN_Half_SINGLE_CALL);
            }
        } else if (iVideoRecv == 0 && iVideoSend == 1) {
            if (MyApplication.Companion.isJoinMetting()) {
                //表示我正在会议时收到视频调用
                MediaAttribute pAttr3 = new MediaAttribute(0, 0, 1, 0);
                IDSApiProxyMgr.getCurProxy().CallAnswer(id, pAttr3, 6);
                return;
            }//视频上传
            CallHelper.getInstance(mContext).receiveCall(IMType.IM_TYPE_VIDEO_UP, id, pcPeerNum, pcPeerName, mediaAttribute);
        } else if (iVideoRecv == 1 && iVideoSend == 0) {//视频下载
            if (MediaState.getVideoState()) {
                csAudio.audioHandUp(id, IMType.IM_TYPE_VIDEO_DOWN, UiCauseConstants.CAUSE_CALL_CONFLICT);
            } else {
                CallHelper.getInstance(mContext).receiveCall(IMType.IM_TYPE_VIDEO_DOWN, id, pcPeerNum, pcPeerName, mediaAttribute);
            }
        } else if (iVideoRecv == 1 && iVideoSend == 1 && iAudioRecv == 1 && iAudioSend == 1) {//视频呼叫
            CallHelper.getInstance(mContext).receiveCall(IMType.IM_TYPE_VIDEO_CALL, id, pcPeerNum, pcPeerName, mediaAttribute);
        }
    }

}
