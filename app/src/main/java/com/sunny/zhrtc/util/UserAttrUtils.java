package com.sunny.zhrtc.util;

import android.text.TextUtils;

import com.ids.idtma.jni.aidl.Member;
import com.ids.idtma.jni.aidl.UserGroupData;
import com.ids.idtma.person.PersonCtrl;

import java.util.List;


/**
 * 通过用户账号获取用户详细属性
 */

public class UserAttrUtils {

    public static String getName(String num) {
        if (TextUtils.isEmpty(num)) return "";
        if (num.contains("#")) {
            return searchGroupName(num);
        }
        List<UserGroupData> groupDatas = PersonCtrl.mGroupData;
//        if (ProjectConfig.DisplayTreeNodeData) {
//            groupDatas = PersonCtrl.mAllGroupData;
//        }
        for (int i = 0; i < groupDatas.size(); i++) {
            UserGroupData userGroupData = groupDatas.get(i);
            if (userGroupData.getUcNum().equals(num)) {
                return userGroupData.getUcName();
            }
            List<Member.MemberBean> memberBeen = groupDatas.get(i).getMemberBeen();
            if (memberBeen != null) {
                for (int i1 = 0; i1 < memberBeen.size(); i1++) {
                    if (memberBeen.get(i1).getNum().equals(num)) {
                        return memberBeen.get(i1).getName();
                    }
                }
            }

        }
        return num;
    }

    public static UserGroupData getUserGroupData(String num) {
        List<UserGroupData> groupDatas = PersonCtrl.mGroupData;
//        if (ProjectConfig.DisplayTreeNodeData) {
//            groupDatas = PersonCtrl.mAllGroupData;
//        }
        for (int i = 0; i < groupDatas.size(); i++) {
            UserGroupData userGroupData = groupDatas.get(i);
            if (userGroupData.getUcNum().equals(num)) {
                return userGroupData;
            }
        }
        return null;
    }

    public static Member.MemberBean getUser(String userAccount) {
        if (PersonCtrl.mGroupData != null && PersonCtrl.mGroupData.size() > 0) {
            for (int i = 0; i < PersonCtrl.mGroupData.size(); i++) {
                if (PersonCtrl.mGroupData.get(i).getMemberBeen() != null) {
                    for (int i1 = 0; i1 < PersonCtrl.mGroupData.get(i).getMemberBeen().size(); i1++) {
                        if (userAccount.equals(PersonCtrl.mGroupData.get(i).getMemberBeen().get(i1).getNum()))
                            return PersonCtrl.mGroupData.get(i).getMemberBeen().get(i1);
                    }
                }
            }
        } else {
            if (PersonCtrl.mNodeMemberData.getMemberBeen() != null) {
                for (Member.MemberBean memberBean : PersonCtrl.mNodeMemberData.getMemberBeen()) {
                    if (userAccount.equals(memberBean.getNum())) {
                        return memberBean;
                    }
                }
            }
        }

        return null;
    }


    public static String searchGroupName(String groupNum) {
        if (groupNum.contains("#")) {
            groupNum = groupNum.replace("#", "");
        }
        if (PersonCtrl.mGroupData == null)
            return "";
        for (int i = 0; i < PersonCtrl.mGroupData.size(); i++) {
            if (groupNum.equals(PersonCtrl.mGroupData.get(i).getUcNum())) {
                return PersonCtrl.mGroupData.get(i).getUcName();
            }
        }
        return "";
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    private static String number;

    private static UserAttrUtils userAttrUtils;

    public static UserAttrUtils getInstance() {
        if (userAttrUtils == null) {
            return new UserAttrUtils();
        }
        return userAttrUtils;
    }
}
