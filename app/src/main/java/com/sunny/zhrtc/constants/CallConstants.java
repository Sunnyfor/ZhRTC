package com.sunny.zhrtc.constants;

/**
 * Created by Administrator on 2017/11/1.
 */

public class CallConstants {

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
    public final static int meetingFromIdt = 100;//100表示追呼会议
    public final static int meetingFromGroup = 200;//200表示聊天界面自己进入
    public final static String fromType = "fromType";
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
}
