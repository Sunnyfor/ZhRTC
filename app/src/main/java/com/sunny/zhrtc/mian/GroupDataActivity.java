package com.sunny.zhrtc.mian;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ids.idtma.IdtLib;
import com.ids.idtma.ftp.CompatIni;
import com.ids.idtma.jni.IDTApi;
import com.ids.idtma.jni.aidl.CallInEntity;
import com.ids.idtma.jni.aidl.CallReleaseEntity;
import com.ids.idtma.jni.aidl.CallTalkingEntity;
import com.ids.idtma.jni.aidl.LoginResult;
import com.ids.idtma.jni.aidl.MediaStreamEntity;
import com.ids.idtma.jni.aidl.Member;
import com.ids.idtma.jni.aidl.OamGroupData;
import com.ids.idtma.jni.aidl.UserGroupData;
import com.ids.idtma.jni.aidl.UserStatusEntity;
import com.ids.idtma.media.CSAudio;
import com.ids.idtma.media.MediaState;
import com.ids.idtma.person.PersonCtrl;
import com.ids.idtma.util.Compat;
import com.ids.idtma.util.IdsLog;
import com.ids.idtma.util.constants.IDTMsgType;
import com.sunny.zhrtc.MyApplication;
import com.sunny.zhrtc.R;
import com.sunny.zhrtc.activity.BulidGroupActivity;
import com.sunny.zhrtc.adapter.GroupBaseAdapter;
import com.sunny.zhrtc.base.BaseActivity;
import com.sunny.zhrtc.constants.C;
import com.sunny.zhrtc.constants.UiCauseConstants;
import com.sunny.zhrtc.util.SoSUtils;
import com.sunny.zy.utils.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupDataActivity extends BaseActivity {

    @BindView(R.id.expandableListView)
    ExpandableListView expandableListView;
    @BindView(R.id.groupName)
    TextView groupName;
    @BindView(R.id.speakName)
    TextView speakName;
    @BindView(R.id.addGroup)
    TextView addGroup;
    @BindView(R.id.searchGroup)
    TextView searchGroup;
    private GroupBaseAdapter groupBaseAdapter;
    public static final long QUERY_THE_MEMBER = 300;//???????????????????????????
    private MyReceiver receiver;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        IdtLib.clear();
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);

        String packageName = this.getPackageName();
        if (am != null) {
            am.restartPackage(this.getPackageName());
            am.killBackgroundProcesses(packageName);
        }
        Process.killProcess(Process.myPid());
        System.exit(0);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_data);
        ButterKnife.bind(this);
        String s = CompatIni.readIni("ORG", "NUM");
        Compat.WriteIni("DEBUG", "LOG", "1");
//        CompatIni.WriteIni("DEBUG", "SAVEFRAME","1");
        startService(new Intent(this, LocationUpService.class));
        groupBaseAdapter = new GroupBaseAdapter(this);
        expandableListView.setAdapter(groupBaseAdapter);
        if (PersonCtrl.mGroupData.size() > 0)
            CallHelper.CURRENT_GROUP_NUM = PersonCtrl.mGroupData.get(0).getUcNum();
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                UserGroupData group = (UserGroupData) groupBaseAdapter.getGroup(groupPosition);
                group.setFocused(true);
                CallHelper.CURRENT_GROUP_NUM = group.getUcNum();
//                CallHelper.CURRENT_GROUP_NUM = group.getUcNum();
                for (int i = 0; i < groupBaseAdapter.getGroupCount(); i++) {
                    if (groupPosition != i) {
                        // ????????????????????????????????????????????????
                        ((UserGroupData) groupBaseAdapter.getGroup(i)).setFocused(false);
                        expandableListView.collapseGroup(i);
                    }
                }
                expandableListView.requestLayout();
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(C.Receiver.PRESS_PTT_KEY);
        filter.addAction(C.Receiver.RELEASE_PTT_KEY);
        filter.addAction(C.Receiver.PRESS_PTT_KEY28);
        filter.addAction(C.Receiver.RELEASE_PTT_KEY28);
        filter.addAction(C.Receiver.PRESS_PTT_KEY2);
        filter.addAction(C.Receiver.RELEASE_PTT_KEY2);
        registerReceiver(broadcastReceiver, filter);

        receiver = new MyReceiver();
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter2);
    }

    /**
     * ??????ptt??????
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (C.Receiver.PRESS_PTT_KEY.equals(intent.getAction()) || C.Receiver.PRESS_PTT_KEY28.equals(intent.getAction())
                    || C.Receiver.PRESS_PTT_KEY2.equals(intent.getAction())) {
                int callId = CallHelper.getInstance(context).callGroup(CallHelper.CURRENT_GROUP_NUM);
                if (callId == MediaState.CAUSE_RESOURCE_UNAVAIL) {
                    Toast.makeText(context, context.getResources().getString(R.string.Call_collision), Toast.LENGTH_SHORT).show();
                }
            } else if (C.Receiver.RELEASE_PTT_KEY.equals(intent.getAction()) || C.Receiver.RELEASE_PTT_KEY28.equals(intent.getAction())
                    || C.Receiver.RELEASE_PTT_KEY2.equals(intent.getAction())) {
                CSAudio.getInstance().groupMicControl(false);
            }
        }
    };

    @Override
    public void onGetData(String data, int type) {
        super.onGetData(data, type);
        if (type == IDTMsgType.USER_STATUS) {
            //TODO ???????????????????????????
            UserStatusEntity userStatusEntity = new Gson().fromJson(data, UserStatusEntity.class);
            updateUserStatus(data);
            groupBaseAdapter.notifyDataSetChanged();
        } else if (type == IDTMsgType.OAM_GROUP_OR_USER) {
            //TODO ??????????????????????????????????????????
            oamModifyGroupData(data);
        } else if (type == IDTMsgType.TIME_DELAY) {
            //???????????????????????????
            Integer integer = new Gson().fromJson(data, Integer.class);
            LogUtil.INSTANCE.e("????????????????????????:" + integer);
        } else if (type == IDTMsgType.GROUP_CALL_SPEAK_TIP) {//???????????????
            //TODO ???????????????
            speakTip(data);
        } else if (type == IDTMsgType.CALL_IN) {
            CallInEntity callInEntity = new Gson().fromJson(data, CallInEntity.class);
            //TODO ???????????? ???????????????
        } else if (type == IDTMsgType.CALL_RELEASE) {
            //TODO ???????????????????????????????????????????????????
            CallReleaseEntity callReleaseEntity = new Gson().fromJson(data, CallReleaseEntity.class);
        } else if (type == IDTMsgType.LOGIN) {
            LoginResult loginResult = new Gson().fromJson(data, LoginResult.class);
            if (UiCauseConstants.CAUSE_DUP_REG == loginResult.getState()) {
                toast("????????????????????????");
                IdtLib.clear();
                finish();
            }
        } else if (type == IDTMsgType.QUERY_GROUP_OVER) {
            //TODO ?????????????????????
            groupBaseAdapter.notifyDataSetChanged();
        } else if (type == IDTMsgType.MESIA_STREAN_STATISTICS) {
            //TODO ?????????????????????
            MediaStreamEntity mediaStreamEntity = new Gson().fromJson(data, MediaStreamEntity.class);
        } else if (type == IDTMsgType.LOAD_GROUP_MEMBER) {
            UserGroupData userGroupData = new Gson().fromJson(data, UserGroupData.class);
            if (userGroupData.getUcNum().equals("0")) {
                List<Member.MemberBean> memberBeen = PersonCtrl.mNodeGroupData.getMemberBeen();
                if (memberBeen != null) {
                    toast("???????????????: " + memberBeen.size());
                }
            }
        }
    }

    private void updateUserStatus(String data) {
        UserStatusEntity userStatusEntity = new Gson().fromJson(data, UserStatusEntity.class);
        for (int i = 0; i < PersonCtrl.mGroupData.size(); i++) {
            List<Member.MemberBean> memberBeen = PersonCtrl.mGroupData.get(i).getMemberBeen();
            for (int i1 = 0; i1 < memberBeen.size(); i1++) {
                if (memberBeen.get(i1).getNum().equals(userStatusEntity.getUcNum())) {
                    memberBeen.get(i1).setStatus(userStatusEntity.getiStatus());
                    break;
                }
            }
        }
    }

    private void speakTip(String data) {
        //???????????????
        CallTalkingEntity callTalkingEntity = new Gson().fromJson(data, CallTalkingEntity.class);
        String speakNameString = callTalkingEntity.getSpeakName();
        String speakNum = callTalkingEntity.getSpeakNum();
        long pUsrCtx = callTalkingEntity.getpUsrCtx();
        if (speakNameString == null || "".equals(speakNameString)) {
            CallHelper.CURRENT_GROUP_CALL_STATE = getResources().getString(R.string.now_take_user);
            groupName.setText(getResources().getString(R.string.intercom_group_name) + CallHelper.CURRENT_GROUP_NAME);
            speakName.setText(CallHelper.CURRENT_GROUP_CALL_STATE);
        } else {
            CallHelper.CURRENT_GROUP_CALL_STATE = getResources().getString(R.string.now_take_user22) + speakNameString;
            groupName.setText(getResources().getString(R.string.intercom_group_name) + CallHelper.CURRENT_GROUP_NAME);
            speakName.setText(CallHelper.CURRENT_GROUP_CALL_STATE);
        }
    }

    private void oamModifyGroupData(String data) {
        OamGroupData oamGroupData = new Gson().fromJson(data, OamGroupData.class);
        String pucGNum = oamGroupData.getPucGNum();
        String pucGName = oamGroupData.getPucGName();
        String pucUNum = oamGroupData.getPucUNum();
        String pucUName = oamGroupData.getPucUName();
        String userAccount = MyApplication.Companion.getUserAccount();
        List<UserGroupData> groupDatas = PersonCtrl.mGroupData;
        IdsLog.d("xiezhixian", pucGNum + "--" + pucUNum);
        if (oamGroupData.getDwOptCode() == 2) {// ?????????????????????,??????????????????
//            String rememberGroupNum = SharedPreferencesUtil.getStringPreference(this, C.sp.userAccount, "@#$%");
//            if (pucUNum.equals(rememberGroupNum)) {
//                SharedPreferencesUtil.setBooleanPreferences(this, C.sp.isAutoLogin, false);
//                mGroupData.clear();
//                IDSApiProxyMgr.getCurProxy().Exit();
//                IDSApiProxyMgr.getCurProxy().unloadLibrary(this);
//                CustomActivityManager.clearAllActivity();
//                Intent intent = new Intent(this, LoginActivity.class);
//                this.startActivity(intent);
//            }
        } else if (oamGroupData.getDwOptCode() == 6) {// ??????????????????????????????????????????????????????
            for (int i = 0; i < PersonCtrl.mGroupData.size(); i++) {
                if (groupDatas.get(i).getUcNum().equals(pucGNum)) {// ??????????????????==????????????
                    groupDatas.remove(i);
                    return;
                }
            }
        } else if (oamGroupData.getDwOptCode() == 7) {//???????????????  ???????????????
            for (int i = 0; i < groupDatas.size(); i++) {
                if (groupDatas.get(i).getUcNum().equals(pucGNum)) {// ??????????????????==????????????
                    groupDatas.get(i).setUcName(pucGName);
                    return;
                }
            }
        } else if (oamGroupData.getDwOptCode() == 9) {// ?????????????????????????????????  ??????
//            IDSApiProxyMgr.getCurProxy().IDT_GQueryU(C.IDTCode.MODIFY_GROUP_DATA, pucGNum, 1, 0, 0);
        } else if (oamGroupData.getDwOptCode() == 10) {// ?????????????????????????????????  // ?????????
            for (int i = 0; i < groupDatas.size(); i++) {
                if (groupDatas.get(i).getUcNum().equals(pucGNum)) {// ??????????????????==????????????
                    List<Member.MemberBean> groupMembers = groupDatas.get(i).getMemberBeen();
                    for (int i1 = 0; i1 < groupMembers.size(); i1++) {
                        if (groupMembers.get(i1).getNum().equals(pucUNum) && pucUNum.equals(userAccount)) {
                            groupMembers.remove(i1);
                            groupDatas.remove(i);
                            break;
                        } else if (groupMembers.get(i1).getNum().equals(pucUNum)) {
                            groupMembers.remove(i1);
                            break;
                        }
                    }
                }
            }
        } else if (oamGroupData.getDwOptCode() == 11) {// ????????????????????????
            for (int i = 0; i < groupDatas.size(); i++) {
                List<Member.MemberBean> groupMembers = groupDatas.get(i).getMemberBeen();
                if (groupMembers != null) {
                    for (Member.MemberBean changeGroup : groupMembers) {
                        if (changeGroup.getNum().equals(pucUNum) && !changeGroup.getName().equals(pucUName)) {
                            changeGroup.setName(pucUName);
                            return;
                        }
                    }
                }
            }
        }
    }

    @OnClick({R.id.addGroup, R.id.searchGroup, R.id.sosTextView})
    public void onViewClicked(View view) {
        Intent intent = new Intent(this, BulidGroupActivity.class);
        switch (view.getId()) {
            case R.id.addGroup:
                startActivity(intent);
                break;
            case R.id.searchGroup:
                IDTApi.IDT_GQueryU(C.IDTCode.QueryNodeGroupData, "0", 0, 1, 0);
                break;
            case R.id.sosTextView:
                SoSUtils.SOS(this);
                break;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }
}
