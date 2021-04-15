package com.sunny.zhrtc.constants;

import android.os.Environment;


/**
 * 常量类
 */

public class C {


    public static final class Receiver {
        public static final String LOGIN_SUCCESS = "login_success";
        public static final String LOGIN_FAILURE = "login_failure";
        public final static String GET_TASK_COUNT = "com.ids.idtma.intent.GET_TASK_COUNT";//工作模块任务数量的广播
        public static final String CHANGE_SKIN_BLUE = "change_skin_blue";//换肤蓝色广播
        public static final String CHANGE_SKIN_YELLOW = "change_skin_yellow";
        /**********  各个机器所带的广播值 ptt sos....   ***************/
        public final static String PRESS_PTT_KEY = "android.intent.action.PTT.down";// com.yl.ptt.keydown

        public final static String RELEASE_PTT_KEY = "android.intent.action.PTT.up";// com.yl.ptt.keyup
        public static final String KNOB_CLOCKWISE_ACTION = "android.intent.action.volumeDownKey.down";//旋钮 - 逆时针 - 广播
        public static final String KNOB_COUNTERLOCKWISE_ACTION = "android.intent.action.volumeUpKey.down";//旋钮 - 逆时针 - 广播
        public static final String ACTION_CALL_RELEASE = "call_release";
        public final static String PRESS_PTT_KEY28 = "com.aoro.poc.key.down";//遨游厂家机器
        public final static String RELEASE_PTT_KEY28 = "com.aoro.poc.key.up";
        public final static String PRESS_PTT_KEY2 = "android.intent.action.side_key.keydown.PTT";
        public final static String RELEASE_PTT_KEY2 = "android.intent.action.side_key.keyup.PTT";


    }


    public static final class FilePath {
        public static String voiceCallPath = Environment.getExternalStorageDirectory() + "/IDT-MA/voiceCall/";
        public static final String VIDEO_CALL_PATH = Environment.getExternalStorageDirectory() + "/IDT-MA/videoCall/";
        public static final String AAC_PATH = Environment.getExternalStorageDirectory() + "/adts.aac";


    }

    public static final class IDTCode {
        public static final long REQ_GROUP_DATA = 100;//查询组
        public static final int REQ_ADDITIONAL_GROUP_DATA = 200;//查询关联组
        public static final long QUERY_THE_MEMBE = 300;//查询用户信息并显示
        public static final long MODIFY_THE_MEMBE = 400;//修改用户信息
        public static final long GET_USER_INFO_PHONE = 500;//查询用户信息并且得到其用户信息里面的手机号
        public static final long QueryNodeData = 600;//查询该节点下所有用户
        public static final long AddGroup = 700;//建组
        public static final int AddGroupUser = 800;//组里面的添加用户
        public static final int VideoUpCallQuery = 900;
        public static final int HalfSingleCall = 1000;//查询该帐号是否开了版双工
        public static final int HalfSingleCallOut = 1100;//查询该帐号是否开了版双工
        public static final int MODIFY_GROUP_DATA = 1200;//刷新组
        public static final int ModifyGroup = 1300;//修改组
        public static final int DeleteGroup = 1400;//
        public static final int DelectUser = 1500;//
        public static final int QueryNodeGroupData = 1700;//
        public static final int NO_ACCOUNT = 17;// 用户不存在
        public static final int SERVER_TIME_OUT = 14;// 连接超时
        public static final int PASSWORD_ERR = 24;// 密码错误
        public static final int ACCOUNT_ISALREADY_REGISTERD = 33;// 该帐号在别处登录
        public static final int CLINE_AND_PHONE_ORDER = 17;// 调度台和终端的指定信息传输

    }

    public static final class intentKeys {
        public static final String TO_WHERE = "to_where";//目标是群组还是个人
        public static final String TARGET_NUM = "target_num";//目标账号
        public static final String TARGET_NAME = "target_name";//目标用户名.
        public static final String USER_GROUP_DATA = "UserGroupData";//UserGroupData实体类
        public static final String TAKE_PHOTO_STRING = "take_photo_String";//UserGroupData实体类
        public static final String TASK_CLASS = "task_class";
        public static final int TO_PERSON = 0;
        public static final int TO_GROUP = 1;
        public static final int TAKE_PHOTO = 101;
        public static final int SELECT_PHOTO = 102;
        public static final int SELECT_FILE = 103;
        public static final int VIDEO_RECORDER = 104;
        public static final int IM_GET_LOCATION = 105;
        public static final int COMMON_REQUEST_CODE = 100;
        public static final int NUM_DIAPAD_REQUEST_CODE = 106;
        public static final String DATA = "data";//通用传递数据的key
        public static final String TYPE = "type";//通用传递类型的key
        public static final String LOGIN_TYPE = "login_type";//跳转到登录界面的方式:被挤下线,注销,还是正常登录
        public static final String RecordVideoPath = "RecordVideoPath";//录像的路径key
        public static final String RecordVideoFlag = "RecordVideoFlag";//跳转录像的flag

        public static final String DMR_MODEL = "dmr_model";
    }

    /**
     * 键值
     */
    public static final class KeyEvent {
        public static final int KNOB_CLOCKWISE = 269;//旋钮 - 顺时针 - 键值
        public static final int KNOB_COUNTERLOCKWISE = 270;//旋钮 - 逆时针 - 键值
    }

}
