package com.sinohb.hardware.test.app.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.adapter.ItemAdapter;
import com.sinohb.hardware.test.app.BaseFragment;
import com.sinohb.hardware.test.app.KeyListener;
import com.sinohb.hardware.test.constant.BluetoothConstants;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.WifiConstants;
import com.sinohb.hardware.test.entities.ConfigEntity;
import com.sinohb.hardware.test.entities.TestItem;
import com.sinohb.hardware.test.module.frc.RFCFactory;
import com.sinohb.hardware.test.module.main.MainController;
import com.sinohb.hardware.test.module.main.MainPresenter;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.hardware.test.utils.JsonUtils;
import com.sinohb.hardware.test.widget.ExitDialog;
import com.sinohb.hardware.test.widget.ExitProgressDialog;
import com.sinohb.logger.LogTools;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements MainPresenter.View, View.OnClickListener {
    private static final String TAG = "MainActivity";
    private MainPresenter.Controller mPresenter;
    private int defaultPosition = -1;
    private BaseFragment currentFragment;
    private static final String SAVED_FRAGMENT_INDEX = "fragment.index";
    private ImageView auxIv;
    private ImageView tfIv;
    private ImageView usbIv;
    private ImageView gpsIv;
    private ImageView btIv;
    private ImageView wifiIv;
    private ItemAdapter mAdapter;
    private RecyclerView recyclerView;
    private TextView version_tv;
    private List<KeyListener> keyListeners = new ArrayList<>();
    private TextView startTaskBtn;
    private TextView stopTaskBtn;
    private ExitDialog dialog;
    private ExitProgressDialog progressDialog;
    private MainHandler mHandler;

    static class ItemOnclickListener implements ItemAdapter.ItemOnclickListener {
        WeakReference<MainActivity> weakReference;

        ItemOnclickListener(MainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onItemClick(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter, int position) {
            if (weakReference != null && weakReference.get() != null) {
                weakReference.get().showFragment(position);
            } else {
                LogTools.e(TAG, "onItemClick weakReference is null and weakReference.get() is null");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setBackgroundDrawable(null);
        super.onCreate(savedInstanceState);
        new MainController(this);
        setContentView(R.layout.activity_main);
        mHandler = new MainHandler(this);
        mPresenter.initFragments(savedInstanceState);
        initView();
        if (savedInstanceState != null) {
            defaultPosition = savedInstanceState.getInt(SAVED_FRAGMENT_INDEX, 0);
        }
        //showFragment(defaultPosition);
        LogTools.p(TAG,"json:"+ JsonUtils.toJson(new ConfigEntity()));
        mPresenter.start();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.test_items_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new ItemAdapter(this, mPresenter.getTestItems(), new ItemOnclickListener(this));
        recyclerView.setAdapter(mAdapter);

        auxIv = (ImageView) findViewById(R.id.aux_iv);
        tfIv = (ImageView) findViewById(R.id.tf_iv);
        usbIv = (ImageView) findViewById(R.id.usb_iv);
        gpsIv = (ImageView) findViewById(R.id.gps_iv);
        btIv = (ImageView) findViewById(R.id.bluetooth_iv);
        wifiIv = (ImageView) findViewById(R.id.wifi_iv);
        version_tv = (TextView) findViewById(R.id.version_tv);

        startTaskBtn = (TextView) findViewById(R.id.start_task_tv);
        stopTaskBtn = (TextView) findViewById(R.id.stop_task_tv);
        startTaskBtn.setOnClickListener(this);
        stopTaskBtn.setOnClickListener(this);
        findViewById(R.id.layout_back).setOnClickListener(this);
        startTaskBtn.setEnabled(false);
        stopTaskBtn.setEnabled(false);
    }


//    private void restoreFragment() {
//        int size = mPresenter.getItemsCount();
//        FragmentManager fragmentManager = getFramentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        for (int i = 0; i < size; i++) {
//            BaseFragment fragment = mPresenter.getTestItem(i).getFragment();
//            if (i == defaultPosition) {
//                transaction.show(fragment);
//                currentFragment = fragment;
//            } else {
//                transaction.hide(fragment);
//            }
//        }
//        transaction.commitAllowingStateLoss();
//    }

    public void showFragment(int index) {
        if (defaultPosition == index) {
            return;
        }
        notifyItem(index);

        FragmentManager fragmentManager = getFramentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        BaseFragment fragment = mPresenter.getTestItem(index).getFragment();
        if (fragment == null || fragment == currentFragment) {
            return;
        }
        if (fragment.isAdded()) {
            transaction.hide(currentFragment).show(fragment);
        } else {
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.content, fragment, "TAG" + index);
        }

        transaction.commitAllowingStateLoss();
        defaultPosition = index;
        currentFragment = fragment;
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        manager.scrollToPosition(index);
    }

    public void notifyTaskTestState(int pos) {
        if (mPresenter == null) return;
        BaseFragment fragment = mPresenter.getTestItem(pos).getFragment();
        if (fragment == null) {
            return;
        }
        TestItem item = mPresenter.getTestItem(pos);
        BaseTestTask task = fragment.getPresenter().getTask();
        item.setTestState(task.getmExecuteState());
        if (mAdapter != null) {
            mAdapter.notifyItemChanged(pos);
        }

    }

    private void notifyItem(int index) {
        if (index < 0) {
            return;
        }
        TestItem item;
        if (defaultPosition >= 0) {
            item = mPresenter.getTestItem(defaultPosition);
            item.setSelect(false);
            mAdapter.notifyItemChanged(defaultPosition);
        }
        defaultPosition = index;
        item = mPresenter.getTestItem(defaultPosition);
        item.setSelect(true);
        mAdapter.notifyItemChanged(defaultPosition);
    }

    @Override
    public void setPresenter(MainPresenter.Controller presenter) {
        mPresenter = presenter;
    }


    @Override
    public FragmentManager getFramentManager() {
        return getSupportFragmentManager();
    }

    @Override
    public void notifyItemTaskFinish(BaseTestTask task, int testType) {
        if (mPresenter != null) {
            mPresenter.notifyItemTaskFinish(task, testType);
//            if (task.isManaul()&&mPresenter.isTaskComplete()){
//                notifyStartBtn();
//            }
            if (!mPresenter.hasTaskExecuting()) {
                notifyStartBtn();
            }
        }

    }

    @Override
    public void notifyStopBtn() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_MAIN_NOTIFY_STOP_BTN);
        }
    }

    @Override
    public void notifyStartBtn() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_MAIN_NOTIFY_START_BTN);
        }
    }

    @Override
    public void exit() {
        if (mPresenter != null) {
            mPresenter.exit();
            showProgressDialog();
        } else {
            finish();
        }
    }

    @Override
    public void destroyView() {
        finish();
    }

    @Override
    public void notifyTaskStart() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_MAIN_NOTIFY_TASK_START);
        }
    }

    public void setBtnEnable() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_MAIN_ENABLE_BTN);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_FRAGMENT_INDEX, defaultPosition);
    }

    private void showDialog() {
        if (dialog == null) {
            dialog = new ExitDialog(this);
        }
        if (!dialog.isShowing() && !isFinishing()) {
            dialog.show();
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ExitProgressDialog(this);
        }
        if (!progressDialog.isShowing() && !isFinishing()) {
            progressDialog.show();
        }
    }

    private void dismissDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void freshBtState(int state) {
        if (btIv == null) {
            LogTools.p(TAG, "freshBtState btIv is null");
            return;
        }
        switch (state) {
            case BluetoothConstants.OpenState.STATE_TURNED_OFF:
                btIv.setImageResource(R.mipmap.ic_bluetooth_bar_closed);
                break;
            case BluetoothConstants.OpenState.STATE_TURNED_ON:
                btIv.setImageResource(R.mipmap.ic_bluetooth_bar_opened);
                break;
            default:
                btIv.setImageResource(R.mipmap.ic_bluetooth_bar_closed);
                break;
        }
    }

    public void freshWifiState(int state) {
        if (wifiIv == null) {
            LogTools.p(TAG, "freshWifiState wifiIv is null");
            return;
        }
        switch (state) {
            case WifiConstants.OpenOrCloseState.STATE_OPENED:
                wifiIv.setImageResource(R.mipmap.ic_wifi_bar_opened);
                break;
            case WifiConstants.OpenOrCloseState.STATE_CLOSED:
                wifiIv.setImageResource(R.mipmap.ic_wifi_bar_closed);
                break;
            default:
                wifiIv.setImageResource(R.mipmap.ic_wifi_bar_closed);
                break;
        }
    }

    public void setSerialVersion(String version) {
        if (version_tv != null) {
            version_tv.setText(version);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        keyListeners.clear();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
        dismissDialog(dialog);
        dismissDialog(progressDialog);
        progressDialog = null;
        dialog = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        recyclerView.removeAllViews();
        mAdapter = null;
        mPresenter = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        for (KeyListener listener : keyListeners) {
            listener.onKeyDown(keyCode, event);
        }
        return true;
    }


    public void addKeyListener(KeyListener listener) {
        if (keyListeners != null) {
            keyListeners.add(listener);
        }
    }

    public boolean removeKeyListener(KeyListener listener) {
        return keyListeners != null && keyListeners.remove(listener);
    }

    public void setAuxStatusImg(final int state) {
        if (mHandler != null) {
            mHandler.obtainMessage(Constants.HandlerMsg.MSG_MAIN_AUX_BAR_IMG, state).sendToTarget();
        }
    }

    public void setUSBStatusImg(final int state) {
        if (mHandler != null) {
            mHandler.obtainMessage(Constants.HandlerMsg.MSG_MAIN_USB_BAR_IMG, state).sendToTarget();
        }
    }

    public void setGPSStatusImg(final int state) {
        if (state == 1 && gpsIv != null) {
            gpsIv.setImageResource(R.mipmap.ic_gps_bar_opened);
        } else if (gpsIv != null) {
            gpsIv.setImageResource(R.mipmap.ic_gps_bar_closed);
        }
    }

    public void setTFStatusImg(final int state) {
        if (mHandler != null) {
            mHandler.obtainMessage(Constants.HandlerMsg.MSG_MAIN_TF_BAR_IMG, state).sendToTarget();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                if (mPresenter != null && mPresenter.hasTaskExecuting()) {
                    showDialog();
                } else if (mPresenter != null) {
                    showProgressDialog();
                    mPresenter.saveLog();
                } else {
                    finish();
                }
                break;
            case R.id.start_task_tv:
                if (mPresenter != null) {
                    v.setEnabled(false);
                    mPresenter.start();
                }
                break;
            case R.id.stop_task_tv:
                if (mPresenter != null) {
                    mPresenter.stop();
                    v.setEnabled(false);
                }
                break;
        }
    }

    static class MainHandler extends Handler {
        private WeakReference<MainActivity> weakReference;

        MainHandler(MainActivity mainActivity) {
            weakReference = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (weakReference == null) {
                return;
            }
            MainActivity activity = weakReference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case Constants.HandlerMsg.MSG_MAIN_NOTIFY_STOP_BTN:
                    if (activity.stopTaskBtn != null)
                        activity.stopTaskBtn.setEnabled(true);
                    if (activity.startTaskBtn != null)
                        activity.startTaskBtn.setEnabled(false);
                    break;
                case Constants.HandlerMsg.MSG_MAIN_NOTIFY_START_BTN:
                    if (activity.stopTaskBtn != null)
                        activity.stopTaskBtn.setEnabled(false);
                    if (activity.startTaskBtn != null)
                        activity.startTaskBtn.setEnabled(true);
                    break;
                case Constants.HandlerMsg.MSG_MAIN_ENABLE_BTN:
                    if (activity.startTaskBtn != null)
                        activity.startTaskBtn.setEnabled(false);
                    if (activity.stopTaskBtn != null)
                        activity.stopTaskBtn.setEnabled(false);
                    break;
                case Constants.HandlerMsg.MSG_MAIN_AUX_BAR_IMG:
                    int auxState = (int) msg.obj;
                    if (auxState == 1 && activity.auxIv != null) {
                        activity.auxIv.setImageResource(R.mipmap.ic_aux_bar_opened);
                    } else if (activity.auxIv != null) {
                        activity.auxIv.setImageResource(R.mipmap.ic_aux_bar_closed);
                    }
                    break;
                case Constants.HandlerMsg.MSG_MAIN_USB_BAR_IMG:
                    int usbState = (int) msg.obj;
                    if (usbState == 1 && activity.usbIv != null) {
                        activity.usbIv.setImageResource(R.mipmap.ic_usb_bar_opened);
                    } else if (activity.usbIv != null) {
                        activity.usbIv.setImageResource(R.mipmap.ic_usb_bar_closed);
                    }
                    break;
                case Constants.HandlerMsg.MSG_MAIN_TF_BAR_IMG:
                    int tfState = (int) msg.obj;
                    if (tfState == 1 && activity.tfIv != null) {
                        activity.tfIv.setImageResource(R.mipmap.ic_tf_bar_opened);
                    } else if (activity.tfIv != null) {
                        activity.tfIv.setImageResource(R.mipmap.ic_tf_bar_closed);
                    }
                case Constants.HandlerMsg.MSG_MAIN_NOTIFY_TASK_START:
                    if (activity.mAdapter != null) {
                        activity.mAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    }
}
