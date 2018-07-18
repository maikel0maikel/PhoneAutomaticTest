package com.sinohb.hardware.test.entities;

import java.io.Serializable;

public class StepEntity implements Serializable{
    private int step;
    private String stepTitle;
    private int testState;//pass unpass testing
    private String description;
    public StepEntity(int step,String stepTitle,int testState){
        this.step = step;
        this.stepTitle = stepTitle;
        this.testState = testState;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getStepTitle() {
        return stepTitle;
    }

    public void setStepTitle(String stepTitle) {
        this.stepTitle = stepTitle;
    }

    public int getTestState() {
        return testState;
    }

    public void setTestState(int testState) {
        this.testState = testState;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
