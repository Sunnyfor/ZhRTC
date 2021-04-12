package com.sunny.zhrtc

import android.app.Application
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.http.ZyConfig

/**
 * Desc
 * Author ZY
 * Mail yongzuo.chen@foxmail.com
 * Date 2021/4/12 16:21
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 初始化应用管理类
        ZyFrameStore.init(this)

        // 设置 StatusBar 模式：true 为黑字； false 为白字
        ZyConfig.statusBarIsDark = false
//        ZyConfig.authorities = "com.sunny.zhrtc.provider"

    }
}