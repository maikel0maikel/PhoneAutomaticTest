package com.sinohb.hardware.test.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.module.key.KeyPresenter;
import com.sinohb.logger.LogTools;

public class KeyTestView extends Activity implements KeyPresenter.View{
    private static final String TAG = "KeyTestView";
    private KeyPresenter.Controller mPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void setPresenter(KeyPresenter.Controller presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogTools.p(TAG,"");
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_UP:
        }
        return super.onKeyDown(keyCode, event);
    }
}
