package com.sinohb.hardware.test.module.main;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseFragment;
import com.sinohb.hardware.test.app.activity.MainActivity;
import com.sinohb.hardware.test.app.fragment.AmplifierFragment;
import com.sinohb.hardware.test.app.fragment.AuxFragment;
import com.sinohb.hardware.test.app.fragment.BluetoothFragment;
import com.sinohb.hardware.test.app.fragment.EffectsFragment;
import com.sinohb.hardware.test.app.fragment.FanFragment;
import com.sinohb.hardware.test.app.fragment.GPSFragment;
import com.sinohb.hardware.test.app.fragment.KeyFragment;
import com.sinohb.hardware.test.app.fragment.KnobFragment;
import com.sinohb.hardware.test.app.fragment.RadioFragment;
import com.sinohb.hardware.test.app.fragment.RearViewFragment;
import com.sinohb.hardware.test.app.fragment.ScreenAdjustFragment;
import com.sinohb.hardware.test.app.fragment.ScreenBrightnessFragment;
import com.sinohb.hardware.test.app.fragment.ScreenRGBFragment;
import com.sinohb.hardware.test.app.fragment.SerialFragment;
import com.sinohb.hardware.test.app.fragment.TFFragment;
import com.sinohb.hardware.test.app.fragment.TemperatureFragment;
import com.sinohb.hardware.test.app.fragment.USBFragment;
import com.sinohb.hardware.test.app.fragment.VideoMonitorFragment;
import com.sinohb.hardware.test.app.fragment.WifiFragment;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.ConvertUtils;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.SerialCommand;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.entities.TestItem;
import com.sinohb.hardware.test.module.frc.RFCFactory;
import com.sinohb.hardware.test.module.frc.RFCSendListener;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.hardware.test.task.ThreadPool;
import com.sinohb.logger.LogTools;
import com.sinohb.logger.constant.ZoneOffset;
import com.sinohb.logger.utils.FileUtils;
import com.sinohb.logger.utils.IOUtils;
import com.sinohb.logger.utils.LogUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.FutureTask;

public class MainController implements MainPresenter.Controller, RFCSendListener {
    private static final String TAG = "MainController";
    private List<TestItem> testItems = new ArrayList<>();
    private static final int[] ICONS = {R.drawable.ic_screen_selector, R.drawable.ic_screen_adjust_selector,
            R.drawable.ic_brightness_selector, R.drawable.ic_radio_selector, R.drawable.ic_effects_selector
            , R.drawable.ic_amplifier_selector, R.drawable.ic_key_selector, R.drawable.ic_knob_selector,
            R.drawable.ic_video_selector, R.drawable.ic_rear_selector, R.drawable.ic_fan_selector,
            R.drawable.ic_bluetooth_selector, R.drawable.ic_wifi_selector, R.drawable.ic_gps_selector,
            R.drawable.ic_usb_selector, R.drawable.ic_tf_selector, R.drawable.ic_serial_selector,
            R.drawable.ic_aux_selector, R.drawable.ic_temperature_selector};

    private static int[] TITLES = {R.string.lable_screen_rgb, R.string.label_screen_calibration, R.string.lable_screen_brightness,
            R.string.lable_radio, R.string.lable_sound_effects, R.string.lable_amplifier, R.string.lable_key, R.string.lable_knob,
            R.string.lable_video_surveillance, R.string.lable_rear_view, R.string.lable_fan, R.string.label_bluetooth,
            R.string.label_wifi, R.string.label_gps, R.string.label_usb, R.string.lable_tf, R.string.lable_serial, R.string.lable_aux,
            R.string.lable_temperature};

    private BaseFragment[] fragments = {ScreenRGBFragment.newInstance(), ScreenAdjustFragment.newInstance(),
            ScreenBrightnessFragment.newInstance(), RadioFragment.newInstance(), EffectsFragment.newInstance(),
            AmplifierFragment.newInstance(), KeyFragment.newInstance(), KnobFragment.newInstance(),
            VideoMonitorFragment.newInstance(), RearViewFragment.newInstance(), FanFragment.newInstance(),
            BluetoothFragment.newInstance(), WifiFragment.newInstance(), GPSFragment.newInstance(),
            USBFragment.newInstance(), TFFragment.newInstance(), SerialFragment.newInstance(),
            AuxFragment.newInstance(), TemperatureFragment.newInstance()};

    private List<BaseTestTask> manualTasks = new ArrayList<>();
    private List<BaseTestTask> autoTasks = new ArrayList<>();
    private List<BaseTestTask> tasks = new ArrayList<>();
    private MainPresenter.View mView;

    private int manualExecuteSize = 0;
    private int completeSize = 0;
    private boolean isExit = false;
    private static final String DIR_PATH = "testLog";

    public MainController(MainPresenter.View view) {
        mView = view;
        mView.setPresenter(this);
        RFCFactory.getInstance().connectService();
    }


    @Override
    public void start() {
        manualExecuteSize = 0;
        completeSize = 0;
        isExit = false;
        ThreadPool.getPool().executeOrderTask(new Runnable() {
            @Override
            public void run() {
                stopTask();
                for (TestItem testItem : testItems) {
                    testItem.setTestState(BaseTestTask.STATE_NONE);
                }
                if (mView != null) {
                    mView.notifyTaskStart();
                }
                clearTasks(tasks);
                clearTasks(manualTasks);
                clearTasks(autoTasks);
                addTestTask();
                executeTasks();
            }
        });

    }

    private void clearTasks(List list) {
        if (!list.isEmpty()) {
            list.clear();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {
        stopTask();
    }


    @Override
    public void complete() {

    }

    @Override
    public void destroy() {
        RFCFactory.getInstance().disconnectService();
        stopTask();
        ThreadPool.getPool().destroy();
        fragments = null;
        clearList(manualTasks);
        manualTasks = null;
        clearList(autoTasks);
        autoTasks = null;
        clearList(tasks);
        tasks = null;
        clearList(testItems);
        testItems = null;
    }

    void clearList(List list) {
        if (list != null) {
            list.clear();
        }
    }

    @Override
    public void initFragments(Bundle savedInstanceState) {
        FragmentManager fragmentManager = mView.getFramentManager();
        for (int i = 0; i < ICONS.length; i++) {
            TestItem testItem = new TestItem(TITLES[i], ICONS[i]);
            BaseFragment fragment;
            if (savedInstanceState != null) {
                fragment = (BaseFragment) fragmentManager.findFragmentByTag("TAG" + i);
                if (fragment != null) {
                    testItem.setFragment(fragment);
                } else {
                    fragment = fragments[i];
                    testItem.setFragment(fragment);
                }
            } else {
                fragment = fragments[i];
                testItem.setFragment(fragment);
            }
            fragment.setPosition(i);
            fragment.setMainActivity((MainActivity) mView);
            testItems.add(testItem);
        }
    }

    @Override
    public List<TestItem> getTestItems() {
        return testItems;
    }

    @Override
    public TestItem getTestItem(int pos) {
        return testItems == null ? null : testItems.get(pos);
    }

    @Override
    public int getItemsCount() {
        return testItems == null ? 0 : testItems.size();
    }

    @Override
    public void notifyItemTaskFinish(BaseTestTask task, int testType) {
        if (task.isManaul() && isExit && mView != null) {
            mView.destroyView();
            isExit = false;
        }
        if (isTaskComplete()) {
            return;
        }
        completeSize++;
        if (testType == BaseTestTask.MANUAL) {
            manualExecuteSize++;
            if (manualExecuteSize == manualTasks.size()) {
                LogTools.p(TAG, "所有手动测试项目已经完成");
                for (BaseTestTask baseTestTask : autoTasks) {
                    if (!baseTestTask.isFinish()) {
                        baseTestTask.setmExecuteState(BaseTestTask.STATE_TEST_UNPASS);
                        LogTools.p(TAG, "任务未完成:" + baseTestTask.toString());
                    }
                }
                if (mView != null) {
                    mView.notifyStartBtn();
                }
            }
        }
        if (isTaskComplete()) {
            transcatResult();
            if (isExit) {
                saveLog();
                isExit = false;
            }
        }
    }

    private void transcatResult() {
        if (tasks == null || tasks.isEmpty()) {
            LogTools.p(TAG, "transcatResult tasks is null or isEmpty");
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("{\"CommandArray\": [");
        StringBuilder result = new StringBuilder();
        String[] arraySrc = {"", "", "", ""};
        int count = 32;
        byte[] array = new byte[count];
        for (BaseTestTask task : tasks) {
            array[task.getmTaskId()] = (byte) task.isPass();
        }
        LogTools.p(TAG, "array:" + Arrays.toString(array));
        int k = 0;
        for (int i = 8 - 1; i < count; i += 8) {
            for (int j = i; j >= k * 8; j--) {
                result.append(array[j]);
            }
            if (k < array.length) {
                arraySrc[k++] = result.toString();
                result.delete(0, result.length());
            }
        }
        result.setLength(0);
//        byte transcat1 = ConvertUtils.bitToByte(arraySrc[0]);
//        byte transcat2 = ConvertUtils.bitToByte(arraySrc[1]);
//        byte transcat3 = ConvertUtils.bitToByte(arraySrc[2]);
//        byte transcat4 = ConvertUtils.bitToByte(arraySrc[3]);
//        builder.append("\"0x").append(Integer.toHexString(transcat4 & 0xFF)).append("\",\"0x").
//                append(Integer.toHexString(transcat3 & 0xFF)).append("\",\"0x").
//                append(Integer.toHexString(transcat2 & 0xFF)).append("\",\"0x").append(Integer.toHexString(transcat1 & 0xFF)).append("\"]}");
//        LogTools.p(TAG, "transcat1:" + transcat1 + ",transcat2:" + transcat2 + ",transcat3:" + transcat3 + ",transcat4:" + transcat4);
//        String data = builder.toString();
//        LogTools.p(TAG, "transcatResult builder:" + data);
        for (int i = arraySrc.length - 1; i >= 0; i--) {
            byte transcatByte = ConvertUtils.bitToByte(arraySrc[i]);
            builder.append("\"0x").append(Integer.toHexString(transcatByte & 0xFF));
            if (i == 0) {
                builder.append("\"]}");
            } else {
                builder.append("\",");
            }
        }
        String data = builder.toString();
        LogTools.p(TAG, "transcatResult builder:" + data);
        SerialCommand c = new SerialCommand(SerialConstants.SERIAL_TRANSACT_TEST_RESULTS_NO, SerialConstants.ID_TEST_RESULTS, data, this);
        RFCFactory.getInstance().sendMsg(c);
    }


    @Override
    public boolean isTaskComplete() {
        return (tasks != null) && !tasks.isEmpty() && (completeSize == tasks.size());
    }

    @Override
    public boolean hasTaskExecuting() {
        for (BaseTestTask task : autoTasks) {
            if (!task.isFinish()) {
                return true;
            }
        }
        for (BaseTestTask task : manualTasks) {
            if (!task.isFinish()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void exit() {
        isExit = true;
        stopTask();
        if (isTaskComplete()) {
            saveLog();
        }
    }

    @Override
    public void saveLog() {
        ThreadPool.getPool().executeSingleTask(new Runnable() {
            @Override
            public void run() {
                realSaveLog();
            }
        });
    }

    private void realSaveLog() {
        String dirPath = LogUtils.genDirPath(DIR_PATH);
        String fileName = LogUtils.genFileName("", 24, ZoneOffset.P0800);
        String filePath = dirPath + File.separator + fileName;
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter writer = null;
        if (FileUtils.createDir(dirPath)) {
            try {
                File file = new File(filePath);
                fos = new FileOutputStream(file, false);
                osw = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
                writer = new BufferedWriter(osw);
                List<StepEntity> stepEntities;
                StringBuilder builder = new StringBuilder();
                int k = 0;
                for (BaseTestTask task : tasks) {
                    stepEntities = task.getStepEntities();
                    int i = 1;
                    builder.append(HardwareTestApplication.getContext().getResources().getString(TITLES[k])).append(":\r\n");
                    for (StepEntity entity : stepEntities) {
                        String pass = entity.getTestState() == Constants.TestItemState.STATE_SUCCESS ?
                                HardwareTestApplication.getContext().getResources().getString(R.string.lable_pass) :
                                HardwareTestApplication.getContext().getResources().getString(R.string.label_un_pass);
                        builder.append("\t").append(i).append(".").append(entity.getStepTitle()).append("(").append(pass).append(")").append("\r\n");
                        i++;
                    }
                    builder.append("\r\n");
                    k++;
                }
                writer.write(builder.toString());
                writer.flush();
                builder.setLength(0);
            } catch (Exception e) {
                LogTools.p("SaveFileTask", e, "error");
            } finally {
                IOUtils.closeQuietly(fos);
                IOUtils.closeQuietly(osw);
                IOUtils.closeQuietly(writer);
            }
        }
        for (BaseFragment fragment : fragments) {
            if (fragment.getPresenter() != null) {
                fragment.getPresenter().destroy();
            }
        }
        Arrays.fill(fragments, 0, fragments.length, null);
        if (mView != null) {
            mView.destroyView();
        }
    }


    private void addTestTask() {
        int taskType;
        for (TestItem item : testItems) {
            BaseFragment fragment = item.getFragment();
            taskType = fragment.getTestType();
            if (fragment.getPresenter() != null && fragment.getPresenter().getTask() != null) {
                if (taskType == BaseTestTask.AUTOMATIC) {
                    autoTasks.add(fragment.getPresenter().getTask());
                } else {
                    manualTasks.add(fragment.getPresenter().getTask());
                }
                tasks.add(fragment.getPresenter().getTask());
            }
        }
    }

    private void executeTasks() {
        LogTools.p(TAG, "manualTasks:" + manualTasks.size());
        List<Runnable> autoRunnables = new ArrayList<>();
        for (BaseTestTask task : autoTasks) {
            task.setFinish(false);
            task.setmExecuteState(BaseTestTask.STATE_NONE);
            task.setManaul(false);
            autoRunnables.add(new FutureTask<>(task));
        }
        List<Runnable> manualRunnables = new ArrayList<>();
        for (BaseTestTask task : manualTasks) {
            task.setFinish(false);
            task.setmExecuteState(BaseTestTask.STATE_NONE);
            task.setManaul(false);
            manualRunnables.add(new FutureTask<>(task));
        }
        ThreadPool.getPool().executeOrderTask(manualRunnables);
        ThreadPool.getPool().execute(autoRunnables);
    }

    private void stopTask() {
        realStop(autoTasks);
        realStop(manualTasks);
    }

    private void realStop(List<BaseTestTask> tasks) {
        if (tasks == null) {
            LogTools.e(TAG, "realStop tasks is null");
            return;
        }
        for (BaseTestTask task : tasks) {
            if (!task.isFinish()) {
                task.stopTask();
            }
        }
    }

    @Override
    public void onSuccess(int NO, int id, String data) {

    }

    @Override
    public void onFailure(int NO, int id) {

    }
}
