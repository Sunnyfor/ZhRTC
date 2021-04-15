package com.sunny.zhrtc.util;


import androidx.annotation.Nullable;

import com.ids.idtma.ftp.FtpUtils;
import com.ids.idtma.util.IdsLog;


public class FtpListenerUtils implements FtpUtils.FtpListener {


    @Override
    public void Failed(int errorCode, @Nullable Exception e) {

    }

    @Override
    public void onProgress(int progress, String localFile) {
    }

    @Override
    public void Success(String fileName) {
        IdsLog.d("FTP", "操作成功");
    }

}
