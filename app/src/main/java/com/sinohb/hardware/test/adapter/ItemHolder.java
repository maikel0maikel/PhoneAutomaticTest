package com.sinohb.hardware.test.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinohb.hardware.test.R;

public class ItemHolder extends RecyclerView.ViewHolder {
    View selectedView;
    ImageView iconIv;
    TextView titleTv;
    ImageView passIv;
    TextView diverView;

    public ItemHolder(View itemView) {
        super(itemView);
        iconIv = (ImageView) itemView.findViewById(R.id.icon_iv);
        titleTv = (TextView) itemView.findViewById(R.id.title_tv);
        passIv = (ImageView) itemView.findViewById(R.id.pass_state_iv);
        selectedView = itemView.findViewById(R.id.selected_v);
        diverView = (TextView) itemView.findViewById(R.id.diver_v);
    }


}
