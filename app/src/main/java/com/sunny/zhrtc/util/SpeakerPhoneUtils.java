
package com.sunny.zhrtc.util;

import android.content.Context;
import android.media.AudioManager;

import com.ids.idtma.util.IdsLog;

/**
 * 免提功能管理类
 */
public class SpeakerPhoneUtils {

    private Context mContext;
    private int currVolume;

    public SpeakerPhoneUtils() {
        super();
    }

    public SpeakerPhoneUtils(Context mContext) {
        super();
        this.mContext = mContext;
    }

    // 打开扬声器
    public void OpenSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_NORMAL);
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
            IdsLog.d("currVolume", "currVolume" + currVolume);
            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 关闭扬声器
    public void CloseSpeaker() {

        try {
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void StopBluetoothSco(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setBluetoothScoOn(false);
        mAudioManager.stopBluetoothSco();
    }

    public static void StartBluetoothSco(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setMode(AudioManager.STREAM_VOICE_CALL);
        mAudioManager.setBluetoothScoOn(true);
        mAudioManager.startBluetoothSco();
    }

}
