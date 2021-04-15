package com.sunny.zhrtc.mian;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.ids.idtma.biz.core.proxy.IDSApiProxyMgr;
import com.ids.idtma.jni.aidl.CallAnswerEntity;
import com.ids.idtma.jni.aidl.CallOutEntity;
import com.ids.idtma.jni.aidl.CallReleaseEntity;
import com.ids.idtma.jni.aidl.MediaAttribute;
import com.ids.idtma.jni.aidl.MediaStreamEntity;
import com.ids.idtma.jni.aidl.ReceiveVideoDataEntity;
import com.ids.idtma.media.CSAudio;
import com.ids.idtma.media.CSMediaCtrl;
import com.ids.idtma.media.CSVideo;
import com.ids.idtma.media.MediaState;
import com.ids.idtma.util.Compat;
import com.ids.idtma.util.IdsLog;
import com.ids.idtma.util.constants.IDTMsgType;
import com.ids.idtma.util.constants.IMType;
import com.sunny.zhrtc.MyApplication;
import com.sunny.zhrtc.R;
import com.sunny.zhrtc.base.BaseActivity;
import com.sunny.zhrtc.constants.C;
import com.sunny.zhrtc.constants.UiCauseConstants;
import com.sunny.zhrtc.util.RingtoneUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoCallActivity extends BaseActivity {
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.my_num)
    TextView myNum;
    @BindView(R.id.video_title_layout)
    RelativeLayout pageTitleLayout;
    @BindView(R.id.video_call_type)
    TextView videoCallType;
    @BindView(R.id.tv_call_number)
    TextView tvCallNumber;
    @BindView(R.id.tv_calling_hint)
    TextView tvCallingHint;
    @BindView(R.id.tiaoshi3)
    TextView tiaoshi3;
    @BindView(R.id.tiaoshi4)
    TextView tiaoshi4;
    @BindView(R.id.chronometer_call_time)
    TextView chronometerCallTime;
    @BindView(R.id.new_ui_audio_call_toplayout)
    RelativeLayout newUiAudioCallToplayout;
    @BindView(R.id.surfaceView_remote)
    SurfaceView surfaceViewRemote;
    @BindView(R.id.surfaceView_local)
    SurfaceView surfaceViewLocal;
    @BindView(R.id.set_full_screen)
    ImageButton setFullScreen;
    @BindView(R.id.landscape_hangup)
    ImageButton landscapeHangup;
    @BindView(R.id.full_screen_canel)
    ImageButton fullScreenCanel;
    @BindView(R.id.rl_video_answer)
    RelativeLayout rlVideoAnswer;
    @BindView(R.id.fasong_stream)
    TextView fasongStream;
    @BindView(R.id.jieshou_stream)
    TextView jieshouStream;
    @BindView(R.id.stream)
    TextView stream;
    @BindView(R.id.stream2)
    TextView stream2;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.btn_hangup_conned)
    ImageButton btnHangup;
    @BindView(R.id.btn_answer)
    ImageButton llAnswer;
    @BindView(R.id.btn_hangup)
    ImageButton llHangup;
    @BindView(R.id.close_speaker)
    ImageButton closeSpeaker;
    @BindView(R.id.turnCamera)
    ImageView turnCamera;
    @BindView(R.id.closeAudio)
    ImageButton closeAudio;
    @BindView(R.id.textView1)
    TextView textView1;
    @BindView(R.id.flashImage)
    ImageButton flashImage;
    @BindView(R.id.rotateCamera)
    ImageButton rotateCamera;
    @BindView(R.id.screenshotButton)
    ImageButton screenshotButton;
    @BindView(R.id.video_ps)
    TextView videoPs;
    @BindView(R.id.video_ps_Rev)
    TextView videoPsRev;
    @BindView(R.id.new_ui_audio_call_bottomlayout)
    RelativeLayout newUiAudioCallBottomlayout;

    private static String TAG = "VideoCallActivity";
    private MediaAttribute mMediaAttribute;
    private int mCallId;
    private String mCallToNumber;
    private String mCallToName;
    private String mCallerNumber;
    private int mVideoContext;
    private int mStatus;//呼叫状态
    private int mCameraNum;//表示选择的摄像头id
    private int mFrameRate;//帧率
    private int mVideoRecv;
    private int mVideoSend;
    private int mAudioRecv;
    private int mAudioSend;
    private String mSaveFrame;//是否存媒体帧
    private int videoForwarding;//1表示为视频转发
    private String mPeerName;//主叫人名称
    private boolean isFullScreen = false;// 是否全屏
    private boolean isCloseAudio = false; // 是否禁音
    private boolean isCloseMaiKe = true; // 是否关闭麦克
    private boolean isOpenFlash = false; // 是否打开闪关灯
    private long time = 0;
    private Handler mHandler = new Handler();
    private int mBitrate;
    private int rotateCameraInt = 90;
    private int mNumberOfCameras;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        ButterKnife.bind(this);
        initData();
        initView();
        initVideo();

        CSVideo.getInstence().initCameraData(surfaceViewLocal, surfaceViewRemote);
    }

    private void initVideo() {
        mVideoRecv = mMediaAttribute.getUcVideoRecv();
        mVideoSend = mMediaAttribute.getUcVideoSend();
        mAudioRecv = mMediaAttribute.getUcAudioRecv();
        mAudioSend = mMediaAttribute.getUcAudioSend();
        if (mAudioRecv == 1 && mStatus != CallHelper.FLAG_INCOMING) {
            closeAudio.setVisibility(View.VISIBLE);
            closeAudio.setBackgroundResource(R.mipmap.open_laba);
        }
        if (mAudioSend == 1 && mStatus != CallHelper.FLAG_INCOMING) {
            closeSpeaker.setVisibility(View.VISIBLE);
            closeSpeaker.setBackgroundResource(R.mipmap.open_maike);
        }
        if (mVideoContext == IMType.IM_TYPE_VIDEO_CALL && mStatus != CallHelper.FLAG_INCOMING) {
            turnCamera.setVisibility(View.VISIBLE);
            flashImage.setVisibility(View.VISIBLE);
            rotateCamera.setVisibility(View.VISIBLE);

        } else if (mVideoContext == IMType.IM_TYPE_VIDEO_DOWN) {
            turnCamera.setVisibility(View.GONE);
            flashImage.setVisibility(View.GONE);
            rotateCamera.setVisibility(View.GONE);
            setFullScreen.setVisibility(View.VISIBLE);
            surfaceViewLocal.setVisibility(View.GONE);
        } else if (mVideoContext == IMType.IM_TYPE_VIDEO_UP) {
            if (mStatus != CallHelper.FLAG_INCOMING) {
                turnCamera.setVisibility(View.VISIBLE);
                flashImage.setVisibility(View.VISIBLE);
                rotateCamera.setVisibility(View.VISIBLE);
            }
            surfaceViewRemote.setVisibility(View.GONE);
            setFullScreen(surfaceViewLocal);
        }
        isCloseAudio = true;
        isCloseMaiKe = false;
        switch (mStatus) {
            case CallHelper.FLAG_INCOMING:// 来电进入

                if (videoForwarding == 1) {
                    tvCallNumber.setText(mCallToName);
                } else {
                    tvCallNumber.setText(mPeerName);
                }
                RingtoneUtils.playRingtone(this);// 播放震铃
                setCalleeMode();
                break;
            case CallHelper.FLAG_CALLING:
                setCallerMode();
                tvCallingHint.setVisibility(View.VISIBLE);
                // 设置对方电话号码
                if (mCallToName != null && !mCallToName.equals("")) {
                    tvCallNumber.setText(mCallToName);
                } else {
                    tvCallNumber.setText("服务器");
                }
                break;
            case CallHelper.CALL_AUTO_ANSWER:

                setCallerMode();
                tvCallNumber.setText(mPeerName);
                surfaceViewLocal.setVisibility(View.VISIBLE);
                surfaceViewLocal.bringToFront();
                mHandler.post(timeRun);
                RingtoneUtils.stopRingtone();
                MediaAttribute pAttr = new MediaAttribute();
                pAttr.setUcAudioRecv(mAudioRecv);
                pAttr.setUcAudioSend(mAudioSend);
                pAttr.setUcVideoRecv(mVideoRecv);
                pAttr.setUcVideoSend(mVideoSend);
                CSVideo.getInstence().callAnswer(mCallId, pAttr);
                // 给so和呼叫方一个应答
                if (pAttr.getUcAudioRecv() == 1 && pAttr.getUcAudioSend() == 1 && pAttr.getUcVideoRecv() == 1 && pAttr.getUcVideoSend() == 1) {
                } else if (pAttr.getUcVideoRecv() == 1 && pAttr.getUcVideoSend() == 0) {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(1, 1);
                    surfaceViewLocal.setLayoutParams(layoutParams);
                } else if (pAttr.getUcVideoRecv() == 0 && pAttr.getUcVideoSend() == 1) {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    surfaceViewLocal.setLayoutParams(layoutParams);
                }
                break;
        }
    }

    /**
     * SurfaceView全屏
     *
     * @param surfaceView
     */
    private void setFullScreen(SurfaceView surfaceView) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) surfaceViewLocal.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        surfaceViewLocal.setLayoutParams(layoutParams);
        surfaceViewLocal.setZOrderMediaOverlay(true);
        RelativeLayout parentView = (RelativeLayout) surfaceViewLocal.getParent();// 解决遮盖问题
        surfaceViewLocal.bringToFront();
        parentView.requestLayout();
        parentView.invalidate();
    }


    private void initView() {
        myNum.setText(MyApplication.Companion.getUserAccount());
        videoPs.setText("S:" + CSMediaCtrl.SEND_PREVIEW_WIDTH + "X" + CSMediaCtrl.SEND_PREVIEW_HEIGHT);
        surfaceViewLocal.setOnTouchListener(new SurfaceViewTouchListener());
        surfaceViewLocal.bringToFront();
        surfaceViewLocal.getHolder().setFormat(PixelFormat.TRANSPARENT);
        surfaceViewLocal.setZOrderOnTop(true);
    }

    private void initData() {
        Intent intent = getIntent();

        // 获取参数
        mStatus = getIntent().getIntExtra(CallHelper.CALL_STATUS, -1);
        mCallId = getIntent().getIntExtra(CallHelper.CALL_ID, -1);
        if (mStatus == CallHelper.FLAG_CALLING) {
            CallOutEntity entity = getIntent().getParcelableExtra(CallHelper.CallEntity);
            mCallId = CSVideo.getInstence().videoCall(entity);
        }
        if (mCallId == MediaState.CAUSE_RESOURCE_UNAVAIL) {
            toast(getString(R.string.resource_not_use));
            finish();
            return;
        }
        mMediaAttribute = intent.getParcelableExtra(CallHelper.MediaAttr);
        mCallToNumber = intent.getStringExtra(CallHelper.CALLEE);
        mCallToName = intent.getStringExtra(CallHelper.CALLEE_NAME);
        mCallerNumber = intent.getStringExtra(CallHelper.CALLER);
        mVideoContext = intent.getIntExtra(CallHelper.CallContext, -1);
        mPeerName = getIntent().getStringExtra(CallHelper.CALLER_NAME);
        videoForwarding = getIntent().getIntExtra("VideoForwarding", -1);
        mSaveFrame = Compat.readIni("DEBUG", "SAVEFRAME");

        externalCamera();

        String bitrate = Compat.readIni("RTP_VIDEO_0", "BITRATE");

        mBitrate = Integer.parseInt(bitrate);

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
        if (mMediaAttribute.getUcVideoRecv() == 1 && mMediaAttribute.getUcVideoSend() == 1) {
            CSMediaCtrl.cameraId = 1;
            mCameraNum = 1;

        }
    }

    private Runnable timeRun = new Runnable() {
        @Override
        public void run() {

            time++;
            chronometerCallTime.setVisibility(View.VISIBLE);
            chronometerCallTime.setText(AudioCallActivity.timeFormat((int) time));
            mHandler.postDelayed(timeRun, 1000);
        }
    };

    /**
     * 设置界面是被叫模式
     */
    private void setCalleeMode() {
        switch (mVideoContext) {

            case IMType.IM_TYPE_VIDEO_DOWN:
                pageTitle.setText(getResources().getString(R.string.video_diaoyong));
                videoCallType.setText(getResources().getString(R.string.video_diaoyong));
                tvCallingHint.setVisibility(View.VISIBLE);
                findViewById(R.id.btn_answer).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_hangup).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_hangup_conned).setVisibility(View.GONE);
                tvCallingHint.setText(getResources().getString(R.string.shenqingVideoCall));
                surfaceViewRemote.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(1, 1);
                surfaceViewLocal.setLayoutParams(layoutParams);
                break;
            case IMType.IM_TYPE_VIDEO_UP:
                if (videoForwarding == 1) {
                    pageTitle.setText(getResources().getString(R.string.video_diaoyong));
                } else {
                    pageTitle.setText(getResources().getString(R.string.video_up));
                }
                videoCallType.setText(getResources().getString(R.string.video_up));
                tvCallingHint.setVisibility(View.VISIBLE);
                findViewById(R.id.btn_answer).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_hangup).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_hangup_conned).setVisibility(View.GONE);
                tvCallingHint.setText(getResources().getString(R.string.shenqingVideoCallUp));
                surfaceViewLocal.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                surfaceViewLocal.setLayoutParams(layoutParams2);
                break;
            case IMType.IM_TYPE_VIDEO_CALL:
                pageTitle.setText(getResources().getString(R.string.video_single_call));
                videoCallType.setText(getResources().getString(R.string.video_single_call));
                tvCallingHint.setVisibility(View.VISIBLE);
                findViewById(R.id.btn_answer).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_hangup).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_hangup_conned).setVisibility(View.GONE);
                tvCallingHint.setText(getResources().getString(R.string.yaoqingVideoCall));
                break;
            default:
                break;
        }
    }

    /**
     * 设置界面是主叫模式
     */
    private void setCallerMode() {
        switch (mVideoContext) {
            case IMType.IM_TYPE_VIDEO_DOWN:
                pageTitle.setText(getResources().getString(R.string.video_diaoyong));
                videoCallType.setText(getResources().getString(R.string.video_diaoyong));
                tvCallingHint.setVisibility(View.INVISIBLE);
                llAnswer.setVisibility(View.GONE);
                llHangup.setVisibility(View.GONE);
                btnHangup.setVisibility(View.VISIBLE);
                tvCallingHint.setText(getResources().getString(R.string.waitForVideoDown));
                surfaceViewRemote.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                surfaceViewRemote.setLayoutParams(layoutParams);
                break;
            case IMType.IM_TYPE_VIDEO_UP:
                if (getIntent().getIntExtra("VideoForwarding", -1) == 1) {
                    pageTitle.setText(getResources().getString(R.string.video_diaoyong));
                } else {
                    pageTitle.setText(getResources().getString(R.string.video_up));
                }
                videoCallType.setText(getResources().getString(R.string.video_up));
                tvCallingHint.setVisibility(View.INVISIBLE);
                llAnswer.setVisibility(View.GONE);
                llHangup.setVisibility(View.GONE);
                btnHangup.setVisibility(View.VISIBLE);
                if (mAudioSend == 1) {
                    closeSpeaker.setVisibility(View.VISIBLE);
                    closeSpeaker.setBackgroundResource(R.mipmap.open_maike);
                }
                turnCamera.setVisibility(View.VISIBLE);
                flashImage.setVisibility(View.VISIBLE);
                rotateCamera.setVisibility(View.VISIBLE);
//                tvCallingHint.setText(getResources().getString(R.string.waitForVideoUp));
                break;
            case IMType.IM_TYPE_VIDEO_CALL:
                pageTitle.setText(getResources().getString(R.string.video_single_call));
                videoCallType.setText(getResources().getString(R.string.video_single_call));
                tvCallingHint.setVisibility(View.INVISIBLE);
                llAnswer.setVisibility(View.GONE);
                llHangup.setVisibility(View.GONE);
                btnHangup.setVisibility(View.VISIBLE);
                turnCamera.setVisibility(View.VISIBLE);
                flashImage.setVisibility(View.VISIBLE);
                rotateCamera.setVisibility(View.VISIBLE);
                closeAudio.setVisibility(View.VISIBLE);
                closeAudio.setBackgroundResource(R.mipmap.open_laba);
                closeSpeaker.setVisibility(View.VISIBLE);
                closeSpeaker.setBackgroundResource(R.mipmap.open_maike);
                tvCallingHint.setText(getResources().getString(R.string.waitForVideoCall));
                break;
            default:
                break;
        }
    }

    private static boolean stop = false;

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "called onStop");
        stop = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "called onResume");
        if (stop)
            if (CSMediaCtrl.cameraId != 2) {
                int orientation = setCameraDisplayOrientation(CSMediaCtrl.cameraId);
                Log.e(TAG, "into stop orientation change recv: " + orientation);

                CSVideo.getInstence().changeCameraRotate(orientation);
            } else {
                CSVideo.getInstence().changeCameraRotate(CSMediaCtrl.rotateInt);
            }
        stop = false;

    }

    @Override
    public void onGetData(String data, int type) {
        super.onGetData(data, type);
        if (type == IDTMsgType.CALL_PEER_ANSWER) {
            CallAnswerEntity callAnswerEntity = new Gson().fromJson(data, CallAnswerEntity.class);
            onCallAnswer(callAnswerEntity);
        } else if (type == IDTMsgType.CALL_RELEASE) {
            CallReleaseEntity callReleaseEntity = new Gson().fromJson(data, CallReleaseEntity.class);
            if (callReleaseEntity.getId() == mCallId) {
                if ((callReleaseEntity.getpUsrCtx() == IMType.IM_TYPE_VIDEO_DOWN || callReleaseEntity.getpUsrCtx() == IMType.IM_TYPE_VIDEO_UP)) {
                    MediaState.setAudioState(false);
                }
                if (CSMediaCtrl.mediaSaveFlag) {
                    String mp4Path = C.FilePath.VIDEO_CALL_PATH + "/" + "test.mp4";
                    CSVideo.getInstence().videoHangUp(callReleaseEntity.getId(), callReleaseEntity.getUiCause(), callReleaseEntity.getpUsrCtx(), mp4Path, mMediaAttribute);
                } else {
                    CSVideo.getInstence().videoHangUp(callReleaseEntity.getId(), callReleaseEntity.getUiCause(), callReleaseEntity.getpUsrCtx(), mMediaAttribute);
                }
                Intent intent = new Intent();
                intent.setAction(C.Receiver.ACTION_CALL_RELEASE);
                sendBroadcast(intent);
                toast(new UiCauseConstants().getDataType(callReleaseEntity.getUiCause(), VideoCallActivity.this));
                finish();
            }
        } else if (type == IDTMsgType.MESIA_STREAN_STATISTICS) {
            displayStream(data);
        } else if (type == IDTMsgType.VIDEO_CODEC_SETTING) {

        } else if (type == IDTMsgType.VIDEO_RECEIVE_DATA) {
            ReceiveVideoDataEntity receiveVideoData = new Gson().fromJson(data, ReceiveVideoDataEntity.class);
            videoPsRev.setText("R:" + receiveVideoData.getWidth() + "x" + receiveVideoData.getHeight());
        }
    }

    private void displayStream(String data) {
        MediaStreamEntity mediaStreamEntity = new Gson().fromJson(data, MediaStreamEntity.class);
        String callInfo = Compat.readIni("SYSTEM", "isOpenAudio", "0");
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
                stream.setText(uiTxUserCount + "/" + uiTxUsrBytes / 1000 * 8);
                stream2.setText(uiRxUserCount + "/" + uiRxUsrBytes / 1000 * 8);
            }
        }

    }

    private void onCallAnswer(CallAnswerEntity callAnswerEntity) {
        long pUsrCtx = callAnswerEntity.getpUsrCtx();
        if (pUsrCtx != 0 && pUsrCtx != 1 && pUsrCtx != -1) {
            setCallerMode();
            if (videoForwarding != 1) {
                mHandler.post(timeRun);
            }
        }
    }


    @OnClick({R.id.set_full_screen, R.id.landscape_hangup, R.id.full_screen_canel, R.id.btn_hangup_conned,
            R.id.btn_answer, R.id.btn_hangup, R.id.close_speaker, R.id.turnCamera, R.id.closeAudio, R.id.flashImage,
            R.id.rotateCamera, R.id.screenshotButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.set_full_screen:
                isFullScreen = !isFullScreen;
                setScreenState(isFullScreen);
                break;
            case R.id.landscape_hangup:
                callRelease(0);

                break;
            case R.id.full_screen_canel:
                callRelease(0);

                break;
            case R.id.btn_hangup_conned:

                callRelease(0);
                break;
            case R.id.btn_hangup:
                callRelease(15);
                break;
            case R.id.btn_answer:
                // 界面显示设置
                setCallerMode();
                mHandler.post(timeRun);
                RingtoneUtils.stopRingtone();
                if (videoForwarding == 1) {
                    mCallId = IDSApiProxyMgr.getCurProxy().CallMakeOut(mCallToName, CallHelper.SRV_TYPE_WATCH_DOWN, mMediaAttribute, IMType.IM_TYPE_VIDEO_DOWN, 0, IMType.NORMAL_JSON);
                    return;
                }
                CSVideo.getInstence().callAnswer(mCallId, mMediaAttribute);
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
            case R.id.turnCamera:
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
            case R.id.flashImage:
                if (isOpenFlash) {
                    //为true点击则关闭闪关灯
                    isOpenFlash = false;
                    flashImage.setBackgroundResource(R.mipmap.flash_off);
                } else {
                    isOpenFlash = true;
                    flashImage.setBackgroundResource(R.mipmap.flash_on);
                }
                CSVideo.getInstence().changeFlashState(isOpenFlash);
                break;
            case R.id.rotateCamera:
                changePreviewDegree();
                break;
        }
    }

    private void setScreenState(boolean isFullScreen) {
        if (isFullScreen) {//全屏
            pageTitleLayout.setVisibility(View.GONE);
            newUiAudioCallToplayout.setVisibility(View.GONE);
            newUiAudioCallBottomlayout.setVisibility(View.GONE);
            fullScreenCanel.setVisibility(View.VISIBLE);
        } else {//起始模样
            pageTitleLayout.setVisibility(View.VISIBLE);
            newUiAudioCallToplayout.setVisibility(View.VISIBLE);
            newUiAudioCallBottomlayout.setVisibility(View.VISIBLE);
            fullScreenCanel.setVisibility(View.GONE);
        }
    }

    private void changePreviewDegree() {
        switch (rotateCameraInt) {
            case 0:
                rotateCameraInt = 90;
                break;
            case 90:
                rotateCameraInt = 180;
                break;
            case 180:
                rotateCameraInt = 270;
                break;
            case 270:
                rotateCameraInt = 0;
                break;
        }
        CSVideo.getInstence().changeCameraRotate(rotateCameraInt);
    }

    @Override
    public void onBackPressed() {
        mHandler.removeCallbacks(timeRun);
        callRelease(0);
        finish();
    }

    //针对按下home的操作
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        CSVideo.getInstence().stopDecoding();
        IDSApiProxyMgr.getCurProxy().SetSurface(null);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //视频界面重新绘画
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                CSVideo.getInstence().changeFlashState(false);
            }
        }, 500);
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                CSVideo.getInstence().initVideoDecod();
            }
        }, 500);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e(TAG, "onConfigurationChanged");
        if (stop) return;

        if (CSMediaCtrl.cameraId != 2) {

            int orientation = setCameraDisplayOrientation(CSMediaCtrl.cameraId);
            Log.e(TAG, "into stop orientation change recv: " + orientation);

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
            e.getMessage();
            Camera.getCameraInfo(0, cameraInfo);
        }
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        Log.e(TAG, "rotation:" + rotation);
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
        Log.e(TAG, "degrees:" + degrees);
        int result;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (cameraInfo.orientation - degrees + 360) % 360;
        }
        Log.e(TAG, "result:" + result);
        return result;
    }

    private float oldDist = 1f;
    private int mCameraZoom;

    class SurfaceViewTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getPointerCount() == 1) {

            } else {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = getFingerSpacing(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float newDist = getFingerSpacing(event);
                        if (newDist > oldDist) {
                            mCameraZoom += 1;
                            if (mCameraNum == 2) {
                                try {
                                    CSVideo.getInstence().changeFocalLength(mCameraZoom);
                                    mCameraNum = 2;
                                } catch (Exception e) {
                                    CSVideo.getInstence().changeFocalLength(0);
                                    mCameraNum = 0;
                                }
                            } else {
                                CSVideo.getInstence().changeFocalLength(mCameraZoom);
                            }
                        } else if (newDist < oldDist) {
                            mCameraZoom -= 1;
                            if (mCameraNum == 2) {
                                try {
                                    CSVideo.getInstence().changeFocalLength(mCameraZoom);
                                    mCameraNum = 2;
                                } catch (Exception e) {
                                    CSVideo.getInstence().changeFocalLength(0);
                                    mCameraNum = 0;
                                }
                            } else {
                                CSVideo.getInstence().changeFocalLength(mCameraZoom);
                            }
                        }
                        oldDist = newDist;
                        break;
                }
            }
            return true;
        }
    }

    private static float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void callRelease(int cause) {
        //己方挂断
        RingtoneUtils.stopRingtone();
        if ((mVideoContext == IMType.IM_TYPE_VIDEO_UP || mVideoContext == IMType.IM_TYPE_VIDEO_DOWN)) {
            MediaState.setAudioState(false);
        }
        if (CSMediaCtrl.mediaSaveFlag) {
            String mp4Path = C.FilePath.VIDEO_CALL_PATH + "test.mp4";
            CSVideo.getInstence().videoHangUp(mCallId, cause, mVideoContext, mp4Path, mMediaAttribute);
        } else {
            CSVideo.getInstence().videoHangUp(mCallId, cause, mVideoContext, mMediaAttribute);

        }
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        CSAudio.getInstance().micSwitch(true);
        CSAudio.getInstance().speakerSwitch(true);
        CSVideo.getInstence().stopDecoding();
        mCameraZoom = 0;
        stop = false;
        mHandler.removeCallbacks(timeRun);
    }
}
