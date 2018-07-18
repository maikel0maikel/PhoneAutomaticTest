package com.sinohb.hardware.test.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.entities.StepEntity;

import java.util.List;

public class TestDetailAdapter extends BaseAdapter {
    private List<StepEntity> stepEntities;

    public TestDetailAdapter(List<StepEntity> stepEntities) {
        this.stepEntities = stepEntities;
    }
    public void setStepEntities(List<StepEntity> stepEntities){
        this.stepEntities = stepEntities;
    }
    @Override
    public int getCount() {
        return stepEntities == null ? 0 : stepEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return stepEntities == null ? null : stepEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(HardwareTestApplication.getContext()).inflate(R.layout.view_test_item_detail, parent, false);
            holder = new Holder();
            holder.title = (TextView) convertView.findViewById(R.id.detail_tv);
            holder.iv = (ImageView) convertView.findViewById(R.id.detail_iv);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        StepEntity stepEntity = stepEntities.get(position);
        holder.title.setText((position + 1) + "." + stepEntity.getStepTitle());
        if (stepEntity.getTestState() == 1) {
            holder.iv.setImageResource(R.mipmap.ic_item_test_pass);
        } else {
            holder.iv.setImageResource(R.mipmap.ic_item_test_unpass);
        }
        return convertView;
    }

    class Holder {
        TextView title;
        ImageView iv;
    }

}
