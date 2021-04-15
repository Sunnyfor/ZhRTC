package com.sunny.zhrtc.mian;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ids.idtma.jni.aidl.CallOutEntity;
import com.ids.idtma.jni.aidl.MediaAttribute;
import com.ids.idtma.media.CSAudio;
import com.ids.idtma.media.CSMediaCtrl;
import com.ids.idtma.media.CSVideo;
import com.ids.idtma.media.MediaState;
import com.ids.idtma.person.PersonCtrl;
import com.ids.idtma.util.Compat;
import com.ids.idtma.util.constants.Constant;
import com.ids.idtma.util.constants.IMType;
import com.sunny.zhrtc.MyApplication;
import com.sunny.zhrtc.R;
import com.sunny.zhrtc.constants.CallConstants;

/**
 * 呼叫帮助类
 */
public class CallHelper {

    private static Context context;
    private static CallHelper callHelper;


    public final static String CALL_ID = "callID";// 呼叫ID
    public final static String CALLER = "caller";//主叫num
    public final static String CALLER_NAME = "callerName";//主叫姓名
    public final static String CALLEE = "callee";//被叫
    public final static String CALLEE_NAME = "calleeName";//被叫姓名
    public final static String CALL_STATUS = "callStatus";//呼叫状态
    public final static String CALL_URI = "callUri";//存入呼叫的uri
    public final static String MediaAttr = "mediaAttr";//媒体参数
    public final static String CallContext = "callContext";//视频上下文
    public final static String NOTIFICATION_INTENT = "notificationIntent";
    public final static String SMS_ID = "sms_conversation_id";
    public final static String MEETING_FROM_TYPE = "MeetingFromGroup";
    public final static String SMS_ENTITY = "SMS_ENTITY";
    public final static String CALL_TIME_LENGTH = "CALL_TIME_LENGTH";
    public final static String AudioMultiple = "AudioMultiple";
    public final static String CallEntity = "CallEntity";
    public final static int meetingFromIdt = 100;//100表示追呼会议
    public final static int meetingFromGroup = 200;//200表示聊天界面自己进入
    public final static String fromType = "fromType";
    public final static int FLAG_INCOMING = 0;// 来电
    public final static int FLAG_CALLING = 1;// 正在呼叫
    public final static int FLAG_ANSWER = 2;// 接听
    public final static int CALL_AUTO_ANSWER = 3;// 自动接听
    /**
     * 业务类型：组呼、会议
     */
    public final static int CALL_TYPE_GROUP_CALL = 17;// 0x11
    /**
     * 业务类型：组呼、会议
     */
    public final static int SRV_TYPE_CONF_JOIN = 18;// 0x11
    /**
     * 业务类型：单呼
     */
    public final static int CALL_TYPE_SINGLE_CALL = 16;// 所有单呼
    public final static int SRV_TYPE_WATCH_DOWN = 21; // 视频下载
    public final static int SRV_TYPE_WATCH_UP = 22; // 视频上传
    public final static int FORCE_INSERT = 19; // 强插
    public final static int SRV_INFO_CAMHISPLAY = 12; // 查看历史视频

    private CallHelper() {
    }

    public static CallHelper getInstance(Context context) {
        CallHelper.context = context;
        if (callHelper == null) {
            callHelper = new CallHelper();
            return callHelper;
        }
        return callHelper;
    }

    /**
     * 收到语音或者视频呼叫
     *
     * @param callContext    呼叫上下文
     * @param callId         呼叫id
     * @param pcPeerNum      对方号码
     * @param pcPeerName     对方姓名
     * @param mediaAttribute 媒体参数
     */
    public void receiveCall(int callContext, int callId, String pcPeerNum, String pcPeerName, MediaAttribute mediaAttribute) {
        Intent intent;
        if (mediaAttribute.getUcVideoRecv() == 1 || mediaAttribute.getUcVideoSend() == 1) {
            intent = new Intent(context, VideoCallActivity.class);
            CSMediaCtrl.rotateInt = 90;//当前预览度数
            CSMediaCtrl.SEND_PREVIEW_WIDTH = Integer.parseInt(Compat.readIni("RTP_VIDEO_0", "WIDTH", "320"));//当前视频预览分辨率
            CSMediaCtrl.SEND_PREVIEW_HEIGHT = Integer.parseInt(Compat.readIni("RTP_VIDEO_0", "HEIGHT", "240"));//
            CSMediaCtrl.mFrameRate = Integer.parseInt(Compat.readIni("RTP_VIDEO_0", "FPS", "30"));//当前的帧率
            CSMediaCtrl.mBitrate = Integer.parseInt(Compat.readIni("RTP_VIDEO_0", "BITRATE", "300000"));//当前的码率
            CSMediaCtrl.cameraId = Integer.parseInt(Compat.readIni("RTP_VIDEO_0", "CAMERA", "0"));
        } else {
            CSAudio.setAudioCallId(callId);
            /*if (mediaAttribute.getUcAudioSend() ==1 && mediaAttribute.getUcAudioRecv() == 1) {
                intent = new Intent(context, AudioCallActivity.class);
            }else */
            if (mediaAttribute.getUcAudioSend() == 0 && mediaAttribute.getUcAudioRecv() == 1) {
                intent = new Intent(context, AudioHalfSingleCallActivity.class);
            } else {
                intent = new Intent(context, AudioCallActivity.class);
            }
        }
        intent.putExtra(CallContext, callContext);
        intent.putExtra(CALL_ID, callId);
        intent.putExtra(CALLEE, MyApplication.Companion.getUserAccount());
        intent.putExtra(CALLEE_NAME, MyApplication.Companion.getUserAccount());
        intent.putExtra(CALLER, pcPeerNum);
        intent.putExtra(CALLER_NAME, pcPeerName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MediaAttr, mediaAttribute);

        context.startActivity(intent);
    }


    /**
     * 拨打电话
     *
     * @param srvType        呼叫类型
     * @param callToNumber   对端号码
     * @param callToName     对端姓名
     * @param videoContext   呼叫上下文
     * @param mediaAttribute 媒体属性
     */
    public void callOut(int srvType, String callToNumber, String callToName,
                        int videoContext, MediaAttribute mediaAttribute) {
        Intent intent;
        CallOutEntity entity = new CallOutEntity();
        if (mediaAttribute.getUcVideoRecv() == 1 || mediaAttribute.getUcVideoSend() == 1) {
            intent = new Intent(context, VideoCallActivity.class);

            CSMediaCtrl.rotateInt = 90;//当前预览度数
            CSMediaCtrl.SEND_PREVIEW_WIDTH = Integer.parseInt(Compat.readIni("RTP_VIDEO_0", "WIDTH", "320"));//当前视频预览分辨率
            CSMediaCtrl.SEND_PREVIEW_HEIGHT = Integer.parseInt(Compat.readIni("RTP_VIDEO_0", "HEIGHT", "240"));//
            CSMediaCtrl.mFrameRate = Integer.parseInt(Compat.readIni("RTP_VIDEO_0", "FPS", "30"));//当前的帧率
            CSMediaCtrl.mBitrate = Integer.parseInt(Compat.readIni("RTP_VIDEO_0", "BITRATE", "300000"));//当前的码率
            CSMediaCtrl.cameraId = Integer.parseInt(Compat.readIni("RTP_VIDEO_0", "CAMERA", "0"));//当前摄像头选择
        } else if (mediaAttribute.getUcVideoRecv() == 0 && mediaAttribute.getUcVideoSend() == 0
                && mediaAttribute.getUcAudioRecv() == 0 && mediaAttribute.getUcAudioSend() == 1) {
            intent = new Intent(context, AudioHalfSingleCallActivity.class);
            mediaAttribute = new MediaAttribute(1, 0, 0, 0);
        } else {
            intent = new Intent(context, AudioCallActivity.class);
//            mediaAttribute = new MediaAttribute(1, 1, 0, 0);
        }
        entity.setiAutoMic(0);//默认0
        entity.setPcUserMark(IMType.NORMAL_JSON);//默认NORMAL_JSON  可自定义,这边打电话带什么参数,对方收到的就是什么
        entity.setMediaAttribute(mediaAttribute);
        entity.setCallToNumber(callToNumber);
        entity.setCallToName(callToName);
        entity.setSrvType(srvType);
        entity.setCallContext(videoContext);

        intent.putExtra(CallEntity, entity);
        intent.putExtra(CALLEE, callToNumber);
        intent.putExtra(CALLEE_NAME, callToName);
        intent.putExtra(CALLER, MyApplication.Companion.getUserAccount());
        intent.putExtra(CALLER_NAME, MyApplication.Companion.getUserAccount());
        intent.putExtra(MediaAttr, mediaAttribute);
        intent.putExtra(CallContext, videoContext);
        intent.putExtra(CALL_STATUS, FLAG_CALLING);
        context.startActivity(intent);
    }

    /**
     * 拨打会议电话
     *
     * @param callToNumber
     * @param callToName
     * @param callContext
     * @param ID
     * @param fromType
     */
    public void callMeetingVideo(String callToNumber, String callToName, int callContext, int ID, int fromType) {
        Intent intent = new Intent(context, MeetingCallActivity2.class);
        // 对方号码
        intent.putExtra(CALLEE, MyApplication.Companion.getUserAccount());
        intent.putExtra(CALLER, callToNumber);
        intent.putExtra(NOTIFICATION_INTENT, FLAG_CALLING);
        intent.putExtra(CALLEE_NAME, callToName);
        intent.putExtra(CallContext, callContext);
        MediaAttribute mediaAttribute = new MediaAttribute();
        mediaAttribute.setUcAudioRecv(1);
        mediaAttribute.setUcAudioSend(0);
        mediaAttribute.setUcVideoRecv(1);
        mediaAttribute.setUcVideoSend(0);
        if (fromType == 100) {
            CSVideo.getInstence().callAnswer(ID, mediaAttribute);
        } else {
            ID = CSVideo.getInstence().videoMettingCall(callToNumber, mediaAttribute, SRV_TYPE_CONF_JOIN, IMType.IM_TYPE_MEETING);
        }
        intent.putExtra(CallConstants.MediaAttr, mediaAttribute);
        intent.putExtra(CALL_ID, ID);
        intent.putExtra(CallHelper.fromType, fromType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static String CURRENT_GROUP_NUM = "";//当前对讲组号码
    public static String CURRENT_GROUP_NAME = "";//当前对讲组名字
    public static String CURRENT_GROUP_CALL_STATE;

    /**
     * 发起组呼
     *
     * @return
     */
    public int callGroup(String groupNum) {
        for (int i = 0; i < PersonCtrl.mGroupData.size(); i++) {
            if (PersonCtrl.mGroupData.get(i).getUcNum().equals(groupNum)) {
                CURRENT_GROUP_NAME = PersonCtrl.mGroupData.get(i).getUcName();
                break;
            }
        }
        // 获取当前组呼的ID
        if (MediaState.getCurrentState() == CSAudio.IN_SINGLE_CALL)
            return MediaState.CAUSE_RESOURCE_UNAVAIL;
        if (MediaState.getCurrentState() != Constant.IN_GROUP && MediaState.getCurrentState() != Constant.IN_Half_SINGLE_CALL) {
            Toast.makeText(context, context.getResources().getString(R.string.my_here) + CallHelper.CURRENT_GROUP_NAME
                    + context.getResources().getString(R.string.intercom_group_send_call), Toast.LENGTH_SHORT).show();
        }
        if (!CallHelper.CURRENT_GROUP_NUM.equals(groupNum)) {
            CSAudio.getInstance().audioHandUp(0x16, IMType.IM_TYPE_GROUP_CALL);
        }
        int groupCallID = CSAudio.getInstance().groupAudioCall(groupNum);
        CURRENT_GROUP_NUM = groupNum;

        return groupCallID;
    }
}
