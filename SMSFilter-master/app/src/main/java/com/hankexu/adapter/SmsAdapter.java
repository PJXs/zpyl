package com.hankexu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hankexu.bean.Sms;
import com.hankexu.smsfilter.R;

import java.util.ArrayList;


public class SmsAdapter extends BaseAdapter {

    private ArrayList<Sms> smsList;
    private Context context;

    public SmsAdapter(ArrayList<Sms> smsList, Context context) {
        this.smsList = smsList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return smsList.size();
    }

    @Override
    public Object getItem(int position) {
        return smsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.sms_item_cell, null);
            holder = new ViewHolder();
            holder.tvFromaddress = (TextView) convertView.findViewById(R.id.tv_fromaddress);
            holder.tvBody = (TextView) convertView.findViewById(R.id.tv_body);
            holder.tvDatetime = (TextView) convertView.findViewById(R.id.tv_datetime);
            holder.tvFromaddress.setText(smsList.get(position).getFromAddress());
            holder.tvBody.setText(smsList.get(position).getBody());
            holder.tvDatetime.setText(smsList.get(position).getDatetime());
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.tvFromaddress.setText(smsList.get(position).getFromAddress());
            holder.tvBody.setText(smsList.get(position).getBody());
            holder.tvDatetime.setText(smsList.get(position).getDatetime());
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView tvFromaddress;
        TextView tvBody;
        TextView tvDatetime;
    }
}
