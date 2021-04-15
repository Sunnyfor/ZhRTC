package com.sunny.zhrtc.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ids.idtma.jni.IDTApi;
import com.ids.idtma.jni.aidl.GroupMember;
import com.ids.idtma.jni.aidl.IDTMsgType;
import com.ids.idtma.jni.aidl.Member;
import com.ids.idtma.jni.aidl.OamGroupData;
import com.ids.idtma.jni.aidl.UserGroupData;
import com.ids.idtma.person.PersonCtrl;
import com.sunny.zhrtc.R;
import com.sunny.zhrtc.base.BaseActivity;
import com.sunny.zhrtc.constants.C;
import com.sunny.zhrtc.adapter.UserGridViewAdapter;

import java.util.List;

/**
 * 管理组成员activity
 */
public class GroupManageActivity extends BaseActivity implements OnClickListener {

    private EditText groupName;
    private String callto_group_num;
    private TextView more;
    private List<Member.MemberBean> memberBeen;
    private UserGroupData userGroupData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_manage);
        initData();
        initView();
    }

    private void initData() {
        callto_group_num = getIntent().getStringExtra(C.intentKeys.TARGET_NUM);
        if (callto_group_num.contains("#")) {
            callto_group_num = callto_group_num.replace("#", "");
        }
        for (UserGroupData mGroupDatum : PersonCtrl.mGroupData) {
            if (mGroupDatum.getUcNum().equals(callto_group_num)) {
                userGroupData = mGroupDatum;
                memberBeen = mGroupDatum.getMemberBeen();
                continue;
            }
        }
    }

    private void initView() {
        GridView userGridView = (GridView) findViewById(R.id.userGridView);
        groupName = (EditText) findViewById(R.id.groupNameEdit);
        TextView textViewSave = (TextView) findViewById(R.id.textViewSave);
        TextView page_title = (TextView) findViewById(R.id.page_title);
        page_title.setText("组管理");
        TextView page_title_name = (TextView) findViewById(R.id.page_title);
        more = (TextView) findViewById(R.id.more);
        more.setText(getString(R.string.delete));
        more.setOnClickListener(this);
        page_title_name.setText(userGroupData.getUcName());
        textViewSave.setOnClickListener(this);
        groupName.setText(userGroupData.getUcName());
        UserGridViewAdapter adapter = new UserGridViewAdapter(this, memberBeen, callto_group_num);
        userGridView.setAdapter(adapter);
    }

    @Override
    public void onGetData(String data, int type) {
        super.onGetData(data, type);
        if (type == IDTMsgType.OAM_GROUP_OR_USER) {
            OamGroupData oamGroupData = new Gson().fromJson(data, OamGroupData.class);
            int dwOptCode = oamGroupData.getDwOptCode();
            String pucGNum = oamGroupData.getPucGNum();
            String pucGName = oamGroupData.getPucGName();
            String pucUNum = oamGroupData.getPucUNum();
            String pucUName = oamGroupData.getPucUName();
            if (dwOptCode == 6 && pucGNum.equals(callto_group_num)) {
//                for (Activity activity : CustomActivityManager.activities) {
//                    if (activity instanceof IMActivity) {
//                        CustomActivityManager.deleteActivity(activity);
//                        break;
//                    }
//                }
                finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userGroupData != null && userGroupData.getUcName().contains("自建")) {
            more.setVisibility(View.VISIBLE);
        } else {
            more.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewSave:
                userGroupData.setUcName(groupName.getText().toString());
                GroupMember groupMember = new GroupMember();
                groupMember.setUcNum(userGroupData.getUcNum());
                groupMember.setUcType(userGroupData.getType());
                groupMember.setUcName(userGroupData.getUcName());
                groupMember.setUcPrio(userGroupData.getUcPriority());
                int i = IDTApi.IDT_GModify(C.IDTCode.ModifyGroup, groupMember);
                if (i == 0) {
                    toast(getString(R.string.modifySuccess));
                    finish();
                } else {
                    toast(getString(R.string.modifyFailed));
                }
                break;
            case R.id.more:
//                Dialogs.commonDialog(getResources().getString(R.string.kindle_reminder), getResources().getString(R.string.deleteUer2), this, new Dialogs.OnSureClickListener() {
//                    @Override
//                    public void onClick() {
                IDTApi.IDT_GDel(C.IDTCode.DeleteGroup, callto_group_num);
                more.setClickable(false);
//                    }
//                });

                break;
            default:
                break;
        }

    }
}
