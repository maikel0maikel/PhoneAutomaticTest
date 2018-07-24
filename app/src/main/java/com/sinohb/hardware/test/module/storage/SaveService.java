package com.sinohb.hardware.test.module.storage;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseTestTask;
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
import java.util.List;

public class SaveService extends IntentService{
    private static final String DIR_PATH = "testLog";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SaveService() {
        super("SaveService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent!=null){
            ArrayList<BaseTestTask> tasks = (ArrayList<BaseTestTask>) intent.getSerializableExtra(Constants.TASK_EXTRA_KEY);
            String dirPath = LogUtils.genDirPath(DIR_PATH);
            String fileName = LogUtils.genFileName("", 24, ZoneOffset.P0800);
            String filePath = dirPath + File.separator + fileName;
            FileOutputStream fos = null;
            OutputStreamWriter osw = null;
            BufferedWriter writer = null;
            if (FileUtils.createDir(dirPath)) {
                try{
                    File file = new File(filePath);
                    fos = new FileOutputStream(file, true);
                    osw = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
                    writer = new BufferedWriter(osw);
                    List<StepEntity> stepEntities;
                    StringBuilder builder = new StringBuilder();
                    for (BaseTestTask task : tasks) {
                        stepEntities = task.getStepEntities();
                        int i = 0;
                        for (StepEntity entity : stepEntities) {
                            String pass = entity.getTestState() == Constants.TestItemState.STATE_SUCCESS ?
                                    HardwareTestApplication.getContext().getResources().getString(R.string.lable_pass) :
                                    HardwareTestApplication.getContext().getResources().getString(R.string.label_un_pass);
                            builder.append(i).append(".").append(entity.getStepTitle()).append("(").append(pass).append(")").append("\r\n");
                            i++;
                        }
                        builder.append("\r\n");
                    }
                    writer.write(builder.toString());
                    writer.flush();
                    builder.setLength(0);
                }catch (Exception e){
                    LogTools.p("SaveFileTask",e,"error");
                }finally {
                    IOUtils.closeQuietly(fos);
                    IOUtils.closeQuietly(osw);
                    IOUtils.closeQuietly(writer);
                }
            }
        }
    }

}
