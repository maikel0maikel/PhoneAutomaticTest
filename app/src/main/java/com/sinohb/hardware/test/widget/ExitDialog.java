package com.sinohb.hardware.test.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.module.main.MainPresenter;

public class ExitDialog extends Dialog implements View.OnClickListener{
    private Context context;
    public ExitDialog(@NonNull Context context) {
        super(context,R.style.exit_dialog);
        init();
        this.context = context;
    }

    public ExitDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected ExitDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        View contentView = getLayoutInflater().inflate(R.layout.view_dialog, null);
        contentView.findViewById(R.id.dialog_ok_btn).setOnClickListener(this);
        contentView.findViewById(R.id.dialog_cancel_btn).setOnClickListener(this);
        setContentView(contentView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_ok_btn:
                dismiss();
                if (context instanceof MainPresenter.View){
                    ((MainPresenter.View)context).exit();
                }
                break;
            case R.id.dialog_cancel_btn:
                dismiss();
                break;
        }
    }
}
