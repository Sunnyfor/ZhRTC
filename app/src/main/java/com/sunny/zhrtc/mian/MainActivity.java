package com.sunny.zhrtc.mian;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.ids.idtma.IdtLib;
import com.ids.idtma.biz.core.proxy.IDSApiProxyMgr;
import com.ids.idtma.biz.core.proxy.Proxy;
import com.ids.idtma.jni.IDTApi;
import com.ids.idtma.jni.aidl.LoginResult;
import com.ids.idtma.util.constants.IDTMsgType;
import com.sunny.zhrtc.MyApplication;
import com.sunny.zhrtc.R;
import com.sunny.zhrtc.base.BaseActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ids.idtma.util.constants.Constant.REQ_GROUP_DATA;

public class MainActivity extends BaseActivity {

    @BindView(R.id.userAccount)
    EditText userAccount;
    @BindView(R.id.ip)
    EditText ip;
    public static final int NO_ACCOUNT = 17;// 用户不存在
    public static final int SERVER_TIME_OUT = 14;// 连接超时
    public static final int PASSWORD_ERR = 24;// 密码错误
    public static final int ACCOUNT_ISALREADY_REGISTERD = 33;// 该帐号在别处登录
    public static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1000;
    public static final int PERMISSIONS_REQUEST_LOCATION = 2000;
    public static final int PERMISSIONS_REQUEST_CAMERA = 3000;
    public static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 4000;
    public static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 5000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkUserPermission();
//        String s = CompatIni.readIni("RTP_VIDEO_0", "BITRATE");
//        CompatIni.WriteIni("DEBUG", "LOG", "0");
//        CompatIni.WriteIni("DEBUG", "LEVEL", "6");
//        CompatIni.WriteIni("SYSTEM", "MSG_MEMNUM", "8");
        IDSApiProxyMgr.getCurProxy().IDT_GetStatus(null);

//        IdtLib.login(ip.getText().toString().trim(), 10000, userAccount.getText().toString(),
//                passWord.getText().toString());

    }

    private void checkUserPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSIONS_REQUEST_RECORD_AUDIO);
        }

    }

    //请求权限的回调函数
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //根据请求码做不同的逻辑处理
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("警告！")
                        .setMessage("缺少权限，不能定位")
                        .setPositiveButton("OK", null)
                        .create().show();
            }
        }


        if (requestCode == PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("警告！")
                        .setMessage("缺少权限，请手动打开权限。")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create().show();
            }
        }
    }

    @OnClick({R.id.buttonLogout, R.id.btn_login, R.id.group, R.id.user})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.buttonLogout) {
            IdtLib.clear();
            IdtLib.getIdsVersion();
//            toast("注销成功");
        } else if (view.getId() == R.id.btn_login) {
            IdtLib.login(ip.getText().toString().trim(), 10000,
                    userAccount.getText().toString(),
                    userAccount.getText().toString());

        } else if (view.getId() == R.id.group) {
            Proxy.myDwsn = 0;
            IDTApi.IDT_GQueryU(REQ_GROUP_DATA, "80011", 1, 0, 0);
        } else if (view.getId() == R.id.user) {
            IDTApi.IDT_UQuery(GroupDataActivity.QUERY_THE_MEMBER, "80039");//查询用户数据
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        IDSApiProxyMgr.getCurProxy().Exit();
//        IDSApiProxyMgr.getCurProxy().unloadLibrary(this);
        IdtLib.clear();
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = this.getPackageName();
        am.restartPackage(this.getPackageName());
        am.killBackgroundProcesses(packageName);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    @Override
    public void onGetData(String data, int type) {
        super.onGetData(data, type);
        if (type == IDTMsgType.LOGIN) {
//            IdtLib.clear();
//            IdtLib.login(ip.getText().toString().trim(), 10000, userAccount.getText().toString(),
//                    passWord.getText().toString());
            Gson gson = new Gson();
            LoginResult loginResult = gson.fromJson(data, LoginResult.class);
            if (loginResult.getResult() == 1) {
                MyApplication.Companion.setUserAccount(userAccount.getText().toString());
                MyApplication.Companion.setServiceIp(ip.getText().toString().trim());
                toast("登录成功,开始查组");
                IDSApiProxyMgr.getCurProxy().IDT_UQuery(GroupDataActivity.QUERY_THE_MEMBER, MyApplication.Companion.getUserAccount());
                Intent intent = new Intent(this, GroupDataActivity.class);
                startActivity(intent);
                finish();
            } else {
                int state = loginResult.getState();
                switch (state) {
                    case NO_ACCOUNT:
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.you_user_no_err), Toast.LENGTH_SHORT).show();
                        IdtLib.loginFail();
                        break;
                    case SERVER_TIME_OUT:
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.server_timed_out), Toast.LENGTH_SHORT).show();
                        IdtLib.loginFail();
                        break;
                    case PASSWORD_ERR:
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.you_password_err), Toast.LENGTH_SHORT).show();
                        IdtLib.loginFail();
                        break;
                    case ACCOUNT_ISALREADY_REGISTERD:
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.account_isAlready_registered), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }

    private void saveIniFile() {
        FileInputStream is = null;
        FileOutputStream fos = null;
        try {
            is = this.openFileInput("IDT.ini");
            fos = new FileOutputStream(new File("/mnt/sdcard/idt.txt"));
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取 buffer字节
                fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
            }
            Toast.makeText(this, getResources().getString(R.string.exportSucc), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
