package com.sinohb.hardware.test.entities;

import com.sinohb.hardware.test.app.BaseFragment;

public class TestItem {
    private int itemTitle;
    private int icon;
   // private boolean isSuccess;
    private boolean isSelect;
    private BaseFragment fragment;
   // private boolean isFinish;

    private int testState;

    public TestItem(int itemTitle,int icon){
        this.itemTitle = itemTitle;
        this.icon = icon;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(int itemTitle) {
        this.itemTitle = itemTitle;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

//    public boolean isSuccess() {
//        return isSuccess;
//    }
//
//    public void setSuccess(boolean success) {
//        isSuccess = success;
//    }

    public BaseFragment getFragment() {
        return fragment;
    }

    public void setFragment(BaseFragment fragment) {
        this.fragment = fragment;
    }

//    public boolean isFinish() {
//        return isFinish;
//    }
//
//    public void setFinish(boolean finish) {
//        isFinish = finish;
//    }


    public int getTestState() {
        return testState;
    }

    public void setTestState(int testState) {
        this.testState = testState;
    }
}
