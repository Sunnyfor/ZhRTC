package com.sunny.zhrtc.mian;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ids.idtma.jni.aidl.IMMsgEntity;
import com.sunny.zhrtc.R;
import com.sunny.zhrtc.bean.MessageCommData;
import com.sunny.zhrtc.bean.SoSData;
import com.sunny.zhrtc.constants.C;
import com.sunny.zhrtc.util.RingtoneUtils;
import com.sunny.zhrtc.util.SpeakerPhoneUtils;
import com.sunny.zhrtc.util.UserAttrUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReceiveSosActivity extends Activity {

    @BindView(R.id.userIcon)
    ImageView userIcon;
    @BindView(R.id.num)
    TextView num;
    @BindView(R.id.address)
    TextView addressTextView;
    @BindView(R.id.time)
    TextView timeTextView;
    @BindView(R.id.confirm)
    TextView confirm;
    @BindView(R.id.cancel)
    TextView cancel;
    private IMMsgEntity imMsgEntity;
    private String pcFrom = "";
    private SoSData soSData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_sos);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        ButterKnife.bind(this);
        imMsgEntity = getIntent().getParcelableExtra(C.intentKeys.DATA);
        initData();
    }

    private void initData() {
        RingtoneUtils.playSos(this);
        Gson gson = new Gson();
        MessageCommData messageCommData = gson.fromJson(imMsgEntity.getPcTxt(), MessageCommData.class);
        soSData = gson.fromJson(messageCommData.getSubPara(), SoSData.class);
        String time = soSData.getTime();
        pcFrom = imMsgEntity.getPcFrom();
        String name = UserAttrUtils.getName(pcFrom);
        num.setText(name + "(" + pcFrom + ")");
        timeTextView.setText(time);
//        String ip = SharedPreferencesUtil.getStringPreference(this, C.sp.ip, "");
//        String path = C.FilePath.RootDirectory + "/IDT-MA/" + ip + "pic_up/";
//        String s = path + pcFrom + ".png";
//        File file = new File(s);
//        if (file.exists() && file.length() >0) {
//            Bitmap decodeFile = BitmapFactory.decodeFile(s);
//            userIcon.setImageBitmap(decodeFile);
//        }
        getAddress();
    }

    private void getAddress() {
//        double latitude = soSData.getLatitude();
//        double longitude = soSData.getLongitude();
//        LatLng baiLocition = GpsUtils.getBaiLocition(latitude, longitude);
//
//        BaiduGeoCodeUtil baiduGeoCodeUtil = new BaiduGeoCodeUtil();
//        addressTextView.setText(latitude+"-"+longitude);
//        baiduGeoCodeUtil.setmListener(new BaiduGeoCodeUtil.addressPointListener() {
//            @Override
//            public void getAddress(String address) {
//                addressTextView.setText(address);
//            }
//        });
//        BaiduGeoCodeUtil.reverseGeoCode(baiLocition);
    }

    @OnClick({R.id.confirm, R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                //TODO 之前做的是跳转地图
//                    Intent intent = new Intent(this, MapActivity.class);
//                    intent.putExtra("user_phone_num", pcFrom);
//                    intent.putExtra("location", ",");
//                    LatLng desLatLng = GpsUtils.getBaiLocition(soSData.getLatitude(),soSData.getLongitude());
//                    intent.putExtra("longitude", desLatLng.longitude);
//                    intent.putExtra("latitude", desLatLng.latitude);
//                    startActivity(intent);
                RingtoneUtils.stopRingtone();
                setVolumeControlStream(AudioManager.STREAM_MUSIC);
                new SpeakerPhoneUtils(this).OpenSpeaker();
                finish();
//                CustomActivityManager.deleteActivity(this);
                break;
            case R.id.cancel:
                Log.d("11", "无用行");
                RingtoneUtils.stopRingtone();
                setVolumeControlStream(AudioManager.STREAM_MUSIC);
                new SpeakerPhoneUtils(this).OpenSpeaker();
//                CustomActivityManager.deleteActivity(this);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        BaiduGeoCodeUtil.releaseGeoCode();
//        finish();
    }

    @Override
    public void onBackPressed() {
    }
}
