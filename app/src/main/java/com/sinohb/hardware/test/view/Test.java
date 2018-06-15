package com.sinohb.hardware.test.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sinohb.hardware.test.R;

public class Test extends Activity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.activity_main);
    }
}
