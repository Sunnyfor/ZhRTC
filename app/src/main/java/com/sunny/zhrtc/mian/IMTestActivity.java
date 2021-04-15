package com.sunny.zhrtc.mian;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ids.idtma.biz.core.proxy.IDSApiProxyMgr;
import com.ids.idtma.ftp.FtpUtils;
import com.ids.idtma.jni.aidl.MediaAttribute;
import com.ids.idtma.util.constants.Constant;
import com.ids.idtma.util.constants.IMType;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.sunny.zhrtc.MyApplication;
import com.sunny.zhrtc.R;
import com.sunny.zhrtc.activity.GroupManageActivity;
import com.sunny.zhrtc.base.BaseActivity;
import com.sunny.zhrtc.constants.C;
import com.sunny.zhrtc.util.FileUtil;
import com.sunny.zhrtc.util.FtpListenerUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ids.idtma.ftp.FtpUtils.UPLOAD_FAILED;
import static com.ids.idtma.ftp.FtpUtils.UPLOAD_SUCCESS;

public class IMTestActivity extends BaseActivity {
    @BindView(R.id.chatListView)
    ListView mListView;
    @BindView(R.id.btn_set_mode_voice)
    Button btnSetModeVoice;
    @BindView(R.id.btn_set_mode_keyboard)
    Button btnSetModeKeyboard;
    @BindView(R.id.btn_press_to_speak)
    LinearLayout btnPressToSpeak;
    @BindView(R.id.et_sendmessage)
    EditText etSendmessage;
    @BindView(R.id.edittext_layout)
    RelativeLayout edittextLayout;
    @BindView(R.id.btn_more)
    Button btnMore;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.btn_voice_call)
    ImageView btnVoiceCall;
    @BindView(R.id.container_picture)
    LinearLayout containerPicture;
    @BindView(R.id.btn_video_call)
    ImageView btnViedeoCall;
    @BindView(R.id.container_location)
    LinearLayout containerLocation;
    @BindView(R.id.btn_video_upload)
    ImageView btnViedeoCallDown;
    @BindView(R.id.container_video_luxiang)
    LinearLayout containerVideoLuxiang;
    @BindView(R.id.btn_video)
    ImageView btnVideo;
    @BindView(R.id.container_video)
    LinearLayout containerVideo;
    @BindView(R.id.btn_picture)
    ImageView btnPicture;
    @BindView(R.id.container_file)
    LinearLayout containerFile;
    @BindView(R.id.btn_location)
    ImageView btnLocation;
    @BindView(R.id.container_voice_call)
    LinearLayout containerVoiceCall;
    @BindView(R.id.btn_video_record)
    ImageView btnVideoLuxiang;
    @BindView(R.id.container_voice_call_group)
    LinearLayout containerVoiceCallGroup;
    @BindView(R.id.btn_file)
    ImageView btnFile;
    @BindView(R.id.container_voice_down_group)
    LinearLayout containerVoiceDownGroup;
    @BindView(R.id.btn_viedeo_camera_now)
    ImageView btnVideoCameraNow;
    @BindView(R.id.container_camera_now)
    LinearLayout containerCameraNow;
    @BindView(R.id.btn_viedeo_camera_history)
    ImageView btnVideoCameraHistory;
    @BindView(R.id.container_camera_history)
    LinearLayout containerCameraHistory;
    @BindView(R.id.btn_picture_some)
    ImageView btnPictureSome;
    @BindView(R.id.btnSendRecordGroup)
    ImageView btnVideoLuxiangSome;
    @BindView(R.id.btn_file_some)
    ImageView btnFileSome;
    @BindView(R.id.btn_loction_some)
    ImageView btnLoctionSome;
    @BindView(R.id.groupManageLayout)
    LinearLayout groupManageLayout;
    @BindView(R.id.more_person)
    LinearLayout morePerson;
    @BindView(R.id.more_group)
    LinearLayout moreGroup;
    @BindView(R.id.refreshView)
    SmartRefreshLayout refreshView;
    @BindView(R.id.parent)
    LinearLayout parent;
    @BindView(R.id.sendVideoLayout)
    LinearLayout sendVideoLayout;
    @BindView(R.id.tv_startRecorder)
    TextView tvStartRecorder;
    @BindView(R.id.mic_image)
    ImageView micImage;
    @BindView(R.id.recording_hint)
    TextView recordingHint;
    @BindView(R.id.recording_container)
    RelativeLayout recordingContainer;

    private boolean CurrentTextModel = true;
    private String mTargetNum;
    private double mToWhere;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPLOAD_SUCCESS) {
                toast("上传成功");
            } else if (msg.what == UPLOAD_FAILED) {
                toast("上传失败");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imtest);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mTargetNum = intent.getStringExtra("TARGET_NUM");
        mToWhere = intent.getIntExtra("TO_WHERE", -1);

        etSendmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    btnSend.setVisibility(View.VISIBLE);
                    btnMore.setVisibility(View.GONE);
                } else {
                    btnSend.setVisibility(View.GONE);
                    btnMore.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @OnClick({R.id.btn_more, R.id.btn_send, R.id.btn_voice_call, R.id.et_sendmessage,
            R.id.btn_video_call, R.id.btn_video_upload, R.id.btn_video, R.id.btn_picture, R.id.btn_location,
            R.id.btn_video_record, R.id.btn_file, R.id.btn_set_mode_voice, R.id.tv_startRecorder, R.id.btn_picture_some,
            R.id.btn_loction_some, R.id.btn_file_some, R.id.btnSendRecordGroup, R.id.buildGroup
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_more:
                if (morePerson.getVisibility() == View.VISIBLE || moreGroup.getVisibility() == View.VISIBLE) {
                    morePerson.setVisibility(View.GONE);
                    moreGroup.setVisibility(View.GONE);
                } else {
                    hideKeyboard();
                    if (mToWhere == MyApplication.Companion.getTO_PERSON()) {
                        morePerson.setVisibility(View.VISIBLE);
                    } else {
                        moreGroup.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.et_sendmessage:
                if (morePerson.getVisibility() == View.VISIBLE || moreGroup.getVisibility() == View.VISIBLE) {
                    morePerson.setVisibility(View.GONE);
                    moreGroup.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_send:
                //普通文本
                String content = etSendmessage.getText().toString().trim();
                int i = -1;
                if (!TextUtils.isEmpty(content)) {
                    if (mTargetNum.contains("#")) {
                        String num = mTargetNum.replace("#", "");
                        i = IDSApiProxyMgr.getCurProxy().iMSend(0, IMType.IM_TYPE_TXT, num, content, null, null, null, 0);
                    } else {
                        i = IDSApiProxyMgr.getCurProxy().iMSend(0, IMType.IM_TYPE_TXT, mTargetNum, content, null, null, null, 0);
                    }
                    //再发送消息
                    etSendmessage.setText(null);
                    if (i == 0) {
                        toast("success");
                    }
                } else
                    Toast.makeText(this, "发送内容不能为空", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_voice_call:
                //语音通话
                CallHelper.getInstance(this).callOut(Constant.CALL_TYPE_SINGLE_CALL, mTargetNum, mTargetNum, IMType.IM_TYPE_AUDIO_CALL, new MediaAttribute(1, 1, 0, 0));
                break;
            case R.id.btn_video_call:
                //视频调用
                CallHelper.getInstance(this).callOut(Constant.SRV_TYPE_WATCH_DOWN, mTargetNum, mTargetNum, IMType.IM_TYPE_VIDEO_DOWN, new MediaAttribute(0, 0, 0, 1));
                break;
            case R.id.btn_video_upload:
                //视频上传
                CallHelper.getInstance(this).callOut(Constant.SRV_TYPE_WATCH_UP, mTargetNum, mTargetNum, IMType.IM_TYPE_VIDEO_UP, new MediaAttribute(0, 0, 1, 0));

                break;
            case R.id.btn_video:
                //视频通话
                CallHelper.getInstance(this).callOut(Constant.CALL_TYPE_SINGLE_CALL, mTargetNum, mTargetNum, IMType.IM_TYPE_VIDEO_CALL, new MediaAttribute(1, 1, 1, 1));
                break;
            case R.id.btn_loction_some:
                //群组发送位置
            case R.id.btn_location:
                //发送位置
                i = IDSApiProxyMgr.getCurProxy().iMSend(0, IMType.IM_TYPE_GPS, mTargetNum, "测试维度" + "," + "测试经度" + "," + "测试地址", null, null, null, 0);
                if (i == 0) {
                    toast("发送成功---" + "测试维度" + "," + "测试经度" + "," + "测试地址");
                }
                break;
            case R.id.btn_picture:
                CallHelper.getInstance(this).callOut(Constant.SRV_TYPE_SIMP_CALL, mTargetNum, mTargetNum, IMType.IM_TYPE_AUDIO_CALL, new MediaAttribute(1, 0, 0, 0));
                break;
            case R.id.btn_picture_some:
                //发送图片
            case R.id.btnSendRecordGroup:
                //群组发送录像
            case R.id.btn_video_record:
                //发送录像
            case R.id.btn_file_some:
                //群组发送文件
            case R.id.btn_file:
                showFileSelector();
                //发送文件
                break;
            case R.id.btn_set_mode_voice:
                //发送语音
                if (CurrentTextModel) {
                    //切换到语音
                    tvStartRecorder.setVisibility(View.VISIBLE);
                    etSendmessage.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(etSendmessage.getWindowToken(), 0);
                    }
                    morePerson.setVisibility(View.GONE);
                    moreGroup.setVisibility(View.GONE);
                    btnSetModeVoice.setBackground(ContextCompat.getDrawable(this, R.drawable.chatting_setmode_keyboard_btn));
                    CurrentTextModel = false;
                } else {
                    //切换到文本
                    tvStartRecorder.setVisibility(View.GONE);
                    etSendmessage.setVisibility(View.VISIBLE);
                    etSendmessage.requestFocus();
                    btnSetModeVoice.setBackground(ContextCompat.getDrawable(this, R.mipmap.icon_chat_voice));
                    CurrentTextModel = true;
                }
                break;
            case R.id.buildGroup:
                if (mToWhere == 1) {
                    Intent intent = new Intent(this, GroupManageActivity.class);
                    intent.putExtra(C.intentKeys.TARGET_NUM, mTargetNum);
                    startActivity(intent);
                } else {
                    toast("不是组");
                }

                break;
        }
    }

    private void showFileSelector() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), C.intentKeys.SELECT_FILE);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == C.intentKeys.SELECT_FILE) {
            //发送文件
            // Get the Uri of the selected file
            Uri uri = data.getData();
            if (uri != null) {
                String path = FileUtil.getPath(this, uri);
                sendFile(path, IMType.IM_TYPE_FILE, null);
            }
        }
    }

    /**
     * @param path    文件路径
     * @param imType  短信类型
     * @param content 短信内容
     */
    private void sendFile(String path, final int imType, @Nullable String content) {
        final File file = new File(path);
//        FtpUtils.upLoadSingleFile( "-netLog.txt", "/updateLogs/", netLog, new FtpListenerUtils());


        FtpUtils.upLoadSingleFile(file.getName(), "/updateLogs/", file, new FtpListenerUtils() {
            @Override
            public void Failed(int cause, @Nullable Exception e) {
                Message message = Message.obtain();
                mHandler.sendEmptyMessage(UPLOAD_FAILED);
            }

            @Override
            public void onProgress(int progress, String localFile) {
                //上传进度
                Message message = Message.obtain();
                message.arg1 = progress;
                long length = file.length();
                int pro = (int) (((double) progress / (double) length) * 100);
            }

            @Override
            public void Success(String fileName) {
//                Message message = Message.obtain();
//                message.what = UPLOAD_SUCCESS;
                mHandler.sendEmptyMessage(UPLOAD_SUCCESS);
//              //通知对方去服务器上取该文件
                IDSApiProxyMgr.getCurProxy().iMSend(0, imType, mTargetNum, "", file.getName(), file.getName(), null, 0);
            }
        });
    }

    /**
     * 隐藏键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (getCurrentFocus() != null && manager != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
