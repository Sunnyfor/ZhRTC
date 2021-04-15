package com.sunny.zhrtc.mian;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ids.idtma.jni.IDTApi;
import com.ids.idtma.jni.aidl.CallAnswerEntity;
import com.ids.idtma.jni.aidl.CallReleaseEntity;
import com.ids.idtma.jni.aidl.CallTalkingEntity;
import com.ids.idtma.jni.aidl.IDTMsgType;
import com.ids.idtma.jni.aidl.MediaAttribute;
import com.ids.idtma.media.CSAudio;
import com.ids.idtma.media.CSMediaCtrl;
import com.ids.idtma.media.MediaState;
import com.ids.idtma.util.Compat;
import com.ids.idtma.util.constants.IMType;
import com.sunny.zhrtc.R;
import com.sunny.zhrtc.base.BaseActivity;
import com.sunny.zhrtc.constants.C;
import com.sunny.zhrtc.constants.CallConstants;
import com.sunny.zhrtc.constants.UiCauseConstants;
import com.sunny.zhrtc.util.RingtoneUtils;
import com.sunny.zhrtc.util.SpeakerPhoneUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AudioHalfSingleCallActivity extends BaseActivity {

    private static final String TAG = "lzn_AudioHalfSingleCall";
    @BindView(R.id.dialNum1)
    Button dialNum1;
    @BindView(R.id.dialNum2)
    Button dialNum2;
    @BindView(R.id.dialNum3)
    Button dialNum3;
    @BindView(R.id.dialNum4)
    Button dialNum4;
    @BindView(R.id.dialNum5)
    Button dialNum5;
    @BindView(R.id.dialNum6)
    Button dialNum6;
    @BindView(R.id.dialNum7)
    Button dialNum7;
    @BindView(R.id.dialNum8)
    Button dialNum8;
    @BindView(R.id.dialNum9)
    Button dialNum9;
    @BindView(R.id.dialx)
    Button dialx;
    @BindView(R.id.dialNum0)
    Button dialNum0;
    @BindView(R.id.dialj)
    Button dialj;
    @BindView(R.id.keyBoardLayout)
    LinearLayout keyBoardLayout;
    @BindView(R.id.attrLayout)
    LinearLayout attrLayout;
    @BindView(R.id.btn_answer)
    ImageButton llAnswer;
    @BindView(R.id.new_ui_video_call_divide0)
    TextView newUiVideoCallDivide0;
    @BindView(R.id.btn_hangup)
    ImageButton llHangup;
    @BindView(R.id.new_ui_video_call_divide1)
    TextView newUiVideoCallDivide1;
    @BindView(R.id.tv_call_name)
    TextView tvCallName;
    @BindView(R.id.tv_call_number)
    TextView tvCallNumber;
    @BindView(R.id.btn_hangup_conned)
    ImageButton btnHangup;
    @BindView(R.id.audio_maike)
    ImageView audioMaike;
    @BindView(R.id.audio_mic_control)
    ImageView audioMicControl;
    @BindView(R.id.audio_laba)
    ImageView audioLaba;
    @BindView(R.id.keyBoardd)
    ImageView keyBoardd;
    @BindView(R.id.audio_moude)
    ImageView audioMoude;
    @BindView(R.id.new_ui_audio_call_bottomlayout)
    RelativeLayout newUiAudioCallBottomlayout;
    @BindView(R.id.attr)
    ImageView attr;

    private AudioManager mAudioManager;
    private int mAudioCallID;
    private String mCaller; // 主叫号码
    private String mCallee;//num对方号码
    private String mCallerName;
    private String mCalleeName;
    private MediaAttribute mediaAttribute;
    private int mStatus;
    private int notificationFlag;
    private long mSaveToDbId;
    private final static int STATUS_TALK = 1;
    private final static int STATUS_LISTEN = 2;
    private int MIC_STATUE;
    private boolean isCloseAudio;
    private boolean isCloseMaiKe;
    private boolean isDisplayBoard;
    private boolean isOpenPhoneOn;
    private String scallGain;
    private Handler mHandler = new Handler();
    public int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_half_single_call);
        ButterKnife.bind(this);
        initData();
        initCall();
        initView();
        initBroadcast();
    }

    private void initBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(C.Receiver.PRESS_PTT_KEY);
        filter.addAction(C.Receiver.RELEASE_PTT_KEY);
        registerReceiver(broadcastReceiver, filter);
    }


    private Runnable timeRun = new Runnable() {

        @Override
        public void run() {
            time++;
            mHandler.postDelayed(timeRun, 1000);
        }
    };

    private void initData() {
        new SpeakerPhoneUtils(this).OpenSpeaker();
        // 获取参数
        mStatus = getIntent().getIntExtra(CallConstants.CALL_STATUS, -1);
        mAudioCallID = getIntent().getIntExtra(CallConstants.CALL_ID, -1);
        // 主叫号码
        mCaller = getIntent().getStringExtra(CallConstants.CALLER);
        // call_to_persion num对方号码
        mCallee = getIntent().getStringExtra(CallConstants.CALLEE);
        if (mStatus == CallHelper.FLAG_CALLING) {
            mAudioCallID = CSAudio.getInstance().audioHalfCall(mCallee);
        }
        if (mAudioCallID == MediaState.CAUSE_RESOURCE_UNAVAIL) {
            toast(getString(R.string.resource_not_use));
            finish();
        }
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        isOpenPhoneOn = mAudioManager.isSpeakerphoneOn();

//        SharedPreferencesUtil.setIntPreferences(this, "aes_audio_callID", mAudioCallID);

        mCallerName = getIntent().getStringExtra(CallConstants.CALLER_NAME);
        mCalleeName = getIntent().getStringExtra(CallConstants.CALLEE_NAME);
        if (mCalleeName == null) {
            mCalleeName = mCallee;
        }
        mediaAttribute = getIntent().getParcelableExtra(CallConstants.MediaAttr);


        notificationFlag = getIntent().getIntExtra(CallConstants.NOTIFICATION_INTENT, -1);
        scallGain = Compat.readIni("AUDIO", "SCALLGAIN");


        // 获取参数
//        mStatus = getIntent().getIntExtra(CallHelper.CALL_STATUS, -1);
//        mAudioCallID = getIntent().getIntExtra(CallHelper.CALL_ID, -1);
//        if (mStatus == CallHelper.FLAG_CALLING) {
//            CallOutEntity entity = getIntent().getParcelableExtra(CallHelper.CallEntity);
//            mAudioCallID = CSAudio.getInstance().audioHalfCall(entity);
//        }
//        if (mAudioCallID == MediaState.CAUSE_RESOURCE_UNAVAIL) {
//            toast(getString(R.string.resource_not_use));
//            finish();
//            return;
//        }
//        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//
//        // 主叫号码
//        mCaller = getIntent().getStringExtra(CallHelper.CALLER);
//        // 对方号码
//        mCallee = getIntent().getStringExtra(CallHelper.CALLEE);
//        mCallerName = getIntent().getStringExtra(CallHelper.CALLER_NAME);
//        mCalleeName = getIntent().getStringExtra(CallHelper.CALLEE_NAME);
//        if (mCalleeName == null) {
//            mCalleeName = mCallee;
//        }
//        mediaAttribute = getIntent().getParcelableExtra(CallHelper.MediaAttr);
//
//        myNum.setText(MyApplication.Companion.getUserAccount());
//
//        notificationFlag = getIntent().getIntExtra(CallHelper.NOTIFICATION_INTENT, -1);


    }

    private void initCall() {

        switch (mStatus) {
            case CallHelper.FLAG_INCOMING:  // 来电进入
                tvCallName.setText(mCaller + getResources().getString(R.string.nowCalling));
                setCalleeMode();
                if (notificationFlag == -1) {
                    RingtoneUtils.playRingtone(this);
//                    saveDbCall(1);
                }
                break;
            case CallHelper.FLAG_CALLING: // 呼叫出去
                // 对界面视图进行操作
                setCallerMode();
                RingtoneUtils.playSound(this, "ringtone.mp3");
                tvCallName.setText(getResources().getString(R.string.call) + mCalleeName + "-" + mCallee);
//                if (notificationFlag == -1) {
//                    saveDbCall(2);
//                }
                break;
            // 只有在有notification的时候才会调用这个地方？
            case CallHelper.FLAG_ANSWER:
                setCallerMode();
                tvCallName.setText(mCallerName + "-" + mCaller);
                if (notificationFlag != -1) {
                    tvCallName.setText(mCallerName + "-" + mCaller);
                }
                break;

            case CallHelper.CALL_AUTO_ANSWER:
                mStatus = CallHelper.FLAG_ANSWER;
                RingtoneUtils.stopRingtone();
                setCallerMode();
                audioMicControl.setVisibility(View.VISIBLE);
//                saveDbCall(1);
                tvCallName.setText(mCaller);
                CSAudio.getInstance().callAnswer(mAudioCallID, mediaAttribute, IMType.IM_TYPE_GROUP_CALL);
                break;
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        audioMicControl.setOnTouchListener(new MicTouchListener());
        if (mAudioManager.isSpeakerphoneOn()) {
            Log.d(TAG, "扬声器是打开的 onCreate == >" + mAudioManager.isSpeakerphoneOn());
            audioMoude.setImageResource(R.mipmap.new_ui_notice_button);
        } else {
            Log.d(TAG, "扬声器是关闭的 onCreate  == >" + mAudioManager.isSpeakerphoneOn());

            audioMoude.setImageResource(R.mipmap.new_ui_notice_button_press);
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (C.Receiver.PRESS_PTT_KEY.equals(intent.getAction())) {
                MIC_STATUE = STATUS_TALK;
                audioMicControl.setBackground(getResources().getDrawable(R.mipmap.audio_call_press));
                CSAudio.getInstance().groupMicControl(true);
                mAudioManager.setMicrophoneMute(false);
            } else if (C.Receiver.RELEASE_PTT_KEY.equals(intent.getAction())) {
                MIC_STATUE = STATUS_LISTEN;
                audioMicControl.setBackground(getResources().getDrawable(R.mipmap.audio_call_nomal));
                CSAudio.getInstance().groupMicControl(false);
                mAudioManager.setMicrophoneMute(true);
            }
        }
    };

    /**
     * 设置界面是主叫模式
     */
    private void setCallerMode() {
//        tvCallingHint.setVisibility(View.INVISIBLE);
        llAnswer.setVisibility(View.GONE);
        newUiVideoCallDivide0.setVisibility(View.GONE);
        llHangup.setVisibility(View.GONE);
        newUiVideoCallDivide1.setVisibility(View.GONE);
        btnHangup.setVisibility(View.VISIBLE);
        isCloseAudio = false;
        isCloseMaiKe = true;
//        tvCallingHint.setText(getResources().getString(R.string.nowAudioCalling));
    }

    /**
     * 设置界面是被叫模式
     */
    private void setCalleeMode() {
//        tvCallingHint.setVisibility(View.VISIBLE);
        llAnswer.setVisibility(View.VISIBLE);
        newUiVideoCallDivide0.setVisibility(View.VISIBLE);
        llHangup.setVisibility(View.VISIBLE);
        newUiVideoCallDivide1.setVisibility(View.GONE);
        btnHangup.setVisibility(View.GONE);
        isCloseAudio = false;
        isCloseMaiKe = false;
//        tvCallingHint.setText(getResources().getString(R.string.Invite_you_voice_call));
    }

    @Override
    public void onGetData(String data, int type) {
        super.onGetData(data, type);
        if (type == IDTMsgType.CALL_PEER_ANSWER) {
            CallAnswerEntity callAnswerEntity = new Gson().fromJson(data, CallAnswerEntity.class);
            onCallAnswer(callAnswerEntity);
        } else if (type == IDTMsgType.CALL_RELEASE) {//对方释放
            CallReleaseEntity callReleaseEntity = new Gson().fromJson(data, CallReleaseEntity.class);
            if (callReleaseEntity.getId() == mAudioCallID) {
                mHandler.removeCallbacks(timeRun);
                MediaState.setCurrentState(0);
                CSAudio.getInstance().audioHandUp(callReleaseEntity.getId(), callReleaseEntity.getpUsrCtx());
                RingtoneUtils.stopRingtone();
                toast(new UiCauseConstants().getDataType(callReleaseEntity.getUiCause(), this));
                finish();
            }
        } else if (type == IDTMsgType.MESIA_STREAN_STATISTICS) {
//            displayStream(data);
        } else if (type == IDTMsgType.GROUP_CALL_SPEAK_TIP) {
            CallTalkingEntity callTalkingEntity = new Gson().fromJson(data, CallTalkingEntity.class);
            String speakName = callTalkingEntity.getSpeakName();
            String speakNum = callTalkingEntity.getSpeakNum();
            if (speakNum == null || "".equals(speakNum)) {
                tvCallNumber.setText(getResources().getString(R.string.now_take_user));
            } else {
                tvCallNumber.setText(getResources().getString(R.string.now_take_user22) + speakName);
            }
        }

    }

    @Override
    public void onBackPressed() {
//        Dialogs.commonDialog(getResources().getString(R.string.kindle_reminder), getResources().getString(R.string.you_sure_quit), this, new Dialogs.OnSureClickListener() {
//            @Override
//            public void onClick() {
//                RingtoneUtils.stopRingtone();
        if (mStatus == CallHelper.FLAG_ANSWER) {
            callRelease(UiCauseConstants.CAUSE_ZERO);
        } else {
            callRelease(UiCauseConstants.CAUSE_CALL_REJ_BY_USER);
        }
    }

    private void onCallAnswer(CallAnswerEntity callAnswerEntity) {
        RingtoneUtils.stopRingtone();
        audioMicControl.setVisibility(View.VISIBLE);
        CSAudio.getInstance().groupMicControl(false);
        tvCallName.setText(mCallee);
        setCallerMode();
        mStatus = CallHelper.FLAG_ANSWER;
        mHandler.post(timeRun);
    }

    @SuppressLint("WrongConstant")
    @OnClick({R.id.dialNum1, R.id.dialNum2, R.id.dialNum3, R.id.dialNum4, R.id.dialNum5, R.id.dialNum6, R.id.dialNum7, R.id.dialNum8, R.id.dialNum9, R.id.dialx, R.id.dialNum0, R.id.dialj, R.id.btn_answer, R.id.btn_hangup, R.id.btn_hangup_conned, R.id.audio_maike, R.id.audio_laba, R.id.keyBoardd, R.id.audio_moude})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialNum0:
                IDTApi.IDT_CallSendNum(mAudioCallID, "0");
                break;
            case R.id.dialNum1:
                IDTApi.IDT_CallSendNum(mAudioCallID, "1");
                break;
            case R.id.dialNum2:
                IDTApi.IDT_CallSendNum(mAudioCallID, "2");
                break;
            case R.id.dialNum3:
                IDTApi.IDT_CallSendNum(mAudioCallID, "3");
                break;
            case R.id.dialNum4:
                IDTApi.IDT_CallSendNum(mAudioCallID, "4");
                break;
            case R.id.dialNum5:
                IDTApi.IDT_CallSendNum(mAudioCallID, "5");
                break;
            case R.id.dialNum6:
                IDTApi.IDT_CallSendNum(mAudioCallID, "6");
                break;
            case R.id.dialNum7:
                IDTApi.IDT_CallSendNum(mAudioCallID, "7");
                break;
            case R.id.dialNum8:
                IDTApi.IDT_CallSendNum(mAudioCallID, "8");
                break;
            case R.id.dialNum9:
                IDTApi.IDT_CallSendNum(mAudioCallID, "9");
                break;
            case R.id.dialx:
                IDTApi.IDT_CallSendNum(mAudioCallID, "*");
                break;
            case R.id.dialj:
                IDTApi.IDT_CallSendNum(mAudioCallID, "#");
                break;
            case R.id.btn_answer:
                tvCallName.setText(mCaller);
                mStatus = CallHelper.FLAG_ANSWER;
                RingtoneUtils.stopRingtone();
                setCallerMode();
                mHandler.post(timeRun);
                audioMicControl.setVisibility(View.VISIBLE);
                mAudioManager.setMicrophoneMute(true);
//                mHandler.post(timeRun);
                isCloseAudio = true;
                CSAudio.getInstance().callAnswer(mAudioCallID, mediaAttribute, IMType.IM_TYPE_AUDIO_CALL);
                break;
            case R.id.btn_hangup:
                RingtoneUtils.stopRingtone();
                if (mStatus == CallHelper.FLAG_ANSWER) {
                    callRelease(UiCauseConstants.CAUSE_ZERO);
                } else {
                    callRelease(UiCauseConstants.CAUSE_CALL_REJ_BY_USER);
                }
                break;
            case R.id.btn_hangup_conned:
                RingtoneUtils.stopRingtone();
                callRelease(UiCauseConstants.CAUSE_ZERO);
                break;
            case R.id.audio_maike:
                if (isCloseMaiKe) {
                    isCloseMaiKe = false;
                    audioMaike.setImageResource(R.mipmap.close_maike_press);
                    CSAudio.getInstance().micSwitch(false);
                } else {
                    isCloseMaiKe = true;
                    audioMaike.setImageResource(R.mipmap.open_maike_press);
                    CSAudio.getInstance().micSwitch(true);
                }
                break;
            case R.id.audio_laba:
                if (isCloseAudio) {
                    isCloseAudio = false;
                    audioLaba.setImageResource(R.mipmap.close_laba);
                    CSAudio.getInstance().speakerSwitch(false);
                } else {
                    isCloseAudio = true;
                    audioLaba.setImageResource(R.mipmap.open_laba_press);
                    CSAudio.getInstance().speakerSwitch(true);
                }
                break;
            case R.id.keyBoardd:
                if (isDisplayBoard) {
                    keyBoardLayout.setVisibility(View.VISIBLE);
                    isDisplayBoard = false;
                    attrLayout.setVisibility(View.GONE);
                } else {
                    keyBoardLayout.setVisibility(View.GONE);
                    isDisplayBoard = true;
                    attrLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.audio_moude:
                if (isOpenPhoneOn) {
                    mAudioManager.setSpeakerphoneOn(false);
                    setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
                    //把声音设定成Earpiece（听筒）出来，设定为正在通话中
                    mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                            mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                            AudioManager.FLAG_PLAY_SOUND);
                    isOpenPhoneOn = false;
                    audioMoude.setImageResource(R.mipmap.new_ui_notice_button_press);
                    CSMediaCtrl.audioCallGain = 400;
                    Toast.makeText(this, getResources().getString(R.string.toTingTong), Toast.LENGTH_SHORT).show();
                } else {
                    mAudioManager.setSpeakerphoneOn(true);
                    mAudioManager.setMode(AudioManager.STREAM_SYSTEM);
                    isOpenPhoneOn = true;
                    audioMoude.setImageResource(R.mipmap.new_ui_notice_button);
                    CSMediaCtrl.audioCallGain = Integer.parseInt(scallGain);
                    Toast.makeText(this, getResources().getString(R.string.toYangShengQi), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    class MicTouchListener implements View.OnTouchListener {

        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    MIC_STATUE = STATUS_LISTEN;
                    audioMicControl.setBackground(getResources().getDrawable(R.mipmap.audio_call_nomal));
                    CSAudio.getInstance().groupMicControl(false);
                    mAudioManager.setMicrophoneMute(true);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    MIC_STATUE = STATUS_LISTEN;
                    audioMicControl.setBackground(getResources().getDrawable(R.mipmap.audio_call_nomal));
                    CSAudio.getInstance().groupMicControl(false);
                    break;
                case MotionEvent.ACTION_DOWN:
                    v.performClick();
                    MIC_STATUE = STATUS_TALK;
                    audioMicControl.setBackground(getResources().getDrawable(R.mipmap.audio_call_press));
                    CSAudio.getInstance().groupMicControl(true);
                    mAudioManager.setMicrophoneMute(false);
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
            }
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ChatSureApplication.isHalfAudioCall = false;
        mAudioManager.setMicrophoneMute(false);

        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 20) {
            if (MIC_STATUE == STATUS_TALK)
                return true;
            MIC_STATUE = STATUS_TALK;
            audioMicControl.setBackground(getResources().getDrawable(R.mipmap.audio_call_press));
            CSAudio.getInstance().groupMicControl(true);
            mAudioManager.setMicrophoneMute(false);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 20) {
            if (MIC_STATUE == STATUS_LISTEN)
                return true;
            MIC_STATUE = STATUS_LISTEN;
            audioMicControl.setBackground(getResources().getDrawable(R.mipmap.audio_call_nomal));
            CSAudio.getInstance().groupMicControl(false);
            mAudioManager.setMicrophoneMute(true);
        }
        return super.onKeyUp(keyCode, event);
    }

    private void callRelease(int cause) {
        mHandler.removeCallbacks(timeRun);
        CSAudio.getInstance().audioHandUp(mAudioCallID, IMType.IM_TYPE_GROUP_CALL, cause);
        setResult(RESULT_OK);
        finish();
    }
}
