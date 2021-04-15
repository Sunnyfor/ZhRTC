package com.sunny.zhrtc.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.ids.idtma.biz.core.proxy.IDSApiProxyMgr;
import com.ids.idtma.jni.aidl.UserData;
import com.ids.idtma.jni.aidl.UserGroupData;
import com.ids.idtma.jni.aidl.UserOptResponse;
import com.ids.idtma.jni.aidl.WorkInfoEntity;
import com.ids.idtma.person.PersonCtrl;
import com.sunny.zhrtc.MyApplication;
import com.sunny.zhrtc.R;
import com.sunny.zhrtc.mian.GroupDataActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Administrator on 2018/8/29.
 */

public class Utils {

    /**
     * user的信息查询和修改后的处理
     *
     * @param context 上下文
     * @param data    json字符串
     */
    public static void getUserOptionRsp(Context context, String data) {
        UserOptResponse userOptResponse = new Gson().fromJson(data, UserOptResponse.class);
        UserData userData = userOptResponse.getUserData();
        if (userData.getUcNum().equals("")) {
            return;
        }
        String workInfo = userData.getWorkInfo();
        if (TextUtils.isEmpty(workInfo) || workInfo.contains("##")) {
            WorkInfoEntity w = new WorkInfoEntity();
            userData.setWorkInfo(new Gson().toJson(w));
            IDSApiProxyMgr.getCurProxy().IDT_UModify((long) 400, userData);
            MyApplication.Companion.setUserData(userData);
            return;
        }
        if (userOptResponse.getDwSn() == GroupDataActivity.QUERY_THE_MEMBER) {
            if (!userData.getUcNum().equals(MyApplication.Companion.getUserAccount())) {
                return;
            }
            String videoUpSwitch = IDSApiProxyMgr.getCurProxy().IDT_ReadJasonStr(workInfo, "c");

            String buildGroupState = IDSApiProxyMgr.getCurProxy().IDT_ReadJasonStr(workInfo, "b");

            String halfSingleCall = IDSApiProxyMgr.getCurProxy().IDT_ReadJasonStr(workInfo, "d");

            MyApplication.Companion.setUserData(userData);
        }  /*else if (userOptResponse.getDwOptCode() == 4 && userOptResponse.getDwSn() == C.IDTCode.GET_USER_INFO_PHONE) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + userData.getUcTel()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }*/
    }


    public static UserGroupData getUserGroupData(String num) {
        List<UserGroupData> groupDatas = PersonCtrl.mGroupData;
        for (int i = 0; i < groupDatas.size(); i++) {
            UserGroupData userGroupData = groupDatas.get(i);
            if (userGroupData.getUcNum().equals(num)) {
                return userGroupData;
            }
        }
        return null;
    }

    /**
     * 将idt.ini文件进行保存到data/data/包名下
     *
     * @param context
     */
    public static void saveIniFile(Context context) {
        FileOutputStream outStream;
        try {// 可被读写
//            outStream = context.openFileOutput("IDT.ini", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
            outStream = context.openFileOutput("IDT.ini", Context.MODE_PRIVATE);

            InputStream inStream = context.getResources().openRawResource(R.raw.idt);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }
            outStream.close();
            inStream.close();
            Log.d("CompatIni", "IDT.ini文件保存成功");
        } catch (FileNotFoundException e) {
            Log.d("CompatIni", "文件未找到");

        } catch (IOException e) {
            Log.d("CompatIni", "文件操作异常");

        }

    }
}
