package com.sinohb.hardware.test.module.main;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.sinohb.hardware.test.entities.TestItem;
import com.sinohb.hardware.test.task.BaseTestTask;

import java.util.List;

public interface MainPresenter {

    interface View   {

        void setPresenter(Controller presenter);

        FragmentManager getFramentManager();

        void notifyItemTaskFinish(BaseTestTask task, int testType);

        void notifyStopBtn();

        void notifyStartBtn();

        void exit();

        void destroyView();

        void notifyTaskStart();
    }

    interface Controller {

        void start();

        void pause();

        void stop();

        void complete();

        void destroy();

        void initFragments(Bundle savedInstanceState);

        List<TestItem> getTestItems();

        TestItem getTestItem(int pos);

        int getItemsCount();

        void notifyItemTaskFinish(BaseTestTask task, int testType);

        boolean isTaskComplete();

        boolean hasTaskExecuting();

        void exit();
    }

}
