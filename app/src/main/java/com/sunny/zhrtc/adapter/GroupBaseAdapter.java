package com.sunny.zhrtc.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ids.idtma.person.PersonCtrl;
import com.sunny.zhrtc.MyApplication;
import com.sunny.zhrtc.R;
import com.sunny.zhrtc.mian.IMTestActivity;

/**
 * Created by Administrator on 2018/4/24.
 */

public class GroupBaseAdapter extends BaseExpandableListAdapter {
    private Context context;

    public GroupBaseAdapter(Context context) {
        this.context = context;
    }


    @Override
    public int getGroupCount() {
        return PersonCtrl.mGroupData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return PersonCtrl.mGroupData.get(groupPosition).getMemberBeen().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return PersonCtrl.mGroupData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return PersonCtrl.mGroupData.get(groupPosition).getMemberBeen().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_idt_group_item, parent, false);
            groupHolder = new GroupHolder();
            groupHolder.arrow_image = (ImageView) convertView.findViewById(R.id.arrow_image);
            groupHolder.groupName = (TextView) convertView.findViewById(R.id.group_name);
            groupHolder.message_image = (ImageView) convertView.findViewById(R.id.message_image);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        if (PersonCtrl.mGroupData.get(groupPosition).isFocused()) {
            groupHolder.arrow_image.setImageResource(R.drawable.new_ui_group_page_arrow_open);
        } else {
            groupHolder.arrow_image.setImageResource(R.drawable.new_ui_group_page_arrow_close);
        }
        groupHolder.message_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, IMTestActivity.class);
                intent.putExtra("TARGET_NUM", PersonCtrl.mGroupData.get(groupPosition).getUcNum());
                intent.putExtra("TARGET_NAME", PersonCtrl.mGroupData.get(groupPosition).getUcName());
                intent.putExtra("TO_WHERE", MyApplication.Companion.getTO_GROUP());
                context.startActivity(intent);
            }
        });
        groupHolder.groupName.setText(PersonCtrl.mGroupData.get(groupPosition).getUcName());
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final MemberHolder memberHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.new_ui_idt_group_listview_child_item, parent, false);
            memberHolder = new MemberHolder();
            memberHolder.avatar = (ImageView) convertView.findViewById(R.id.child_avatar);
            memberHolder.memberPhone = (TextView) convertView.findViewById(R.id.child_num);
            convertView.setTag(memberHolder);
        }
        final MemberHolder holder = (MemberHolder) convertView.getTag();
        holder.memberPhone.setText(PersonCtrl.mGroupData.get(groupPosition).getMemberBeen().get(childPosition).getNum());
        if (PersonCtrl.mGroupData.get(groupPosition).getMemberBeen().get(childPosition).getStatus() == 1) {
            holder.avatar.setImageResource(R.drawable.online);
        } else {
            holder.avatar.setImageResource(R.drawable.offline);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.Companion.getUserAccount().equals(PersonCtrl.mGroupData.get(groupPosition).getMemberBeen().get(childPosition).getNum())) {
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(context, IMTestActivity.class);
                intent.putExtra("TARGET_NUM", PersonCtrl.mGroupData.get(groupPosition).getMemberBeen().get(childPosition).getNum());
                intent.putExtra("TARGET_NAME", PersonCtrl.mGroupData.get(groupPosition).getMemberBeen().get(childPosition).getName());
                intent.putExtra("TO_WHERE", MyApplication.Companion.getTO_PERSON());
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class GroupHolder {
        public ImageView arrow_image;
        public TextView groupName;
        public ImageView message_image;
    }

    class MemberHolder {
        public ImageView avatar;
        public TextView memberPhone;

    }

}
