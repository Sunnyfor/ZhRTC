package com.sunny.zhrtc.constants;

import android.content.Context;

import com.sunny.zhrtc.R;
import com.sunny.zy.ZyFrameStore;

public class UiCauseConstants {
    public static final int CAUSE_ZERO = 0x00; // null
    public static final int CAUSE_UNASSIGNED_NUMBER = 0x01; // 未分配号码
    public static final int CAUSE_NO_ROUTE_TO_DEST = 0x02; // 无目的路由
    public static final int CAUSE_USER_BUSY = 0x03;// 用户忙
    public static final int CAUSE_ALERT_NO_ANSWER = 0x04;// 用户无应答(人不应答)
    public static final int CAUSE_CALL_REJECTED = 0x05; // 呼叫被拒绝
    public static final int CAUSE_ROUTING_ERROR = 0x06; // 路由错误
    public static final int CAUSE_FACILITY_REJECTED = 0x07; // 设备拒绝
    public static final int CAUSE_ERROR_IPADDR = 0x08; // 错误IP地址过来的呼叫
    public static final int CAUSE_NORMAL_UNSPECIFIED = 0x09; // 通常,未指定
    public static final int CAUSE_TEMPORARY_FAILURE = 0x0A; // 临时错误
    public static final int CAUSE_RESOURCE_UNAVAIL = 0x0B; // 资源不可用
    public static final int CAUSE_INVALID_CALL_REF = 0x0C; // 不正确的呼叫参考号
    public static final int CAUSE_MANDATORY_IE_MISSING = 0x0D; // 必选信息单元丢失
    public static final int CAUSE_TIMER_EXPIRY = 0x0E; // 定时器超时
    public static final int CAUSE_CALL_REJ_BY_USER = 0x0F; // 被用户拒绝
    public static final int CAUSE_CALLEE_STOP = 0x10; // 被叫停止
    public static final int CAUSE_USER_NO_EXIST = 0x11; // 用户不存在
    public static final int CAUSE_MS_UNACCESSAVLE = 0x12; // 不可接入
    public static final int CAUSE_MS_POWEROFF = 0x13; // 用户关机
    public static final int CAUSE_FORCE_RELEASE = 0x14; // 强制拆线
    public static final int CAUSE_HO_RELEASE = 0x15; // 切换拆线
    public static final int CAUSE_CALL_CONFLICT = 0x16; // 呼叫冲突
    public static final int CAUSE_TEMP_UNAVAIL = 0x17; // 暂时无法接通
    public static final int CAUSE_AUTH_ERROR = 0x18; // 鉴权错误
    public static final int CAUSE_NEED_AUTH = 0x19; // 需要鉴权
    public static final int CAUSE_SDP_SEL = 0x1A; // SDP选择错误
    public static final int CAUSE_MS_ERROR = 0x1B; // 媒体资源错误
    public static final int CAUSE_INNER_ERROR = 0x1C; // 内部错误
    public static final int CAUSE_PRIO_ERROR = 0x1D; // 优先级不够
    public static final int CAUSE_SRV_CONFLICT = 0x1E; // 业务冲突
    public static final int CAUSE_NOTREL_RECALL = 0x1F; // 由于业务要求,不释放,启动重呼定时器
    public static final int CAUSE_NO_CALL = 0x20; // 呼叫不存在
    public static final int CAUSE_DUP_REG = 0x21; // 重复注册
    public static final int CAUSE_MG_OFFLINE = 0x22; // MG离线
    public static final int CAUSE_DISP_REQ_QUITCALL = 0x23; // 调度员要求退出呼叫
    public static final int CAUSE_DB_ERROR = 0x24; // 数据库操作错误
    public static final int CAUSE_TOOMANY_USER = 0x25; // 太多的用户
    public static final int CAUSE_SAME_USERNUM = 0x26; // 相同的用户号码
    public static final int CAUSE_SAME_USERIPADDR = 0x27; // 相同的固定IP地址
    public static final int CAUSE_PARAM_ERROR = 0x28; // 参数错误
    public static final int CAUSE_SAME_GNUM = 0x29; // 相同的组号码
    public static final int CAUSE_TOOMANY_GROUP = 0x2A; // 太多的组
    public static final int CAUSE_NO_GROUP = 0x2B; // 没有这个组
    public static final int CAUSE_SAME_USERNAME = 0x2C; // 相同的用户名字
    public static final int CAUSE_OAM_OPT_ERROR = 0x2D; // OAM操作错误
    public static final int CAUSE_INVALID_NUM_FORMAT = 0x2E; // 不正确的地址格式
    public static final int CAUSE_INVALID_DNSIP = 0x2F; // DNS或IP地址错误
    public static final int CAUSE_SRV_NOTSUPPORT = 0x30; // 不支持的业务
    public static final int CAUSE_MEDIA_NOTDATA = 0x31; // 没有媒体数据
    public static final int CAUSE_RECALL = 0x32; // 重新呼叫
    public static final int CAUSE_LINK_DISC = 0x33; // 断链
    public static final int CAUSE_ORG_RIGHT = 0x34; // 组织越权(节点)
    public static final int CAUSE_SAME_ORGNUM = 0x35; // 相同的组织号码
    public static final int CAUSE_SAME_ORGNAME = 0x36; // 相同的组织名字
    public static final int CAUSE_UNASSIGNED_ORG = 0x37; // 未分配的组织号码
    public static final int CAUSE_INOTHER_ORG = 0x38; // 在其他组织中
    public static final int CAUSE_HAVE_GCALL = 0x39; // 已经有组呼存在
    public static final int CAUSE_HAVE_CONF = 0x3A; // 已经有会议存在
    public static final int CAUSE_SEG_FORMAT = 0x3B; // 错误的号段格式
    public static final int CAUSE_USEG_CONFLICT = 0x3C; // 用户号码段冲突
    public static final int CAUSE_GSEG_CONFLICT = 0x3D; // 组号码段冲突
    public static final int CAUSE_NOTIN_SEG = 0x3E; // 不在号段内
    public static final int NO_SUPPORT_CAMERA = 100; // 不支持摄像头

    public static final int CAUSE_OUTNETWORK_NUM = 65; // 外网用户
    public static final int CAUSE_CFU = 66; // 无条件呼叫前转
    public static final int CAUSE_CFB = 67; // 遇忙呼叫前转
    public static final int CAUSE_CFNRc = 68; // 不可及呼叫前转
    public static final int CAUSE_CFNRy = 69; // 无应答呼叫前转
    public static final int CAUSE_MAX_FWDTIME = 70; // 到达最大前转次数

    public static final int CAUSE_MAX = 0x1fff; // *********
    public static final int CAUSE_EXPIRE_IDT = 0x0000; // IDT定时器超时***********
    public static final int CAUSE_EXPIRE_MC = 0x4000; // MC定时器超时
    public static final int CAUSE_EXPIRE_MG = 0x8000; // MG定时器超时
    private Context context;

    public UiCauseConstants() {
        super();
        context = ZyFrameStore.INSTANCE.getContext();
    }

    public String getDataType(int uiCause, Context context) {
        String uiCauseStatusSummary = context.getResources().getString(R.string.not_know_uiCause) + uiCause;
        if (CAUSE_TIMER_EXPIRY == (uiCause & 0xff)) {
            int ucSrc = (uiCause & 0xc000);
            int ucH = (uiCause & 0x3f00) >> 8;
            switch (ucSrc) {
                case CAUSE_EXPIRE_IDT:// IDT定时器超时

                    uiCauseStatusSummary = "IDT定时器超时" + GetIdtTmStr(ucH);
                    break;
                case CAUSE_EXPIRE_MC:// MC定时器超时
                    uiCauseStatusSummary = "MC定时器超时" + GetIdtTmStr(ucH);
                    break;
                case CAUSE_EXPIRE_MG:// MG定时器超时
                    uiCauseStatusSummary = "MG定时器超时" + GetIdtTmStr(ucH);
                    break;
                default:
                    uiCauseStatusSummary = "定时器超时";
                    break;
            }
        }
        switch (uiCause) {
            case CAUSE_ZERO:
                uiCauseStatusSummary = context.getResources().getString(R.string.normal);
                break;
            case CAUSE_UNASSIGNED_NUMBER:
                uiCauseStatusSummary = context.getResources().getString(R.string.no_distribution_number);
                break;
            case CAUSE_NO_ROUTE_TO_DEST:
                uiCauseStatusSummary = context.getResources().getString(R.string.Aimless_route);
                break;
            case CAUSE_USER_BUSY:
                uiCauseStatusSummary = context.getResources().getString(R.string.user_busy);
                break;
            case CAUSE_ALERT_NO_ANSWER:
                uiCauseStatusSummary = context.getResources().getString(R.string.user_no_take);
                break;
            case CAUSE_CALL_REJECTED:
                uiCauseStatusSummary = context.getResources().getString(R.string.Call_rejected);
                break;
            case CAUSE_ROUTING_ERROR:
                uiCauseStatusSummary = context.getResources().getString(R.string.route_err);
                break;
            case CAUSE_FACILITY_REJECTED:
                uiCauseStatusSummary = context.getResources().getString(R.string.refuse_equipment);
                break;
            case CAUSE_ERROR_IPADDR:
                uiCauseStatusSummary = context.getResources().getString(R.string.err_ip_call);
                break;
            case CAUSE_NORMAL_UNSPECIFIED:
                uiCauseStatusSummary = context.getResources().getString(R.string.normal_fuck);
                break;
            case CAUSE_TEMPORARY_FAILURE:
                uiCauseStatusSummary = context.getResources().getString(R.string.temp_err);
                break;
            case CAUSE_RESOURCE_UNAVAIL:
                uiCauseStatusSummary = context.getResources().getString(R.string.resource_not_use);
                break;
            case CAUSE_INVALID_CALL_REF:
                uiCauseStatusSummary = context.getResources().getString(R.string.err_call_num);
                break;
            case CAUSE_MANDATORY_IE_MISSING:
                uiCauseStatusSummary = context.getResources().getString(R.string.bixuan_smsdishi);
                break;
            case CAUSE_TIMER_EXPIRY:
                uiCauseStatusSummary = context.getResources().getString(R.string.timer_time_out);
                break;
            case CAUSE_CALL_REJ_BY_USER:
                uiCauseStatusSummary = context.getResources().getString(R.string.user_refused);
                break;
            case CAUSE_CALLEE_STOP:
                uiCauseStatusSummary = context.getResources().getString(R.string.callde_stop);
                break;
            case CAUSE_USER_NO_EXIST:
                uiCauseStatusSummary = context.getResources().getString(R.string.user_no_exist);
                break;
            case CAUSE_MS_UNACCESSAVLE:
                uiCauseStatusSummary = context.getResources().getString(R.string.not_accessible);
                break;
            case CAUSE_MS_POWEROFF:
                uiCauseStatusSummary = context.getResources().getString(R.string.user_close_phone);
                break;
            case CAUSE_FORCE_RELEASE:
                uiCauseStatusSummary = context.getResources().getString(R.string.Forced_stitches);
                break;
            case CAUSE_HO_RELEASE:
                uiCauseStatusSummary = context.getResources().getString(R.string.Disconnect_switch);
                break;
            case CAUSE_CALL_CONFLICT:
                uiCauseStatusSummary = context.getResources().getString(R.string.Call_collision);
                break;
            case CAUSE_TEMP_UNAVAIL:
                uiCauseStatusSummary = context.getResources().getString(R.string.Can_not_be_connected);
                break;
            case CAUSE_AUTH_ERROR:
                uiCauseStatusSummary = context.getResources().getString(R.string.Authentication_Error);
                break;
            case CAUSE_NEED_AUTH:
                uiCauseStatusSummary = context.getResources().getString(R.string.need_Authentication);
                break;
            case CAUSE_SDP_SEL:
                uiCauseStatusSummary = context.getResources().getString(R.string.sdp_chose_err);
                break;
            case CAUSE_MS_ERROR:
                uiCauseStatusSummary = context.getResources().getString(R.string.Media_Resource_Error);
                break;
            case CAUSE_INNER_ERROR:
                uiCauseStatusSummary = context.getResources().getString(R.string.internal_error);
                break;
            case CAUSE_PRIO_ERROR:
                uiCauseStatusSummary = context.getResources().getString(R.string.Priority_not_enough);
                break;
            case CAUSE_SRV_CONFLICT:
                uiCauseStatusSummary = context.getResources().getString(R.string.Business_conflict);
                break;
            case CAUSE_NOTREL_RECALL:
                uiCauseStatusSummary = context.getResources().getString(R.string.Automatic_Recall);
                break;
            case CAUSE_NO_CALL:
                uiCauseStatusSummary = context.getResources().getString(R.string.call_not_exist);
                break;
            case CAUSE_DUP_REG:
                uiCauseStatusSummary = context.getResources().getString(R.string.Repeat_registration);
                break;
            case CAUSE_MG_OFFLINE:
                uiCauseStatusSummary = context.getResources().getString(R.string.MG_offLine);
                break;
            case CAUSE_DISP_REQ_QUITCALL:
                uiCauseStatusSummary = context.getResources().getString(R.string.leave_the_call_dispatcher);
                break;
            case CAUSE_DB_ERROR:
                uiCauseStatusSummary = context.getResources().getString(R.string.db_operation_err);
                break;
            case CAUSE_TOOMANY_USER:
                uiCauseStatusSummary = context.getResources().getString(R.string.to_much_user);
                break;
            case CAUSE_SAME_USERNUM:
                uiCauseStatusSummary = context.getResources().getString(R.string.subscriber_number);
                break;
            case CAUSE_SAME_USERIPADDR:
                uiCauseStatusSummary = context.getResources().getString(R.string.subscriber_guding_ip);
                break;
            case CAUSE_PARAM_ERROR:
                uiCauseStatusSummary = context.getResources().getString(R.string.Parameter_error);
                break;
            case CAUSE_SAME_GNUM:
                uiCauseStatusSummary = context.getResources().getString(R.string.subscriber_group_number);
                break;
            case CAUSE_TOOMANY_GROUP:
                uiCauseStatusSummary = context.getResources().getString(R.string.to_much_group);
                break;
            case CAUSE_NO_GROUP:
                uiCauseStatusSummary = context.getResources().getString(R.string.no_exist_group);
                break;
            case CAUSE_SAME_USERNAME:
                uiCauseStatusSummary = context.getResources().getString(R.string.subscriber_user_name);
                break;
            case CAUSE_OAM_OPT_ERROR:
                uiCauseStatusSummary = context.getResources().getString(R.string.OAM_err);
                break;
            case CAUSE_INVALID_NUM_FORMAT:
                uiCauseStatusSummary = context.getResources().getString(R.string.err_address);
                break;
            case CAUSE_SRV_NOTSUPPORT:
                uiCauseStatusSummary = "不支持的业务";
                break;
            case CAUSE_MEDIA_NOTDATA:
                uiCauseStatusSummary = "没有媒体数据";
                break;
            case CAUSE_RECALL:
                uiCauseStatusSummary = "重新呼叫";
                break;
            case CAUSE_LINK_DISC:
                uiCauseStatusSummary = "断链";
                break;
            case CAUSE_ORG_RIGHT:
                uiCauseStatusSummary = "组织越权(节点)";
                break;
            case CAUSE_SAME_ORGNUM:
                uiCauseStatusSummary = "相同的组织号码";
                break;
            case CAUSE_SAME_ORGNAME:
                uiCauseStatusSummary = "相同的组织名字";
                break;
            case CAUSE_UNASSIGNED_ORG:
                uiCauseStatusSummary = "未分配的组织号码";
                break;
            case CAUSE_INOTHER_ORG:
                uiCauseStatusSummary = "在其他组织中";
                break;
            case CAUSE_HAVE_GCALL:
                uiCauseStatusSummary = "已经有组呼存在";
                break;
            case CAUSE_HAVE_CONF:
                uiCauseStatusSummary = "已经有会议存在";
                break;
            case CAUSE_EXPIRE_MC:
                uiCauseStatusSummary = context.getResources().getString(R.string.MC_timer_timeOut);
                break;
            case CAUSE_SEG_FORMAT:
                uiCauseStatusSummary = context.getResources().getString(R.string.MG_timer_timeOut);
                break;
            case NO_SUPPORT_CAMERA:
                uiCauseStatusSummary = context.getResources().getString(R.string.no_spport_camera);
                break;
            case CAUSE_USEG_CONFLICT:
                uiCauseStatusSummary = "错误的号段格式";
                break;
            case CAUSE_GSEG_CONFLICT:
                uiCauseStatusSummary = "用户号码段冲突";
                break;
            case CAUSE_NOTIN_SEG:
                uiCauseStatusSummary = "组号码段冲突";
                break;
            case CAUSE_EXPIRE_MG:
                uiCauseStatusSummary = "不在号段内";
                break;
            case CAUSE_OUTNETWORK_NUM:
                uiCauseStatusSummary = "外网用户";
                break;
            case CAUSE_CFU:
                uiCauseStatusSummary = "无条件呼叫前转";
                break;
            case CAUSE_CFB:
                uiCauseStatusSummary = "遇忙呼叫前转";
                break;
            case CAUSE_CFNRc:
                uiCauseStatusSummary = "不可及呼叫前转";
                break;
            case CAUSE_CFNRy:
                uiCauseStatusSummary = "无应答呼叫前转";
                break;
            case CAUSE_MAX_FWDTIME:
                uiCauseStatusSummary = "到达最大前转次数";
                break;
            default:
                break;
        }
        return uiCauseStatusSummary;
    }

    // #define CPTM_MM_REG 0x10 // 注册请求
    // #define CPTM_MM_OFFLINE 0x11 // 离线扫描
    // #define CPTM_MM_PERIOD 0x12 // 周期注册
    // #define CPTM_MM_MODIFY 0x13 // 修改用户属性
    // #define CPTM_MM_NAT 0x14 // NAT
    //
    // #define CPTM_CC_SETUPACK 0x20 // 发送SETUP,等待SETUP_ACK
    // #define CPTM_CC_CONN 0x21 // 发送SETUP,等待CONN
    // #define CPTM_CC_ANSWER 0x22 // 发送CallIn给用户,等用户应答
    // #define CPTM_CC_CONNACK 0x23 // 发送CONN给对端,等待CONNACK
    // #define CPTM_CC_HB 0x24 // 通话后的心跳定时器
    private String GetIdtTmStr(int wTm) {
        String aa = "";
        switch (wTm) {
            case CPTM_MM_REG:
                aa = "CPTM_MM_REG";
                break;
            case CPTM_MM_OFFLINE:
                return "CPTM_MM_OFFLINE";
            case CPTM_MM_PERIOD:
                return "CPTM_MM_PERIOD";
            case CPTM_MM_MODIFY:
                return "CPTM_MM_MODIFY";
            case CPTM_MM_NAT:
                return "CPTM_MM_NAT";
            case CPTM_CC_SETUPACK:
                return "CPTM_CC_SETUPACK";
            case CPTM_CC_CONN:
                return "CPTM_CC_CONN";
            case CPTM_CC_ANSWER:
                return "CPTM_CC_ANSWER";
            case CPTM_CC_CONNACK:
                return "CPTM_CC_CONNACK";
            case CPTM_CC_HB:
                return "CPTM_CC_HB";
        }
        return null;
    }

    private static final int CPTM_MM_REG = 0x10;
    private static final int CPTM_MM_OFFLINE = 0x11;
    private static final int CPTM_MM_PERIOD = 0x12;
    private static final int CPTM_MM_MODIFY = 0x13;
    private static final int CPTM_MM_NAT = 0x14;
    private static final int CPTM_CC_SETUPACK = 0x20;
    private static final int CPTM_CC_CONN = 0x21;
    private static final int CPTM_CC_ANSWER = 0x22;
    private static final int CPTM_CC_CONNACK = 0x23;
    private static final int CPTM_CC_HB = 0x24;
}
