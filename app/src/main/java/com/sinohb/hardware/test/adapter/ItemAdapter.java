package com.sinohb.hardware.test.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.entities.TestItem;
import com.sinohb.hardware.test.task.BaseTestTask;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {

    private LayoutInflater mInflater;
    private List<TestItem> items;
    private ItemOnclickListener listener;

    public ItemAdapter(Context context, List<TestItem> items, ItemOnclickListener listener) {
        mInflater = LayoutInflater.from(context);
        this.items = items;
        this.listener = listener;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.view_test_item, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        TestItem testItem = items.get(position);
        holder.titleTv.setText(testItem.getItemTitle());
        holder.iconIv.setImageResource(testItem.getIcon());
//        if (testItem.isFinish()){
//            holder.passIv.setVisibility(View.VISIBLE);
//        }else {
//            holder.passIv.setVisibility(View.INVISIBLE);
//        }
//        if (testItem.isSuccess()) {
//            holder.passIv.setImageResource(R.mipmap.ic_item_test_pass);
//        } else {
//            holder.passIv.setImageResource(R.mipmap.ic_item_test_unpass);
//        }
        switch (testItem.getTestState()) {
            case BaseTestTask.STATE_NONE:
                holder.passIv.setVisibility(View.INVISIBLE);
                break;
            case BaseTestTask.STATE_RUNNING:
            case BaseTestTask.STATE_TEST_WAIT_OPERATE:
            case BaseTestTask.STATE_STEP_FINSH:
                holder.passIv.setVisibility(View.VISIBLE);
                holder.passIv.setImageResource(R.drawable.ic_test_running_amain);
                AnimationDrawable anim = (AnimationDrawable) holder.passIv.getDrawable();
                if (anim != null) {
                    anim.start();
                }
                break;
            case BaseTestTask.STATE_FINISH:
                holder.passIv.setVisibility(View.VISIBLE);
                stopAnima(holder.passIv);
                holder.passIv.setImageResource(R.mipmap.ic_item_test_pass);
                break;
            case BaseTestTask.STATE_TEST_UNPASS:
                holder.passIv.setVisibility(View.VISIBLE);
                stopAnima(holder.passIv);
                holder.passIv.setImageResource(R.mipmap.ic_item_test_unpass);
                break;
        }
        if (testItem.isSelect()) {
            holder.selectedView.setVisibility(View.VISIBLE);
        } else {
            holder.selectedView.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setSelected(testItem.isSelect());
        holder.itemView.setOnClickListener(new MyClickListener(position));
    }

    private void stopAnima(ImageView imageView) {
        Drawable anim1 = imageView.getDrawable();
        if (anim1 != null && (anim1 instanceof AnimationDrawable)) {
            ((AnimationDrawable) anim1).stop();
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    class MyClickListener implements View.OnClickListener {
        private int position;
        // private WeakReference<ItemAdapter> weakReference;

        MyClickListener(int position/**, ItemAdapter adapter**/) {
            this.position = position;
            //weakReference = new WeakReference<>(adapter);
        }

        @Override
        public void onClick(View v) {
//            if (weakReference != null && weakReference.get() != null && weakReference.get().listener != null) {
//                weakReference.get().listener.onItemClick(weakReference.get(), position);
//            }
            if (listener != null) {
                listener.onItemClick(ItemAdapter.this, position);
            }
        }
    }

    public interface ItemOnclickListener {
        void onItemClick(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter, int position);
    }
}
