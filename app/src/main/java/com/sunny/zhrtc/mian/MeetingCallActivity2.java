package com.sunny.zhrtc.mian;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ids.idtma.biz.core.proxy.IDSApiProxyMgr;
import com.ids.idtma.jni.aidl.CallAnswerEntity;
import com.ids.idtma.jni.aidl.CallInEntity;
import com.ids.idtma.jni.aidl.CallReleaseEntity;
import com.ids.idtma.jni.aidl.CallTalkingEntity;
import com.ids.idtma.jni.aidl.IDTMsgType;
import com.ids.idtma.jni.aidl.MediaAttribute;
import com.ids.idtma.jni.aidl.MediaStreamEntity;
import com.ids.idtma.jni.aidl.VideoCodecEntity;
import com.ids.idtma.media.CSAudio;
import com.ids.idtma.media.CSMediaCtrl;
import com.ids.idtma.media.CSVideo;
import com.ids.idtma.util.Compat;
import com.ids.idtma.util.IdsLog;
import com.ids.idtma.util.constants.IMType;
import com.sunny.zhrtc.MyApplication;
import com.sunny.zhrtc.R;
import com.sunny.zhrtc.base.BaseActivity;
import com.sunny.zhrtc.constants.C;
import com.sunny.zhrtc.constants.CallConstants;

import java.util.Formatter;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MeetingCallActivity2 extends BaseActivity {

    @BindView(R.id.meetingUiLogo)
    ImageView meetingUiLogo;
    @BindView(R.id.video_title_layout)
    RelativeLayout videoTitleLayout;
    @BindView(R.id.tiaoshi3)
    TextView tiaoshi3;
    @BindView(R.id.tv_group_number)
    TextView tvGroupNumber;
    @BindView(R.id.tiaoshi4)
    TextView tiaoshi4;
    @BindView(R.id.tv_talking_user)
    TextView tvTalkingUser;
    @BindView(R.id.relativeLayout1)
    RelativeLayout relativeLayout1;
    @BindView(R.id.stream)
    TextView stream;
    @BindView(R.id.send_stream)
    TextView sendStream;
    @BindView(R.id.chronometer_call_time)
    TextView chronometerCallTime;
    @BindView(R.id.new_ui_audio_call_toplayout)
    RelativeLayout newUiAudioCallToplayout;
    @BindView(R.id.surfaceView_remote)
    SurfaceView surfaceViewRemote;
    @BindView(R.id.full_screen_canel)
    ImageButton fullScreenCanel;
    @BindView(R.id.surfaceView_local)
    SurfaceView surfaceViewLocal;
    @BindView(R.id.set_full_scean)
    ImageButton setFullScean;
    @BindView(R.id.landscape_hangup)
    ImageButton landscapeHangup;
    @BindView(R.id.rl_video_answer)
    RelativeLayout rlVideoAnswer;
    @BindView(R.id.close_speaker)
    ImageButton closeSpeaker;
    @BindView(R.id.btn_speak)
    ImageButton btnSpeak;
    @BindView(R.id.btn_hangup_conned)
    ImageButton btnHangup;
    @BindView(R.id.turnCamera)
    ImageButton turnCamera;
    @BindView(R.id.closeAudio)
    ImageButton closeAudio;
    @BindView(R.id.saveAudio)
    ImageView saveAudio;
    @BindView(R.id.new_ui_audio_call_bottomlayout)
    RelativeLayout newUiAudioCallBottomlayout;

    private int callID;
    private String caller;
    private String callee;
    private int status = 2;
    private SurfaceHolder surfaceHolder_remote;
    private Surface mRemoteSurface;
    private SurfaceHolder surfaceHolder;
    private boolean TIME_THREAD_RUN = true;
    private int meetingVideoId; //表示我正在会议时收到视频调用的id
    //    private boolean isSendMeetingVideo;
    private MediaAttribute mediaAttribute;
    private String calleeName;
    private int surfaceHeight;
    private int surfaceWidth;
    // 是前置摄像头还是后置摄像头
    private boolean setRecordingHint = true;
    private static String TAG = "xiezhixian";
    private final static int STATUS_TALK = 1;
    private final static int STATUS_LISTEN = 2;
    private String saveFrame;
    private long meetingVideoContent = -1;
    private int SEND_STATUS = 0;
    private long time = 0;
    private Handler handler = new Handler();
    private int mNumberOfCameras;
    private int height;
    private boolean silentFlag = false;
    private boolean isCloseAudio;
    private boolean isCloseMaiKe;
    private boolean isFullScreen = false;// 是否全屏
    private int mCameraNum;
    private int fromType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_call);
        ButterKnife.bind(this);

        MyApplication.Companion.setJoinMetting(true);
        initData();
        initStatus();
        initFilter();
        CSVideo.getInstence().initCameraData(surfaceViewLocal, surfaceViewRemote);
    }

    private void initFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(C.Receiver.PRESS_PTT_KEY);
        filter.addAction(C.Receiver.RELEASE_PTT_KEY);
        registerReceiver(broadcastReceiver, filter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            if (C.Receiver.PRESS_PTT_KEY.equals(intent.getAction())) {
                if (SEND_STATUS == 1)
                    return;
                status = STATUS_TALK;

                setImageButtonBackground();
            } else if (C.Receiver.RELEASE_PTT_KEY.equals(intent.getAction())) {
                if (SEND_STATUS == 1)
                    return;
                status = STATUS_LISTEN;
                setImageButtonBackground();
            }
        }
    };

    private void initData() {
        height = getResources().getDisplayMetrics().heightPixels;
        mNumberOfCameras = Camera.getNumberOfCameras();
        saveFrame = Compat.readIni("DEBUG", "SAVEFRAME");
        callID = getIntent().getIntExtra(CallConstants.CALL_ID, 0);
        caller = getIntent().getStringExtra(CallConstants.CALLER);
        callee = getIntent().getStringExtra(CallConstants.CALLEE);
        calleeName = getIntent().getStringExtra(CallConstants.CALLEE_NAME);
        fromType = getIntent().getIntExtra(CallConstants.fromType, -1);
        mediaAttribute = getIntent().getParcelableExtra(CallConstants.MediaAttr);
        tvGroupNumber.setText(callee);
        surfaceHolder_remote = surfaceViewRemote.getHolder();
        mRemoteSurface = surfaceHolder_remote.getSurface();
        surfaceViewLocal.bringToFront();
        setFullScean.bringToFront();
        surfaceHolder = surfaceViewLocal.getHolder();
        externalCamera();
    }

    //确定打开的摄像头
    private void externalCamera() {
        AudioManager localAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        boolean wiredHeadsetOn = localAudioManager.isWiredHeadsetOn();
        mNumberOfCameras = Camera.getNumberOfCameras();
        if (wiredHeadsetOn && mNumberOfCameras > 2) {
            mCameraNum = 2;
        } else {
            String camerChoseNum = Compat.readIni("RTP_VIDEO_0", "CAMERA");
            if (camerChoseNum != null && !camerChoseNum.equals("")) {
                mCameraNum = Integer.parseInt(camerChoseNum);
            }
        }
        CSMediaCtrl.cameraId = mCameraNum;
//        if (CSMediaCtrl.cameraId == 2) {
//            CSMediaCtrl.rotateInt = 
//        }


    }

    private void initStatus() {
        if (fromType == 100) {
            surfaceViewLocal.setLayoutParams(new RelativeLayout.LayoutParams(1, 1));
        }
        meetingUiLogo.setImageDrawable(getResources().getDrawable(R.mipmap.new_ui_logo));
    }

    @Override
    public void onGetData(String data, int type) {
        super.onGetData(data, type);
        if (type == IDTMsgType.CALL_IN) {
            CallInEntity callInEntity = new Gson().fromJson(data, CallInEntity.class);
            meetingVideoId = callInEntity.getId();
            meetingVideoContent = callInEntity.getpExtInfo();
            surfaceViewLocal.bringToFront();
            surfaceViewLocal.setZOrderMediaOverlay(true);
            surfaceViewLocal.setLayoutParams(new RelativeLayout.LayoutParams(surfaceHeight, surfaceHeight));
//            isSendMeetingVideo = true;
            CSVideo.getInstence().changeCameraId(0);
//            CSVideo.getInstence().addCideoCallId(isSendMeetingVideo,meetingVideoId);
            CSMediaCtrl.isTwoCallId = true;
            CSMediaCtrl.callId2 = meetingVideoId;
            return;
        } else if (type == IDTMsgType.CALL_PEER_ANSWER) {
            CallAnswerEntity callAnswerEntity = new Gson().fromJson(data, CallAnswerEntity.class);
            onCallAnswer(callAnswerEntity);
        } else if (type == IDTMsgType.MESIA_STREAN_STATISTICS) {
            displayStream(data);
        } else if (type == IDTMsgType.VIDEO_CODEC_SETTING) {
            videoCodecChange(data);
        } else if (type == IDTMsgType.GROUP_CALL_SPEAK_TIP) {
            CallTalkingEntity callTalkingEntity = new Gson().fromJson(data, CallTalkingEntity.class);
            tvTalkingUser.setVisibility(View.VISIBLE);
            String phone = callTalkingEntity.getSpeakNum();
            if (phone == null || "".equals(phone)) {
                tvTalkingUser.setText("主讲人员：空闲");
            } else {
                tvTalkingUser.setText("主讲人员：" + phone);
            }
        } else if (type == IDTMsgType.CALL_RELEASE) {
            CallReleaseEntity callReleaseEntity = new Gson().fromJson(data, CallReleaseEntity.class);
            long pUsrCtx = callReleaseEntity.getpUsrCtx();
//            int uiCause = callReleaseEntity.getUiCause();
//            int id = callReleaseEntity.getId();
            if (pUsrCtx == 6) {
//                isSendMeetingVideo = false;
                CSMediaCtrl.isTwoCallId = false;
                CSMediaCtrl.callId2 = -1;
                return;
            }
            if (pUsrCtx == IMType.IM_TYPE_MEETING) {
                callRel();
            }
            Log.e("xiezhixian", "会议退出");
        }
    }

    private void videoCodecChange(String data) {
        VideoCodecEntity videoCodecEntity = new Gson().fromJson(data, VideoCodecEntity.class);
        int ucSend = videoCodecEntity.getUcSend();
        int ucRecv = videoCodecEntity.getUcRecv();
        long pUsrCtx = videoCodecEntity.getpUsrCtx();
        if (pUsrCtx == 6) {
            return;
        }
        if (ucSend == 0) {
            // 不发送
            SEND_STATUS = 0;
            turnCamera.setVisibility(View.GONE);
        } else if (ucSend == 1) {
            // 发送
            SEND_STATUS = 1;
            turnCamera.setVisibility(View.VISIBLE);
        }
        if (ucRecv == 1 && ucSend == 0) {
            surfaceViewLocal.setLayoutParams(new RelativeLayout.LayoutParams(1, 1));
            surfaceViewRemote.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            setFullScean.bringToFront();
            CSVideo.getInstence().displayCamera();
        } else if (ucSend == 1) {
            CSVideo.getInstence().displayCamera();
        }

        if (ucRecv == 0 && ucSend == 1) {
            surfaceViewLocal.bringToFront();
            setFullScean.bringToFront();
            surfaceViewLocal.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            surfaceViewRemote.setLayoutParams(new RelativeLayout.LayoutParams(1, 1));

        }
        if (ucRecv == 1 && ucSend == 1) {
            surfaceViewLocal.bringToFront();
            setFullScean.bringToFront();
            surfaceViewLocal.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            surfaceViewRemote.setLayoutParams(new RelativeLayout.LayoutParams(1, 1));
        }
    }

    private void onCallAnswer(CallAnswerEntity callAnswerEntity) {
        if (callAnswerEntity.getpUsrCtx() == 5) {
            status = STATUS_LISTEN;
            startChronometer();
        }
    }

    private void displayStream(String data) {
        MediaStreamEntity mediaStreamEntity = new Gson().fromJson(data, MediaStreamEntity.class);
        String callInfo = Compat.readIni("SYSTEM", "isOpenAudio", "0");
        long pUsrCtx = mediaStreamEntity.getpUsrCtx();
        int ucType = mediaStreamEntity.getUcType();
        int uiRxBytes = mediaStreamEntity.getUiRxBytes();
        int uiRxUsrBytes = mediaStreamEntity.getUiRxUsrBytes();
        int uiRxCount = mediaStreamEntity.getUiRxCount();
        int uiRxUserCount = mediaStreamEntity.getUiRxUserCount();
        int uiTxBytes = mediaStreamEntity.getUiTxBytes();
        int uiTxUsrBytes = mediaStreamEntity.getUiTxUsrBytes();
        int uiTxCount = mediaStreamEntity.getUiTxCount();
        int uiTxUserCount = mediaStreamEntity.getUiTxUserCount();
        if ("1".equals(callInfo)) {
            if (1 == ucType) {// 语音
                tiaoshi3.setText("接收:" + uiRxBytes + "字节,用户" + uiRxUsrBytes + "字节," + uiRxCount + "次,用户" + uiRxUserCount + "次\r\n" + "发送:" + uiTxBytes
                        + "字节,用户" + uiTxUsrBytes + "字节," + uiTxCount + "次,用户" + uiTxUserCount + "次\r\n");
                IdsLog.d("xiezhixian", "语音的打印");
            } else if (2 == ucType) {
                tiaoshi4.setText("接收:" + uiRxBytes + "字节,用户" + uiRxUsrBytes + "字节," + uiRxCount + "次,用户" + uiRxUserCount + "次\r\n" + "发送:" + uiTxBytes
                        + "字节,用户" + uiTxUsrBytes + "字节," + uiTxCount + "次,用户" + uiTxUserCount + "次\r\n");
            }

        }
        stream.setVisibility(View.VISIBLE);
        if (mediaStreamEntity.getpUsrCtx() != 0 && 2 == ucType) {
            if (uiRxUsrBytes != -1 && uiTxUsrBytes != -1) {
                if (pUsrCtx == 6) {
                    sendStream.setText(uiTxUserCount + "/" + uiTxUsrBytes / 1000 * 8);
                } else {
                    if (uiTxUserCount != 0) {
                        sendStream.setText(uiTxUserCount + "/" + uiTxUsrBytes / 1000 * 8);
                    }
                    stream.setText(uiRxUserCount + "/" + uiRxUsrBytes / 1000 * 8);
                }


            }
        }

    }

    private void startChronometer() {
        chronometerCallTime.setVisibility(View.VISIBLE);
        handler.post(timeRun);
    }

    private Runnable timeRun = new Runnable() {
        @Override
        public void run() {

            time++;
            chronometerCallTime.setVisibility(View.VISIBLE);
            chronometerCallTime.setText(timeFormat((int) time));
            handler.postDelayed(timeRun, 1000);

        }
    };

    @OnClick({R.id.full_screen_canel, R.id.set_full_scean, R.id.landscape_hangup, R.id.close_speaker, R.id.btn_speak, R.id.btn_hangup_conned, R.id.turnCamera, R.id.closeAudio, R.id.saveAudio})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.full_screen_canel:
                callRel();
                break;
            case R.id.set_full_scean:
                setSurfaceFull();
                break;
            case R.id.landscape_hangup:
                callRel();
                break;
            case R.id.btn_speak:
                if (status == STATUS_LISTEN) {
                    status = STATUS_TALK;
                    setImageButtonBackground();
                } else if (status == STATUS_TALK) {
                    status = STATUS_LISTEN;
                    setImageButtonBackground();
                }
                break;
            case R.id.btn_hangup_conned:
                callRel();
                break;
            case R.id.turnCamera:
                changeCamera();
                break;
            case R.id.close_speaker:
                if (isCloseMaiKe) {
                    IdsLog.d("xiezhixian11", "关闭麦克");
                    isCloseMaiKe = false;
                    closeSpeaker.setBackgroundResource(R.mipmap.close_maike);
                    CSAudio.getInstance().micSwitch(false);
                } else {
                    IdsLog.d("xiezhixian11", "打开麦克");
                    isCloseMaiKe = true;
                    closeSpeaker.setBackgroundResource(R.mipmap.open_maike);
                    CSAudio.getInstance().micSwitch(true);
                }
                break;
            case R.id.closeAudio:
                if (isCloseAudio) {
                    IdsLog.d("xiezhixian11", "关闭声音");
                    isCloseAudio = false;
                    closeAudio.setBackgroundResource(R.mipmap.close_laba_press);
                    CSAudio.getInstance().speakerSwitch(false);
                } else {
                    IdsLog.d("xiezhixian11", "打开声音");
                    isCloseAudio = true;
                    closeAudio.setBackgroundResource(R.mipmap.open_laba);
                    CSAudio.getInstance().speakerSwitch(true);
                }
                break;

        }
    }

    private void setSurfaceFull() {
        isFullScreen = !isFullScreen;
        if (isFullScreen) {
            videoTitleLayout.setVisibility(View.GONE);
            newUiAudioCallToplayout.setVisibility(View.GONE);
            newUiAudioCallBottomlayout.setVisibility(View.GONE);
        } else {
            videoTitleLayout.setVisibility(View.VISIBLE);
            newUiAudioCallToplayout.setVisibility(View.VISIBLE);
            newUiAudioCallBottomlayout.setVisibility(View.VISIBLE);
        }
    }

    private void changeCamera() {
        if (mNumberOfCameras == 1) {
            return;
        }
        if (mNumberOfCameras == 2) {
            if (mCameraNum == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mCameraNum = Camera.CameraInfo.CAMERA_FACING_BACK;
                CSVideo.getInstence().changeCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
            } else if (mCameraNum == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCameraNum = Camera.CameraInfo.CAMERA_FACING_FRONT;
                CSVideo.getInstence().changeCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }
        } else {
            if (mCameraNum == Camera.CameraInfo.CAMERA_FACING_FRONT) {//前置
                mCameraNum = 2;
                CSVideo.getInstence().changeCameraId(2);
            } else if (mCameraNum == 2) {//外置
                mCameraNum = Camera.CameraInfo.CAMERA_FACING_BACK;
                CSVideo.getInstence().changeCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
            } else if (mCameraNum == Camera.CameraInfo.CAMERA_FACING_BACK) {//后置
                mCameraNum = Camera.CameraInfo.CAMERA_FACING_FRONT;
                CSVideo.getInstence().changeCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }
        }
    }


    private int isScreenChange() {
        Configuration mConfiguration = this.getResources().getConfiguration();
        int ori = mConfiguration.orientation; // 获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            return 1;
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            // 竖屏
            return 0;
        }
        return 0;
    }

    // 按下和放松的时候
    public void setImageButtonBackground() {
        switch (status) {
            case STATUS_TALK:
                btnSpeak.setBackgroundResource(R.mipmap.new_ui_ppt02);
                IDSApiProxyMgr.getCurProxy().CallMicCtrl(callID, true);
                IdsLog.d(TAG, ">>>>>>>>>>>>> 获取话权");
                break;
            case STATUS_LISTEN:
                btnSpeak.setBackgroundResource(R.mipmap.new_ui_ppt01);
                IDSApiProxyMgr.getCurProxy().CallMicCtrl(callID, false);
                IdsLog.d(TAG, ">>>>>>>>>>>>> 释放话权");
                break;
            default:
                break;
        }
    }

//    @Override
//    protected void postEventResult(@NonNull EventMessage message) {
//        super.postEventResult(message);
//        if ("AudienceSilentData".equals(message.getType())) {
//            String msg = message.getMsg();
//            if (msg.equals("1")) {
//                if (silentFlag) {
//                    return;
//                }
//                silentFlag = true;
//                CSAudio.getInstance().micSwitch(false);
//            } else {
//                silentFlag = false;
//                CSAudio.getInstance().micSwitch(true);
//            }
//        }
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (CSMediaCtrl.cameraId != 2) {
            int orientation = setCameraDisplayOrientation(CSMediaCtrl.cameraId);
            CSVideo.getInstence().changeCameraRotate(orientation);
        } else {
            CSVideo.getInstence().changeCameraRotate(CSMediaCtrl.rotateInt);
        }
    }

    // 设置显示方向
    public int setCameraDisplayOrientation(int cameraId) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        try {
            Camera.getCameraInfo(cameraId, cameraInfo);
        } catch (Exception e) {
            String message = e.getMessage();
            Camera.getCameraInfo(0, cameraInfo);
        }
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (cameraInfo.orientation - degrees + 360) % 360;
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        callRel();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                CSVideo.getInstence().changeFlashState(false);
            }
        }, 500);
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                CSVideo.getInstence().initVideoDecod();
            }
        }, 500);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        CSVideo.getInstence().stopDecoding();
        IDSApiProxyMgr.getCurProxy().SetSurface(null);
    }

    private void callRel() {
        CSVideo.getInstence().videoHangUp(callID, 0, IMType.IM_TYPE_MEETING, mediaAttribute);
        setResult(RESULT_OK);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        CSAudio.setGroupCallNum("");
        CallHelper.CURRENT_GROUP_CALL_STATE = getResources().getString(R.string.now_take_user);
        MyApplication.Companion.setJoinMetting(false);
        handler.removeCallbacks(timeRun);
    }

    public static String timeFormat(int timeMs) {
        int totalSeconds = timeMs;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder sb = new StringBuilder();
        @SuppressWarnings("resource")
        Formatter formatter = new Formatter(sb, Locale.getDefault());
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
}
