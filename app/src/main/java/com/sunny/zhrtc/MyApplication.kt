package com.sunny.zhrtc

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.util.Log
import com.ids.idtma.IdtLib
import com.ids.idtma.ftp.CompatIni
import com.ids.idtma.jni.IDTNativeApi
import com.ids.idtma.jni.aidl.UserData
import com.ids.idtma.util.Compat
import com.ids.idtma.util.IdsLog
import com.sunny.zhrtc.mian.GlobalReceiver
import com.sunny.zhrtc.util.Utils
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.http.ZyConfig
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Desc
 * Author ZY
 * Mail yongzuo.chen@foxmail.com
 * Date 2021/4/12 16:21
 */
class MyApplication : Application() {

    companion object {
        var userAccount = ""
        var serviceIp = "" //登录成功后的ip
        var TO_GROUP = 1
        var TO_PERSON = 2
        var context: Context? = null
        var lastTime: Long = 0
        var isJoinMetting = false
        var SosSwitch = false
        fun getUserData(): UserData? {
            return userData
        }

        fun setUserData(userData: UserData?) {
            this.userData = userData
        }

        private var userData: UserData? = null
    }


    private var globalReceiver: GlobalReceiver? = null


    override fun onCreate() {
        super.onCreate()

        // 初始化应用管理类
        ZyFrameStore.init(this)

        // 设置 StatusBar 模式：true 为黑字； false 为白字
        ZyConfig.statusBarIsDark = false

        // sdk
        val sharedPreferences =
            getSharedPreferences("isChatSureFirst", Context.MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("isChatSureFirstRun", true)
        val editor = sharedPreferences.edit()
        handleSSLHandshake()
        if (isFirstRun) {
            //TODO 如果是第一次运行,需要将raw下的idt.ini复制到data/data/包名下
            CompatIni.saveIniFile(this);
            Utils.saveIniFile(this)
            editor.putBoolean("isFirstRun", false)
            editor.apply()
        }
        context = this
        val s = Compat.readIni("RTP_VIDEO_0", "WIDTH")
        Log.d("CompatIni", "找到$s")
        IdtLib.init(this, 10, "/data/data/com.sunny.zhrtc/files/IDT.ini")
        val s1 = Compat.readIni("RTP_VIDEO_0", "WIDTH")
        Log.d("CompatIni", "找到$s1")
        val filter = IntentFilter()
        filter.addAction(IDTNativeApi.IDT_ACTION)
        if (globalReceiver == null) {
            globalReceiver = GlobalReceiver()
        }
        registerReceiver(globalReceiver, filter)
    }


    @SuppressLint("TrulyRandom")
    fun handleSSLHandshake() {
        try {

            val trustAllCerts =
                arrayOf<TrustManager>(object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls(0)
                    }

                    override fun checkClientTrusted(
                        certs: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun checkServerTrusted(
                        certs: Array<X509Certificate>,
                        authType: String
                    ) {
                    }
                })


            val sc = SSLContext.getInstance("SSL")
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier { hostname, session -> true }
        } catch (ignored: Exception) {
            IdsLog.d("xiezhixian", "绕开https出错" + ignored.message)
        }
    }
}