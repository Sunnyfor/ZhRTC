package com.sunny.zhrtc.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.idtma.jni.IDTApi;
import com.ids.idtma.jni.aidl.Member;
import com.sunny.zhrtc.R;
import com.sunny.zhrtc.constants.C;
import com.sunny.zhrtc.activity.BulidGroupActivity;

import java.util.ArrayList;
import java.util.List;

public class UserGridViewAdapter extends BaseAdapter {

    private Context mContext = null;
    private Activity context = null;
    private LayoutInflater mLayoutInflater = null;
    private List<Member.MemberBean> groupMembers = new ArrayList<>();

    public interface DeletePicListener {
        public void deleteUrl(String deleteUrl);
    }

    private DeletePicListener mListener;
    private boolean isDelect;
    private String callto_group_num;

    public void setmListener(DeletePicListener mListener) {
        this.mListener = mListener;
    }

    public UserGridViewAdapter(Activity context, List<Member.MemberBean> groupMemberList, String callto_group_num) {
        this.mContext = context;
        this.context = context;
        this.groupMembers.clear();
        this.groupMembers.addAll(groupMemberList);
        this.callto_group_num = callto_group_num;
        Member.MemberBean memberBean = new Member.MemberBean();
        memberBean.setNum("固定");
        groupMembers.add(memberBean);
        mLayoutInflater = context.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return groupMembers == null ? 0 : groupMembers.size();
    }

    @Override
    public Object getItem(int position) {
        return groupMembers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.gv_user_item_pic, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.pic = (ImageView) convertView.findViewById(R.id.pic);
            viewHolder.delect = (ImageButton) convertView.findViewById(R.id.delete);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.userName);
            convertView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if ("固定".equals(groupMembers.get(position).getNum())) {
            holder.pic.setImageResource(R.drawable.icon_chattype_add);
            holder.userName.setVisibility(View.INVISIBLE);
            holder.delect.setVisibility(View.GONE);
            holder.pic.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    context.finish();
                    Intent intent = new Intent(mContext, BulidGroupActivity.class);
                    intent.putExtra("AddUserToGroupNum", callto_group_num);
                    mContext.startActivity(intent);
                }
            });
            return convertView;
        }
        String name = groupMembers.get(position).getName();
        holder.userName.setText(name);
        int userUtType = groupMembers.get(position).getAttr();
        holder.pic.setImageResource(R.mipmap.cus_phone);

        if (isDelect && !"固定".equals(groupMembers.get(position).getNum())) {
            holder.delect.setVisibility(View.VISIBLE);
        }
        convertView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                notifyDataSetChanged();
                isDelect = true;
                return false;
            }
        });
        holder.delect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Member.MemberBean memberBean = groupMembers.get(position);
                groupMembers.remove(position);
                IDTApi.IIDT_GDelU(C.IDTCode.DelectUser, callto_group_num, memberBean.getNum());
                Toast.makeText(mContext, context.getResources().getString(R.string.removeUser) + memberBean.getName(), Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();

            }
        });
        return convertView;
    }

    class ViewHolder {
        private ImageView pic;
        private ImageButton delect;
        private TextView userName;
    }

}
