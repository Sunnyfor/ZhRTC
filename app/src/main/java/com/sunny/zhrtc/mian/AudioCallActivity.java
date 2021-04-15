package com.sunny.zhrtc.mian;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.ids.idtma.jni.aidl.CallAnswerEntity;
import com.ids.idtma.jni.aidl.CallOutEntity;
import com.ids.idtma.jni.aidl.CallReleaseEntity;
import com.ids.idtma.jni.aidl.MediaAttribute;
import com.ids.idtma.jni.aidl.MediaStreamEntity;
import com.ids.idtma.media.CSAudio;
import com.ids.idtma.media.CSMediaCtrl;
import com.ids.idtma.media.MediaState;
import com.ids.idtma.util.Compat;
import com.ids.idtma.util.DateUtils;
import com.ids.idtma.util.IdsLog;
import com.ids.idtma.util.constants.IDTMsgType;
import com.ids.idtma.util.constants.IMType;
import com.sunny.zhrtc.MyApplication;
import com.sunny.zhrtc.R;
import com.sunny.zhrtc.base.BaseActivity;
import com.sunny.zhrtc.constants.C;
import com.sunny.zhrtc.constants.UiCauseConstants;
import com.sunny.zhrtc.util.FileUtil;
import com.sunny.zhrtc.util.RingtoneUtils;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AudioCallActivity extends BaseActivity {


    private static final String TAG = "lzn_AudioCallActivity";
    @BindView(R.id.ui_logo)
    ImageButton uiLogo;
    @BindView(R.id.my_num)
    TextView myNum;
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;
    @BindView(R.id.video_title_layout)
    RelativeLayout videoTitleLayout;
    @BindView(R.id.tv_call_number)
    TextView tvCallNumber;
    @BindView(R.id.tv_calling_hint)
    TextView tvCallingHint;
    @BindView(R.id.chronometer_call_time)
    TextView chronometerCallTime;
    @BindView(R.id.tiaoshi2)
    TextView tiaoshi2;
    @BindView(R.id.tiaoshi1)
    TextView tiaoshi1;
    @BindView(R.id.bofang_textView)
    TextView bofangTextView;
    @BindView(R.id.new_ui_audio_call_toplayout)
    RelativeLayout newUiAudioCallToplayout;
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
    @BindView(R.id.btn_answer)
    ImageButton llAnswer;
    @BindView(R.id.new_ui_video_call_divide0)
    TextView newUiVideoCallDivide0;
    @BindView(R.id.btn_hangup)
    ImageButton llHangup;
    @BindView(R.id.new_ui_video_call_divide1)
    TextView newUiVideoCallDivide1;
    @BindView(R.id.btn_hangup_conned)
    ImageButton btnHangup;
    @BindView(R.id.audio_maike)
    ImageView audioMaike;
    @BindView(R.id.audio_laba)
    ImageView audioLaba;
    @BindView(R.id.keyBoardd)
    ImageView keyBoardd;
    @BindView(R.id.audio_moude)
    ImageView audioMoude;
    @BindView(R.id.attr)
    ImageView attr;
    @BindView(R.id.new_ui_audio_call_bottomlayout)
    RelativeLayout newUiAudioCallBottomlayout;
    private String scallGain;//记住的音量放大参数100的倍数
    private AudioManager mAudioManager;
    private int mAudioCallID;
    private String mCaller;//主叫num
    private String mCallee;//被叫num
    private String mCallerName;//主叫name
    private String mCalleeName;//被叫name
    private MediaAttribute mediaAttribute;
    private int notificationFlag;
    private int mStatus;
    private boolean isOpenPhoneOn = true;//是否打开扬声器
    private boolean isDisplayBoard = true;//是否显示拨号盘
    private boolean isCloseMaiKe = true; // 是否关闭麦克
    private boolean isCloseAudio = true; // 是否关闭喇叭
    private static boolean mNowIsConnected;//当前已经在通话
    private int time;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_call);
        ButterKnife.bind(this);
        initData();
        initCall();
        scallGain = Compat.readIni("AUDIO", "SCALLGAIN");
    }


    private void initData() {
        // 获取参数
        mStatus = getIntent().getIntExtra(CallHelper.CALL_STATUS, -1);
        mAudioCallID = getIntent().getIntExtra(CallHelper.CALL_ID, -1);
        if (mStatus == CallHelper.FLAG_CALLING) {
            CallOutEntity entity = getIntent().getParcelableExtra(CallHelper.CallEntity);
            mAudioCallID = CSAudio.getInstance().singleAudioCall(entity);
        }
        if (mAudioCallID == MediaState.CAUSE_RESOURCE_UNAVAIL) {
            toast(getString(R.string.resource_not_use));
            finish();
            return;
        }
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // 主叫号码
        mCaller = getIntent().getStringExtra(CallHelper.CALLER);
        // 对方号码
        mCallee = getIntent().getStringExtra(CallHelper.CALLEE);
        mCallerName = getIntent().getStringExtra(CallHelper.CALLER_NAME);
        mCalleeName = getIntent().getStringExtra(CallHelper.CALLEE_NAME);
        if (mCalleeName == null) {
            mCalleeName = mCallee;
        }
        mediaAttribute = getIntent().getParcelableExtra(CallHelper.MediaAttr);

        myNum.setText(MyApplication.Companion.getUserAccount());

        notificationFlag = getIntent().getIntExtra(CallHelper.NOTIFICATION_INTENT, -1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String stringExtra = getIntent().getStringExtra(CallHelper.AudioMultiple);
        if (stringExtra != null && !"".equals(stringExtra)) {
            scallGain = stringExtra;
        }
    }

    private void initCall() {
        switch (mStatus) {
            case CallHelper.FLAG_INCOMING:  // 来电进入
                if (mCallerName != null && !mCallerName.equals("")) {
                    tvCallNumber.setText(mCallerName + getResources().getString(R.string.nowCalling));
                } else {
                    String nowCalling = getResources().getString(R.string.nowCalling);
                    tvCallNumber.setText(mCaller + nowCalling);

                }
                tvCallingHint.setText(getResources().getString(R.string.calling));
                setCalleeMode();
                if (notificationFlag == -1) {
                    RingtoneUtils.playRingtone(this);
                }
                break;
            case CallHelper.FLAG_CALLING: // 呼叫出去
                // 对界面视图进行操作
                setCallerMode();
                tvCallingHint.setVisibility(View.VISIBLE);
                tvCallNumber.setText(getResources().getString(R.string.call) + mCalleeName);
                break;
            // 只有在有notification的时候才会调用这个地方？
            case CallHelper.FLAG_ANSWER:
                setCallerMode();
                tvCallingHint.setVisibility(View.INVISIBLE);
                if (null == mCalleeName || "".equals(mCalleeName))
                    tvCallNumber.setText(mCalleeName);
                else
                    tvCallNumber.setText(mCallerName);
                if (notificationFlag != -1) {
                    tvCallNumber.setText(mCallerName + getResources().getString(R.string.tonghuazhonging));
                    time = getIntent().getIntExtra(CallHelper.CALL_TIME_LENGTH, 0);
                    int l = (int) ((System.currentTimeMillis() - MyApplication.Companion.getLastTime()) / 1000);
                    time = time + l;
                    chronometerCallTime.setVisibility(View.VISIBLE);
                    chronometerCallTime.setText(getString(R.string.callTimeLength) + timeFormat(time));
                    mHandler.post(timeRun);
                }
                break;

            case CallHelper.CALL_AUTO_ANSWER:
                mStatus = CallHelper.FLAG_ANSWER;
                RingtoneUtils.stopRingtone();
                setCallerMode();
                mHandler.post(timeRun);
                tvCallNumber.setText(getIntent().getStringExtra(CallHelper.CALLER));
                CSAudio.getInstance().callAnswer(mAudioCallID, mediaAttribute, IMType.IM_TYPE_AUDIO_CALL);
                break;
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private Runnable timeRun = new Runnable() {
        @Override
        public void run() {
            time++;
            chronometerCallTime.setVisibility(View.VISIBLE);
            chronometerCallTime.setText(getString(R.string.callTimeLength) + timeFormat(time));
            mHandler.postDelayed(timeRun, 1000);
        }
    };

    /**
     * 设置界面是主叫模式
     */
    private void setCallerMode() {
        tvCallingHint.setVisibility(View.INVISIBLE);
        llAnswer.setVisibility(View.GONE);
        newUiVideoCallDivide0.setVisibility(View.GONE);
        llHangup.setVisibility(View.GONE);
        newUiVideoCallDivide1.setVisibility(View.GONE);
        btnHangup.setVisibility(View.VISIBLE);
        tvCallingHint.setText(getResources().getString(R.string.nowAudioCalling));
    }

    /**
     * 设置界面是被叫模式
     */
    private void setCalleeMode() {
        tvCallingHint.setVisibility(View.VISIBLE);
        llAnswer.setVisibility(View.VISIBLE);
        newUiVideoCallDivide0.setVisibility(View.VISIBLE);
        llHangup.setVisibility(View.VISIBLE);
        newUiVideoCallDivide1.setVisibility(View.GONE);
        btnHangup.setVisibility(View.GONE);
        tvCallingHint.setText(getResources().getString(R.string.Invite_you_voice_call));
    }

    @OnClick({R.id.dialNum1, R.id.dialNum2, R.id.dialNum3, R.id.dialNum4, R.id.dialNum5, R.id.dialNum6, R.id.dialNum7,
            R.id.dialNum8, R.id.dialNum9, R.id.dialx, R.id.dialNum0, R.id.dialj, R.id.btn_answer, R.id.btn_hangup, R.id.btn_hangup_conned,
            R.id.audio_maike, R.id.audio_laba, R.id.keyBoardd, R.id.audio_moude})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_answer:
                mNowIsConnected = true;
                closeSpeakerPhone();

                tvCallNumber.setText(mCaller + getResources().getString(R.string.tonghuazhonging));
                mStatus = CallHelper.FLAG_ANSWER;
                RingtoneUtils.stopRingtone();
                setCallerMode();
                mHandler.post(timeRun);
                CSAudio.getInstance().callAnswer(mAudioCallID, mediaAttribute, IMType.IM_TYPE_AUDIO_CALL);
                break;
            case R.id.btn_hangup:

                RingtoneUtils.stopRingtone();
                //停止计时  将时间存入数据库  关闭mNotificationManager的通知
                if (mStatus == CallHelper.FLAG_ANSWER) {
                    callRelease(UiCauseConstants.CAUSE_ZERO);
                } else {
                    callRelease(UiCauseConstants.CAUSE_CALL_REJ_BY_USER);
                }
                break;
            case R.id.btn_hangup_conned:
                mNowIsConnected = false;

                RingtoneUtils.stopRingtone();
                // 停止计时  将时间存入数据库  关闭mNotificationManager的通知
                callRelease(UiCauseConstants.CAUSE_ZERO);
                Compat.WriteIni("AUDIO", "SCALLGAIN", scallGain);
                break;
            case R.id.audio_maike:
                if (isCloseMaiKe) {
                    isCloseMaiKe = false;
                    audioMaike.setBackgroundResource(R.mipmap.close_maike_press);
                    CSAudio.getInstance().micSwitch(false);
                } else {
                    isCloseMaiKe = true;
                    audioMaike.setBackgroundResource(R.mipmap.open_maike_press);
                    CSAudio.getInstance().micSwitch(true);
                }
                break;
            case R.id.audio_laba:
                if (isCloseAudio) {
                    IdsLog.d("xiezhixian11", "关闭声音");
                    isCloseAudio = false;
                    audioLaba.setBackgroundResource(R.mipmap.close_laba);
                    CSAudio.getInstance().speakerSwitch(false);
                } else {
                    IdsLog.d("xiezhixian11", "打开声音");
                    isCloseAudio = true;
                    audioLaba.setBackgroundResource(R.mipmap.open_laba_press);
                    CSAudio.getInstance().speakerSwitch(true);
                }
                break;
            case R.id.keyBoardd:
                if (isDisplayBoard) {
                    keyBoardLayout.setVisibility(View.VISIBLE);
                    isDisplayBoard = false;
                } else {
                    keyBoardLayout.setVisibility(View.GONE);
                    isDisplayBoard = true;
                }
                break;
            case R.id.audio_moude:
                if (isOpenPhoneOn) {
                    closeSpeakerPhone();
                    Toast.makeText(this, getResources().getString(R.string.toTingTong), Toast.LENGTH_SHORT).show();
                } else {
                    openSpeakerPhone();
                    Toast.makeText(this, getResources().getString(R.string.toYangShengQi), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.dialNum0:
                CSAudio.getInstance().callSendNum(mAudioCallID, "0");
                break;
            case R.id.dialNum1:
                CSAudio.getInstance().callSendNum(mAudioCallID, "1");
                break;
            case R.id.dialNum2:
                break;
            case R.id.dialNum3:
                break;
            case R.id.dialNum4:
                break;
            case R.id.dialNum5:
                break;
            case R.id.dialNum6:
                break;
            case R.id.dialNum7:
                break;
            case R.id.dialNum8:
                break;
            case R.id.dialNum9:
                break;
            case R.id.dialx:
                CSAudio.getInstance().callSendNum(mAudioCallID, "*");
                break;
            case R.id.dialj:
                CSAudio.getInstance().callSendNum(mAudioCallID, "#");
                break;
        }
    }

    @SuppressLint("WrongConstant")
    private void openSpeakerPhone() {
        mAudioManager.setSpeakerphoneOn(true);
        mAudioManager.setMode(AudioManager.STREAM_SYSTEM);
        isOpenPhoneOn = true;
        Log.d(TAG, "扬声器 openSpeakerPhone== >" + mAudioManager.isSpeakerphoneOn());
        audioMoude.setImageResource(R.mipmap.new_ui_notice_button);
        CSMediaCtrl.audioCallGain = Integer.parseInt(scallGain);
    }

    private void closeSpeakerPhone() {
        mAudioManager.setSpeakerphoneOn(false);
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        //把声音设定成Earpiece（听筒）出来，设定为正在通话中
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                AudioManager.FLAG_PLAY_SOUND);
        isOpenPhoneOn = false;
        Log.d(TAG, "扬声器 closeSpeakerPhone== >" + mAudioManager.isSpeakerphoneOn());
        audioMoude.setImageResource(R.mipmap.new_ui_notice_button_press);
        CSMediaCtrl.audioCallGain = 400;

    }

    private void callRelease(int cause) {

        mHandler.removeCallbacks(timeRun);
        try {
            openSpeakerPhone();
        } catch (Exception e) {//NumberFormat
            e.printStackTrace();
        }
        CSAudio.getInstance().audioHandUp(mAudioCallID, IMType.IM_TYPE_AUDIO_CALL, cause);
        saveAudioCallData();
        setResult(RESULT_OK);
        finish();
    }

    private void onCallAnswer(CallAnswerEntity callAnswerEntity) {
        long pUsrCtx = callAnswerEntity.getpUsrCtx();
        if (pUsrCtx == IMType.IM_TYPE_AUDIO_CALL) {
            closeSpeakerPhone();
            tvCallNumber.setText(mCalleeName + getResources().getString(R.string.tonghuazhong));
            setCallerMode();
            mStatus = CallHelper.FLAG_ANSWER;
            // 将计时器清零并启动
            mHandler.post(timeRun);
        }
    }

    @Override
    public void onGetData(String data, int type) {
        super.onGetData(data, type);
        if (type == IDTMsgType.CALL_PEER_ANSWER) {
            mNowIsConnected = true;
            CallAnswerEntity callAnswerEntity = new Gson().fromJson(data, CallAnswerEntity.class);
            onCallAnswer(callAnswerEntity);
        } else if (type == IDTMsgType.CALL_RELEASE) {//对方释放
            CallReleaseEntity callReleaseEntity = new Gson().fromJson(data, CallReleaseEntity.class);
            if (callReleaseEntity.getId() == mAudioCallID) {
                openSpeakerPhone();
                saveAudioCallData();
                RingtoneUtils.stopRingtone();
                toast(new UiCauseConstants().getDataType(callReleaseEntity.getUiCause(), this));
                finish();
            }
        } else if (type == IDTMsgType.MESIA_STREAN_STATISTICS) {
            displayStream(data);
        }

    }

    private void saveAudioCallData() {
        if (CSMediaCtrl.mediaSaveFlag) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(C.FilePath.voiceCallPath + DateUtils.baseDateWithNoTime() + "/");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    String aacName = "test.aac";
                    String aacPath = C.FilePath.voiceCallPath;
                    FileUtil.copyFile(C.FilePath.AAC_PATH, aacPath + "/" + aacName);
                }
            }).start();

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
                tiaoshi1.setText("接收:" + uiRxBytes + "字节,用户" + uiRxUsrBytes + "字节," + uiRxCount + "次,用户" + uiRxUserCount + "次\r\n" + "发送:" + uiTxBytes
                        + "字节,用户" + uiTxUsrBytes + "字节," + uiTxCount + "次,用户" + uiTxUserCount + "次\r\n");
                IdsLog.d("xiezhixian", "语音的打印");
            } else if (2 == ucType) {
                tiaoshi2.setText("接收:" + uiRxBytes + "字节,用户" + uiRxUsrBytes + "字节," + uiRxCount + "次,用户" + uiRxUserCount + "次\r\n" + "发送:" + uiTxBytes
                        + "字节,用户" + uiTxUsrBytes + "字节," + uiTxCount + "次,用户" + uiTxUserCount + "次\r\n");
            }

        }
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
