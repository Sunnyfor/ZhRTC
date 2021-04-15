package com.sunny.zhrtc.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.ids.idtma.util.IdsLog;
import com.sunny.zhrtc.MyApplication;
import com.sunny.zhrtc.R;
import com.sunny.zy.ZyFrameStore;

public class RingtoneUtils {

    private static String uri = "android.resource://" + ZyFrameStore.INSTANCE.getContext().getPackageName() + "/" + R.raw.tishi;
//    private static Uri notification = null;
//    private static Ringtone ringtone = null;

    private static Uri notification = Uri.parse(uri);
    private static Ringtone ringtone = RingtoneManager.getRingtone(MyApplication.Companion.getContext(), notification);

    public static void playRing(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (audioManager != null) {
            ringtone.play();
        }
    }

    public static final String TAG = "RingtoneUtil";

    private static MediaPlayer mMediaPlayer;

    public static void playRingtone(Context context) {
        // 开始播放手机铃声及震动
        try {
            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(context, alert);

            new SpeakerPhoneUtils(context).OpenSpeaker();
//                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);

            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            mMediaPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 停止震动
    public static void stopRingtone() {
        try {
            mSmsRingPlaying = false;
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 当前是否已经在播放
     */
    private static boolean mSmsRingPlaying = false;

    /**
     * 播放语音
     *
     * @param context
     * @param fileName
     */
    public static void playSound(Context context, String fileName) {
        if (mSmsRingPlaying) {
            return;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = new MediaPlayer();
        //设置一个error监听器
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                mMediaPlayer.reset();
                return false;
            }
        });

        try {
            AssetManager assetManager = context.getAssets();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.release();
                    mSmsRingPlaying = false;
                    mMediaPlayer = null;
                }
            });
            AssetFileDescriptor afd = assetManager.openFd(fileName);
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mMediaPlayer.prepare();
            if (fileName.equals("ringtone.mp3")) {
                mMediaPlayer.setLooping(true);// 循环播放
            }
            mMediaPlayer.start();
            mSmsRingPlaying = true;

        } catch (Exception e) {
            IdsLog.d("", "" + e.getMessage());
        }
    }

    @SuppressLint("WrongConstant")
    public static void playSos(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMicrophoneMute(false);
        audioManager.setSpeakerphoneOn(true);// 使用扬声器外放，即使已经插入耳机
        audioManager.setMode(AudioManager.STREAM_MUSIC);
        AssetManager assetManager = context.getAssets();
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        if (mMediaPlayer.isPlaying()) {
            return;
        }
        try {
            AssetFileDescriptor afd = assetManager.openFd("sos.mp3");
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mMediaPlayer.setLooping(true);// 循环播放
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
