package com.sunny.zhrtc.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ids.idtma.jni.IDTApi;
import com.ids.idtma.jni.aidl.GroupMember;
import com.ids.idtma.jni.aidl.IDTMsgType;
import com.ids.idtma.jni.aidl.Member;
import com.ids.idtma.jni.aidl.UserGroupData;
import com.ids.idtma.person.PersonCtrl;
import com.sunny.zhrtc.MyApplication;
import com.sunny.zhrtc.R;
import com.sunny.zhrtc.adapter.AllUserDataAdapter;
import com.sunny.zhrtc.base.BaseActivity;
import com.sunny.zhrtc.constants.C;
import com.sunny.zhrtc.constants.UiCauseConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * app用户建组
 *
 * @author Administrator
 */
public class BulidGroupActivity extends BaseActivity {
    @BindView(R.id.return_button)
    ImageView returnButton;
    @BindView(R.id.add)
    TextView add;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.groupNum)
    TextView groupNum;
    @BindView(R.id.groupName)
    EditText groupName;
    @BindView(R.id.list_view_user_list)
    ListView listViewUserList;
    @BindView(R.id.txt_dialog)
    TextView txtDialog;
    //    @BindView(R.id.sild_bar)
//    SideBar sildBar;
    private int flag = 0;//0表示建组1表示添加用户
    //    private String myPhoneNumber;
    private List<Member.MemberBean> memberBeans = new ArrayList<>();
    //    private PaiXuComparator.PinyinComparator comparator = new PaiXuComparator.PinyinComparator();
    private AllUserDataAdapter adapter;
    private GroupMember groupMember;//准备建的组

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_group);
        ButterKnife.bind(this);
//        myPhoneNumber = ChatSureApplication.userAccount;

//        sildBar.setmTextDialog(txtDialog);

//        sildBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
//
//            public void onTouchingLetterChanged(String str) {
//                if (memberBeans != null) {
//                    int position = adapter.getPositionForSection(str.charAt(0));
//                    if (position != -1)
//                        listViewUserList.setSelection(position);
//                }
//            }
//        });

        listViewUserList.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listViewUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toast(((Member.MemberBean) adapter.getItem(position)).getName());
            }
        });
//
//
        String callto_group_num = getIntent().getStringExtra("AddUserToGroupNum");
        List<Member.MemberBean> groupMembers = null;
        for (UserGroupData mGroupDatum : PersonCtrl.mGroupData) {
            if (mGroupDatum.getUcNum().equals(callto_group_num)) {
                groupMembers = mGroupDatum.getMemberBeen();
            }
        }
        if (groupMembers != null) {
            flag = 1;
            UserGroupData gropuEntity = null;
            List<UserGroupData> mGroupData = PersonCtrl.mGroupData;
            for (UserGroupData groupData : mGroupData) {
                if (groupData.getUcNum().equals(callto_group_num)) {
                    gropuEntity = groupData;
                }
            }
            groupNum.setFocusable(false);
            groupName.setFocusable(false);
            groupNum.setText(callto_group_num);
            groupName.setText(gropuEntity.getUcName());
        } else {
            flag = 0;
        }
//		// 获取该节点下的所有用户
        IDTApi.IDT_GQueryU(C.IDTCode.QueryNodeData, "0", 1, 0, 0);
////        IDTApi.IDT_GQueryU(C.IDTCode.QueryNodeGroupData, "0", 0, 1, 0);

    }


    @Override
    public void onGetData(String data, int type) {
        super.onGetData(data, type);
        if (type == IDTMsgType.LOAD_GROUP_MEMBER) {
            getDodeUserData(data);
        }
    }

    @OnClick({R.id.return_button, R.id.add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.return_button:
                finish();
                break;
            case R.id.add:
//                if (Utils.isFastDoubleClick()) {
//                    return;
//                }
                if (flag == 0) {


                    groupMember = new GroupMember();
                    if (TextUtils.isEmpty(groupName.getText().toString().trim())) {
                        toast(getString(R.string.groupNameEmpty));
                        return;
                    } else {
                        groupMember.setUcName(groupName.getText().toString() + getString(R.string.buildSelf));
                    }
                    groupMember.setUcNum("");
                    groupMember.setUcPrio(7);
                    groupMember.setUcType(0);
                    IDTApi.IIDT_GAdd(C.IDTCode.AddGroup, groupMember);


                } else {
                    String s = groupNum.getText().toString();
                    UserGroupData userGroupData = null;
                    for (int i = 0; i < PersonCtrl.mGroupData.size(); i++) {
                        if (s.equals(PersonCtrl.mGroupData.get(i).getUcNum())) {
                            userGroupData = PersonCtrl.mGroupData.get(i);
                            break;
                        }
                    }
                    if (userGroupData == null) {
                        return;
                    }

                    for (int i = 0; i < memberBeans.size(); i++) {
                        if (memberBeans.get(i).isSelect()) {
                            Member.MemberBean memberBean = memberBeans.get(i);
                            GroupMember member = new GroupMember();
                            member.setUcPrio(memberBean.getPrio());
                            member.setUcName(memberBean.getName());
                            member.setUcNum(memberBean.getNum());
                            member.setUcType(memberBean.getType());
                            member.setUtType(memberBean.getUTType());
                            member.setOnline(memberBean.getStatus() == 1);
                            member.setUcAttr(memberBean.getAttr());
                            IDTApi.IIDT_GAddU(C.IDTCode.AddGroupUser, s, member);
                        }
                    }
                }
                break;
        }
    }

    public void getDodeUserData(String data) {
        UserGroupData userGroupData = new Gson().fromJson(data, UserGroupData.class);
        int dwSn = userGroupData.getDwSn();
        if (dwSn == C.IDTCode.QueryNodeData) {
            memberBeans = PersonCtrl.mNodeMemberData.getMemberBeen();
//            memberBeans = filledData(memberBeans);
//            Collections.sort(memberBeans, comparator);
            for (int i = 0; i < memberBeans.size(); i++) {
                if (memberBeans.get(i).getNum().equals(MyApplication.Companion.getUserAccount())) {
                    memberBeans.get(i).setSelect(true);
                    break;
                }
            }
            adapter = new AllUserDataAdapter(getApplicationContext(), memberBeans);
            listViewUserList.setAdapter(adapter);
        } else if (dwSn == C.IDTCode.AddGroup) {
            if (userGroupData.getwRes() == 0) {
                Toast.makeText(this, getString(R.string.buildGroupSuccess), Toast.LENGTH_SHORT).show();
                userGroupData.setUcName(groupName.getText().toString() + getString(R.string.buildSelf));
                ArrayList<Member.MemberBean> datas = new ArrayList<>();
                for (int i = 0; i < memberBeans.size(); i++) {
                    Member.MemberBean memberBean = memberBeans.get(i);
                    if (memberBean.isSelect()) {
                        GroupMember groupMember = new GroupMember(memberBean.getPrio(), memberBean.getType(), memberBean.getUTType(), memberBean.getAttr(),
                                memberBean.getNum(), memberBean.getName(), memberBean.getChanNum(), memberBean.getStatus() == 1);
                        datas.add(memberBeans.get(i));
                        IDTApi.IIDT_GAddU(C.IDTCode.AddGroupUser, userGroupData.getUcNum(), groupMember);
                    }
                }
                userGroupData.setDwNum(datas.size());
                userGroupData.setMemberBeen(datas);
                PersonCtrl.mGroupData.add(userGroupData);
                finish();
            } else {
                String dataType = new UiCauseConstants().getDataType(userGroupData.getwRes(), this);
                toast(getString(R.string.buildGroupFailed) + dataType);
            }
        } else if (dwSn == C.IDTCode.AddGroupUser) {
            if (userGroupData.getwRes() == 0) {
                List<Member.MemberBean> memberBeenData = userGroupData.getMemberBeen();
                for (int i = 0; i < memberBeans.size(); i++) {
                    if (memberBeans.get(i).isSelect()) {
                        memberBeenData.add(memberBeans.get(i));
                    }
                }
                for (int i = 0; i < PersonCtrl.mGroupData.size(); i++) {
                    boolean equals = PersonCtrl.mGroupData.get(i).getUcNum().equals(userGroupData.getUcNum());
                    if (equals) {
                        PersonCtrl.mGroupData.remove(i);
                        PersonCtrl.mGroupData.add(userGroupData);
                        break;
                    }
                }
                Toast.makeText(this, getString(R.string.addSuccess), Toast.LENGTH_SHORT).show();
            } else {
                String dataType = new UiCauseConstants().getDataType(userGroupData.getwRes(), this);
                toast(getString(R.string.addFailed) + dataType);
            }
        }
    }

//    private List<Member.MemberBean> filledData(List<Member.MemberBean> date) {
//        List<Member.MemberBean> mSortList = new ArrayList<Member.MemberBean>();
//
//        for (int i = 0; i < date.size(); i++) {
//            String ucName = date.get(i).getName();
//            if (ucName.length() < 1) {
//                continue;
//            }
//            String upperCase = ucName.substring(0, 1).toUpperCase();
//            String convert = Cn2Spell.getInstance().convert(upperCase);
//            String sortString = convert.substring(0, 1);
//            if (sortString.matches("[A-Z]")) {
//                date.get(i).setSortLetters(sortString);
//            } else {
//                date.get(i).setSortLetters("#");
//            }
//
//            mSortList.add(date.get(i));
//        }
//        return mSortList;
//
//    }
}
