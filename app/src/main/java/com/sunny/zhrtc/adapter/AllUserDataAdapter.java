package com.sunny.zhrtc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.ids.idtma.jni.aidl.Member;
import com.sunny.zhrtc.R;

import java.util.List;

public class AllUserDataAdapter extends BaseAdapter implements SectionIndexer {
    private List<Member.MemberBean> list = null;
    private Context mContext;
    private String callto_group_num;

    public AllUserDataAdapter(Context mContext, List<Member.MemberBean> list) {
        super();
        this.list = list;
        this.mContext = mContext;
    }

    public void updateListView(List<Member.MemberBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int pos) {
        return this.list.get(pos);
    }

    public long getItemId(int pos) {
        return pos;
    }

    public View getView(final int pos, View view, ViewGroup group) {
        ViewHolder viewHolder;
//		final GroupMember groupMember = ;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_user_list, null);
            viewHolder.tvName = (TextView) view.findViewById(R.id.txt_user_name);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.txt_catalog);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkBox1);
            viewHolder.user_head = (ImageView) view.findViewById(R.id.user_head);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        // get position and get the first letter
//        int section = getSectionForPosition(pos);

//        if (pos == getPositionForSection(section)) {
//            viewHolder.tvLetter.setVisibility(View.VISIBLE);
//            viewHolder.tvLetter.setText(list.get(pos).getSortLetters());
//        } else
//            viewHolder.tvLetter.setVisibility(View.GONE);

        if (list.get(pos).isSelect()) {
            viewHolder.checkBox.setChecked(true);
        } else {
            viewHolder.checkBox.setChecked(false);
        }
//        int userUtType = list.get(pos).getAttr();
        viewHolder.user_head.setImageResource(R.mipmap.ic_launcher);
//        if (userUtType == 0x10) {
//            viewHolder.user_head.setImageResource(R.drawable.diaoduyuan);
//        } else if ((userUtType == 0x12 || userUtType == 3)) {
//            viewHolder.user_head.setImageResource(R.mipmap.ic_launcher);
//        } else if ((userUtType == 0x11 || userUtType == 1|| userUtType == 0x16)) {
//            viewHolder.user_head.setImageResource(R.drawable.cust_blue_computer);
//        } else if ((userUtType == 5 || userUtType == 0x14)) {
//            viewHolder.user_head.setImageResource(R.drawable.gateway);
//        } else if (userUtType == 0x13) {
//            viewHolder.user_head.setImageResource(R.drawable.ip_telephone_blue);
//        } else if (userUtType == 0x15) {
//            viewHolder.user_head.setImageResource(R.drawable.cust_monitor_camare_blue);
//        } else if (userUtType == 0x30) {
//            viewHolder.user_head.setImageResource(R.drawable.cust_police_car_blue);
//        } else if (userUtType == 0x31) {
//            viewHolder.user_head.setImageResource(R.drawable.cust_engineering_car_blue);
//        } else if (userUtType == 0x32) {
//            viewHolder.user_head.setImageResource(R.drawable.engineering_car);
//        } else if (userUtType == 0x33) {
//            viewHolder.user_head.setImageResource(R.drawable.taxi_car);
//        } else if (userUtType == 0x34) {
//            viewHolder.user_head.setImageResource(R.drawable.truck_car);
//        } else if (userUtType == 0x35) {
//            viewHolder.user_head.setImageResource(R.drawable.police_motorcycles);
//        }else{
//            viewHolder.user_head.setImageResource(R.drawable.cust_blue_phone);
//        }
        viewHolder.checkBox.setOnClickListener(v -> {
            boolean checked = ((CheckBox) v).isChecked();
            if (checked) {
                list.get(pos).setSelect(true);
            } else {
                list.get(pos).setSelect(false);
            }
        });


        viewHolder.tvName.setText(this.list.get(pos).getName() + "/" + this.list.get(pos).getNum());

        return view;
    }

    final static class ViewHolder {
        TextView tvLetter;
        TextView tvName;
        CheckBox checkBox;
        ImageView user_head;
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section)
                return i;
        }

        return -1;
    }

    public int getSectionForPosition(int arg0) {
        return this.list.get(arg0).getSortLetters().charAt(0);
    }

    public Object[] getSections() {
        return null;
    }

    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        if (sortStr.matches("[A-Z]"))
            return sortStr;
        else
            return "#";
    }

}
