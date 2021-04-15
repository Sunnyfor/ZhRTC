package com.sunny.zhrtc.mian;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ids.idtma.biz.core.proxy.IDSApiProxyMgr;
import com.ids.idtma.ftp.FtpUtils;
import com.ids.idtma.jni.aidl.IMMsgEntity;
import com.ids.idtma.media.CSMediaCtrl;
import com.ids.idtma.util.Compat;
import com.ids.idtma.util.DateUtils;
import com.ids.idtma.util.constants.IMType;
import com.sunny.zhrtc.MyApplication;
import com.sunny.zhrtc.R;
import com.sunny.zhrtc.bean.GpsSetUpData;
import com.sunny.zhrtc.bean.MeetingMsgData;
import com.sunny.zhrtc.bean.MessageCommData;
import com.sunny.zhrtc.bean.Person;
import com.sunny.zhrtc.bean.ResolutionData;
import com.sunny.zhrtc.bean.SoSData;
import com.sunny.zhrtc.bean.SosSwitchEntity;
import com.sunny.zhrtc.bean.VideoPara;
import com.sunny.zhrtc.constants.C;
import com.sunny.zhrtc.constants.CallConstants;
import com.sunny.zhrtc.util.FtpListenerUtils;
import com.sunny.zhrtc.util.RingtoneUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GlobalReceiverUtils {
    private static final String TAG = "GlobalReceiverUtils";

    public static void receiverMessage(IMMsgEntity imEntity, String data, Context context) {
        if (imEntity.getDwType() == IMType.IM_TYPE_MEETING_MSG) {
            //其中17 的时候为会议时
            setUpOnline(imEntity, context);
        } else {
            String content = "";
            switch (imEntity.getDwType()) {
                case IMType.IM_TYPE_TXT:
                    content = imEntity.getPcTxt();
                    break;
                case IMType.IM_GROUP_CALL:
                    content = "[组呼消息]";
                    break;
                case IMType.IM_TYPE_AUDIO_CALL:
                    content = "[语音单呼]";
                    break;
                case IMType.IM_TYPE_FILE:
                    content = "[文件]";
                    break;
                case IMType.IM_TYPE_GPS:
                    content = "[位置信息]";
                    break;
                case IMType.IM_TYPE_VIDEO_CALL:
                    content = "[视频消息]";
                    break;
                case IMType.IM_TYPE_IMAGE:
                    content = "[图片]";
                    break;
                case IMType.IM_TYPE_VIDEO_UP:
                    content = "[视频上传]";
                    break;
                case IMType.VIDEO_TRAN:
                    content = "[拍传]";
                    break;
                case IMType.IM_TYPE_VIDEO_DOWN:
                    content = "[视频调用]";
                    break;
                case IMType.IM_TYPE_HISTORY_VIDEO:
                    content = "[历史视频]";
                    break;
                case IMType.IM_TYPE_GROUP_VOICE_MSG:

                    content = "[组呼录音]";
                    break;
                case IMType.IM_TYPE_VIDEO:
                    content = "[录像]";
                    break;

            }
            Toast.makeText(context, "收到:" + content, Toast.LENGTH_SHORT).show();
            if (imEntity.getPcTo().contains("#")) {
                //TODO 发送方的号码带# 说明为组  格式为 # + 组号码
            }
            String serverPath = "/IM/" + imEntity.getPcFrom() + "/";
            FtpUtils.downLoadSingleFile2(serverPath, imEntity.getPcFileName(), "本地路径", imEntity.getPcSourceFileName(),
                    context, new FtpListenerUtils() {
                        @Override
                        public void Success(String fileName) {
                            super.Success(fileName);

                        }
                    });
        }
    }

    //显示视频会议的dialog
    private static void showMeetingDialog(final IMMsgEntity imEntity, final Context context) {
//        if (meetingDialogFlag ) return;
//        meetingDialogFlag = true;
        Gson gson = new Gson();
        MessageCommData messageCommData = gson.fromJson(imEntity.getPcTxt(), MessageCommData.class);
        final MeetingMsgData meetingMsgData = gson.fromJson(messageCommData.getSubPara(), MeetingMsgData.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View view = View.inflate(context, R.layout.dialog_common, null);

        TextView tvTitle = view.findViewById(R.id.title);
        TextView tvContainer = view.findViewById(R.id.container);
        TextView tvSure = view.findViewById(R.id.sure);
        TextView tvCancel = view.findViewById(R.id.cancel);
        tvContainer.setText("会议邀请");
        tvTitle.setText("title");
        final AlertDialog alertDialog = builder.setView(view).create();
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                String pcTo = meetingMsgData.getNumber();
                if (pcTo.contains("#")) {
                    pcTo = pcTo.replace("#", "");
                }
//                String name = UserAttrUtils.getName(pcTo);
                CallHelper.getInstance(context).callMeetingVideo(pcTo, pcTo, IMType.IM_TYPE_MEETING, -1, CallConstants.meetingFromGroup);
//                meetingDialogFlag = false;
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        alertDialog.show();
    }

    //这个为类型17的调度台对终端的某些设置
    private static void setUpOnline(IMMsgEntity imEntity, final Context context) {
        String localPath = "";//本地头像路径
        String pcTxt = imEntity.getPcTxt();
        String pcTo = imEntity.getPcTo();
        final String pcFrom = imEntity.getPcFrom();
        Gson gson = new Gson();
        MessageCommData messageCommData = gson.fromJson(pcTxt, MessageCommData.class);
        if (messageCommData != null) {
            if (messageCommData.getType() == 3) {//表示对方更改了头像
                Person fromJson = gson.fromJson(messageCommData.getSubPara(), Person.class);
                FtpUtils.downLoadSingleFile("服务器上面的头像路径", fromJson.getPicUrl(), localPath, fromJson.getPicUrl(), context, new FtpListenerUtils() {
                    @Override
                    public void Success(String fileName) {
                        super.Success(fileName);
                    }
                });
            } else if (messageCommData.getType() == 0) {// 会议相关
                showMeetingDialog(imEntity, context);

            } else if (messageCommData.getType() == 1) {// gps参数相关
                GpsSetUpData gpsData = gson.fromJson(messageCommData.getSubPara(), GpsSetUpData.class);
                if (gpsData != null && gpsData.type == 0) {// 查询gps开关状态
                    boolean isOpenGps = false;
                    String timeStr = "3000";
                    GpsSetUpData toGpaData = new GpsSetUpData(1, isOpenGps, Integer.parseInt(timeStr) / 1000);
                    MessageCommData gpsMessage = new MessageCommData(1, messageCommData.messageId, pcTo, messageCommData.toDesc, pcFrom,
                            messageCommData.fromDesc, gson.toJson(toGpaData));
                    IDSApiProxyMgr.getCurProxy().iMSend(0, 17, pcFrom, gson.toJson(gpsMessage), null, "", null, 0);
                } else if (gpsData != null && gpsData.type == 2) {// 设置gps开关
                    if (gpsData.open) {// 开gps
                        GpsSetUpData toGpaData = new GpsSetUpData(3, true, gpsData.frequencyTime);
                        MessageCommData gpsMessage = new MessageCommData(1, messageCommData.messageId, pcTo, messageCommData.toDesc, pcFrom,
                                messageCommData.fromDesc, gson.toJson(toGpaData));
                        IDSApiProxyMgr.getCurProxy().iMSend(0, 17, pcFrom, gson.toJson(gpsMessage), null, "", null, 0);
                    } else {
                        GpsSetUpData toGpaData = new GpsSetUpData(3, false, gpsData.frequencyTime);
                        MessageCommData gpsMessage = new MessageCommData(1, messageCommData.messageId, pcTo, messageCommData.toDesc, pcFrom,
                                messageCommData.fromDesc, gson.toJson(toGpaData));
                        IDSApiProxyMgr.getCurProxy().iMSend(0, 17, pcFrom, gson.toJson(gpsMessage), null, "", null, 0);
                    }
                }
            } else if (messageCommData.getType() == 9) {
                SosSwitchEntity sosSwitchEntity = gson.fromJson(messageCommData.getSubPara(), SosSwitchEntity.class);
                if (sosSwitchEntity != null && sosSwitchEntity.type == 0) {// 查询sos开关状态
                    SosSwitchEntity toSosData = new SosSwitchEntity(1, false);
                    MessageCommData sosMessage = new MessageCommData(9, messageCommData.messageId, pcTo, messageCommData.toDesc, pcFrom,
                            messageCommData.fromDesc, gson.toJson(toSosData));
                    IDSApiProxyMgr.getCurProxy().iMSend(0, 17, pcFrom, gson.toJson(sosMessage), null, "", null, 0);
                } else if (sosSwitchEntity != null && sosSwitchEntity.type == 2) {
                    SosSwitchEntity toSosData = new SosSwitchEntity(3, false);
                    MessageCommData sosMessage = new MessageCommData(9, messageCommData.messageId, pcTo, messageCommData.toDesc, pcFrom,
                            messageCommData.fromDesc, gson.toJson(toSosData));
                    IDSApiProxyMgr.getCurProxy().iMSend(0, 17, pcFrom, gson.toJson(sosMessage), null, "", null, 0);
                }
            } else if (messageCommData.getType() == 2) {// 视频参数相关
                ResolutionData resolutionData = gson.fromJson(messageCommData.getSubPara(), ResolutionData.class);
                if (resolutionData != null && resolutionData.getType() == 0) {// 查询视频参数
                    String width = Compat.readIni("RTP_VIDEO_0", "WIDTH", CSMediaCtrl.SEND_PREVIEW_WIDTH + "");
                    String heigth = Compat.readIni("RTP_VIDEO_0", "HEIGHT", CSMediaCtrl.SEND_PREVIEW_HEIGHT + "");
                    String bit = Compat.readIni("RTP_VIDEO_0", "BITRATE", CSMediaCtrl.mBitrate + "");
                    String frame = Compat.readIni("RTP_VIDEO_0", "FPS", CSMediaCtrl.mFrameRate + "");
                    ArrayList<String> resolutionList = new ArrayList<String>();
                    resolutionList.add("320*240");
                    resolutionList.add("640*480");
                    resolutionList.add("480*720");
                    resolutionList.add("1280*720");

                    VideoPara videoPara = new VideoPara(resolutionList, 300, 3000, 5, 30);
                    ResolutionData currentPara = new ResolutionData(1, width + "*" + heigth, Integer.parseInt(bit) / 1000, Integer
                            .parseInt(frame), videoPara);
                    MessageCommData gpsMessage = new MessageCommData(2, messageCommData.messageId, pcTo, messageCommData.toDesc, pcFrom,
                            messageCommData.fromDesc, gson.toJson(currentPara));
                    IDSApiProxyMgr.getCurProxy().iMSend(0, 17, pcFrom, gson.toJson(gpsMessage), null, "", null, 0);
                    resolutionList.clear();
                } else if (resolutionData != null && resolutionData.getType() == 2) {
                    int bitrate = resolutionData.getBitrate();
                    int framerate = resolutionData.getFramerate();
                    String resolution = resolutionData.getResolution();
                    String[] split = resolution.split("\\u002A");
                    Camera camera;
                    try {
                        camera = Camera.open(CSMediaCtrl.cameraId);
                        Camera.Parameters parameters = camera.getParameters();
                        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
                        int width = Integer.parseInt(split[0]);
                        int height = Integer.parseInt(split[1]);
                        if (sizeList.size() > 1) {
                            Iterator<Camera.Size> itor = sizeList.iterator();
                            while (itor.hasNext()) {
                                Camera.Size cur = itor.next();
                                if (cur.width == width && cur.height == height) {
                                    Compat.WriteIni("RTP_VIDEO_0", "WIDTH", split[0]);
                                    Compat.WriteIni("RTP_VIDEO_0", "HEIGHT", split[1]);
                                    break;
                                }
                            }
                        }
                    } catch (RuntimeException e) {
                        return;
                    }
                    Compat.WriteIni("RTP_VIDEO_0", "FPS", "" + framerate);
                    if (split[1].equals("240") && split[0].equals("320")) {
                        Compat.WriteIni("RTP_VIDEO_0", "BITRATE", "300000");
                    } else if (split[1].equals("480") && split[0].equals("640")) {
                        Compat.WriteIni("RTP_VIDEO_0", "BITRATE", "800000");
                    } else if (split[1].equals("720") && split[0].equals("1280")) {
                        Compat.WriteIni("RTP_VIDEO_0", "BITRATE", "2000000");
                    } else if (split[1].equals("1080") && split[0].equals("1920")) {
                        Compat.WriteIni("RTP_VIDEO_0", "BITRATE", "3000000");
                    } else {
                        Compat.WriteIni("RTP_VIDEO_0", "BITRATE", "1200000");
                    }
                    camera.release();
                    ResolutionData currentPara = new ResolutionData(3, split[0] + "*" + split[1], bitrate, framerate, null);
                    MessageCommData gpsMessage = new MessageCommData(2, messageCommData.messageId, pcTo, messageCommData.toDesc, pcFrom,
                            messageCommData.fromDesc, gson.toJson(currentPara));
                    IDSApiProxyMgr.getCurProxy().iMSend(0, 17, pcFrom, gson.toJson(gpsMessage), null, "", null, 0);
                }
            } else if (messageCommData.getType() == 6) {
                String sosNum = Compat.readIni("SYSTEM", "SOSNUM");//表示发sos消息给调度台的num
                SoSData soSData = gson.fromJson(messageCommData.getSubPara(), SoSData.class);
                if (soSData.getType() == 0) {
                    SoSData soSData1 = new SoSData();
                    soSData1.setType(1);
                    soSData1.setSosNumber(sosNum);
                    soSData1.setOpen(MyApplication.Companion.getSosSwitch());
                    MessageCommData sosMessage = new MessageCommData(6, messageCommData.messageId, MyApplication.Companion.getUserAccount(),
                            messageCommData.toDesc, sosNum, sosNum, gson.toJson(soSData1));
                    IDSApiProxyMgr.getCurProxy().iMSend(0, 17, pcFrom, gson.toJson(sosMessage), null, "", null, 0);
                } else if (soSData.getType() == 2) {
                    MyApplication.Companion.setSosSwitch(soSData.isOpen());
//                    SharedPreferencesUtil.setBooleanPreferences(context, C.sp.SOS_Switch, MyApplication.Companion.SosSwitch);
                    SosSwitchEntity toSosData = new SosSwitchEntity(3, MyApplication.Companion.getSosSwitch());
                    MessageCommData sosMessage = new MessageCommData(6, messageCommData.messageId, pcTo, messageCommData.toDesc, pcFrom,
                            messageCommData.fromDesc, gson.toJson(toSosData));
                    IDSApiProxyMgr.getCurProxy().iMSend(0, 17, pcFrom, gson.toJson(sosMessage), null, "", null, 0);
                } else if (soSData.getType() == 4) {//收到别的终端sos
                    //收到终端发来的sos消息需要播放警铃声
//                    if (RingtoneUtils.isPlay() || Utils.isHaveTheActivity(context, "ReceiveSosActivity"))
//                        return;
                    Intent intent = new Intent(context, ReceiveSosActivity.class);
                    intent.putExtra(C.intentKeys.DATA, imEntity);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else if (soSData.getType() == 6) {
                    RingtoneUtils.stopRingtone();
                    SoSData soSData1 = new SoSData();
                    soSData1.setType(7);
                    soSData1.setOpen(false);
                    soSData1.setSosNumber(sosNum);
                    MessageCommData sosMessage = new MessageCommData(6, DateUtils.formatDate(null, null),
                            MyApplication.Companion.getUserAccount(), messageCommData.toDesc,
                            sosNum, sosNum, gson.toJson(soSData1));

                    IDSApiProxyMgr.getCurProxy().iMSend(0, 17, sosNum, gson.toJson(sosMessage), null, "", null, 0);
                    Toast.makeText(context, "调度台关闭了,我需要关闭sos界面", Toast.LENGTH_SHORT).show();
                    //TODO 关闭当前sos界面
                    //                    for (int i = 0; i < CustomActivityManager.activities.size(); i++) {
//                        if (CustomActivityManager.activities.get(i) instanceof SosActivity) {
//                            CustomActivityManager.activities.get(i).finish();
//                            CustomActivityManager.activities.remove(i);
//                            break;
//                        }
//                    }

                }
            }

        }

    }


    //显示视频会议的dialogActivity
    private static void showMeetingDialogType(IMMsgEntity imEntity, MessageCommData messageCommData, int type,
                                              String title, String content, Context context) {
//        Intent intent = new Intent(context, DialogsActivity.class);
//        intent.putExtra(C.intentKeys.MessageCommData, messageCommData.getSubPara());
//        intent.putExtra(C.intentKeys.IMMsgEntity, imEntity);
//        intent.putExtra(C.intentKeys.TYPE, type);
//        intent.putExtra(C.intentKeys.dialogTitle, title);
//        intent.putExtra(C.intentKeys.dialogContent, content);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        context.startActivity(intent);
    }
}