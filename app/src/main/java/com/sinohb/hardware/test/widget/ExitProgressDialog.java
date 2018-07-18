package com.sinohb.hardware.test.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.sinohb.hardware.test.R;

public class ExitProgressDialog extends Dialog {
    public ExitProgressDialog(@NonNull Context context) {
        super(context,R.style.exit_dialog);
        init();
    }

    public ExitProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected ExitProgressDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        View contentView = getLayoutInflater().inflate(R.layout.view_progress_dialog, null);
        setContentView(contentView);
        setCancelable(false);
    }


}
