package com.sinohb.hardware.test.app.fragment;


import android.os.Message;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseAutomaticFragment;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.gps.GPSController;
import com.sinohb.hardware.test.module.gps.GPSPresenter;
import com.sinohb.hardware.test.task.BaseTestTask;

public class GPSFragment extends BaseAutomaticFragment  implements GPSPresenter.View{
    public static GPSFragment newInstance() {

        return new GPSFragment();
    }
    @Override
    protected void init() {
        testType = BaseTestTask.AUTOMATIC;
        if (mPresenter == null) {
            mHandler = new GpsHandler(this);
            new GPSController(this);
        }
    }
    public GPSFragment(){
        init();
    }

    @Override
    public void startLocate() {
        if (mHandler!=null){
            mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_GPS_START_LOCATE);
        }

    }

    @Override
    public void stopLocate() {
        if (mHandler!=null){
            mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_GPS_STOP_LOCATE);
        }
    }

    @Override
    public void notifyGPSState(final int state) {
        if (mHandler!=null){
            mHandler.obtainMessage(Constants.HandlerMsg.MSG_GPS_STATUS,state).sendToTarget();
        }
    }

    @Override
    public void notifyGpsStartLocate() {
        if (mHandler!=null){
            mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_GPS_START_LOCATE_NOTIFY);
        }
    }

    @Override
    public void notifyGpsStopLocate() {
        if (mHandler!=null){
            mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_GPS_STOP_LOCATE_NOTIFY);
        }
    }

    @Override
    public void notifyLocateResult(double lat, double lon, int st) {
//        if (mHandler!=null){
//            mHandler.obtainMessage(Constants.HandlerMsg.MSG_GPS_LOCATE_RESULT,lat,lon,st);
//        }
    }

    private static class GpsHandler extends  BaseAutomaticHandler{

        public GpsHandler(BaseAutomaticFragment controller) {
            super(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (weakReference == null) {
                return;
            }
            GPSFragment fragment = (GPSFragment) weakReference.get();
            if (fragment == null) {
                return;
            }
            switch (msg.what){
                case Constants.HandlerMsg.MSG_GPS_START_LOCATE:
                    ((GPSPresenter.Controller)fragment.mPresenter).startLocate();
                    break;
                case Constants.HandlerMsg.MSG_GPS_STOP_LOCATE:
                    ((GPSPresenter.Controller)fragment.mPresenter).stopLocate();
                    break;
                case Constants.HandlerMsg.MSG_GPS_STATUS:
                    if (fragment.mainActivity!=null){
                        fragment.mainActivity.setGPSStatusImg((Integer) msg.obj);
                    }
                    break;
                case Constants.HandlerMsg.MSG_GPS_START_LOCATE_NOTIFY:
                    fragment.setOperateHintText(R.string.label_gps_start_locate);
                    break;
                case Constants.HandlerMsg.MSG_GPS_STOP_LOCATE_NOTIFY:
                    fragment.setOperateHintText(R.string.label_gps_stop_locate);
                    break;
                case Constants.HandlerMsg.MSG_GPS_LOCATE_RESULT:
                    break;
            }
        }
    }
}
