package com.sinohb.hardware.test.constant;

public class Constants {
    public static final int DEVICE_NOT_SUPPORT = -1;

    public static final int DEVICE_STATE_ERROR = -100;

    public static final int DEVICE_SUPPORTED = 0;

    public static final int DEVICE_RESET = 1;

    public static final int DEVICE_NORMAL = 2;

    public static final int DEVICE_CONNECTED = 10000;

    public static final String TASK_EXTRA_KEY = "step_entities";
    public static final String TASK_EXTRA_ID = "task.id";
    private Constants() {
    }


    public static class HandlerMsg {
        public static final int MSG_BASE_AUTOMIC_EXCECUTE_STATE = 999;
        public static final int MSG_BASE_MANUAL_EXCECUTE_STATE = 998;
        public static final int MSG_BASE_MANUAL_DISPLAY_VIEW = 997;
        public static final int MSG_BASE_MANUAL_COMPLETE_VIEW = 996;
        public static final int MSG_BASE_AUTOMIC_COMPLETE_VIEW = 995;
        /*****RGB******/
        public static final int MSG_RED = 110;

        public static final int MSG_GREEN = 111;

        public static final int MSG_BLUE = 112;

        public static final int MSG_COMPLETE = 129;
        /*****---end******/

        /**
         * 屏幕校准
         **/
        public static final int MSG_ADJUST_LEFT_TOP = 113;

        public static final int MSG_ADJUST_RIGHT_TOP = 114;

        public static final int MSG_ADJUST_LEFT_BOTTOM = 115;

        public static final int MSG_ADJUST_RIGHT_BOTTOM = 116;

        public static final int MSG_ADJUST_CENTER = 117;

        public static final int MSG_ADJUST_COMPLETE = 118;

        /**
         * 按键
         */
        public static final int MSG_KEY_PRESS_HOME = 119;

        public static final int MSG_KEY_PRESS_UP = 120;

        public static final int MSG_KEY_PRESS_DOWN = 121;

        public static final int MSG_KEY_PRESS_ENTER = 122;

        public static final int MSG_KEY_PRESS_BACK = 123;


        public static final int MSG_KEY_PRESEE_KEYCODE = 152;


        /**
         * knob
         */
        public static final int MSG_KNOB_COUNTERCLOCKWISE = 154;

        public static final int MSG_KNOB_CLOCKWISE = 155;


        /**
         * brightness
         */
        public static final int MSG_BRIGHTNESS_L_HINT = 131;
        public static final int MSG_BRIGHTNESS_M_HINT = 132;
        public static final int MSG_BRIGHTNESS_H_HINT = 133;

        /**
         * radio
         */
        public static final int MSG_RADIO_OPEN = 135;
        public static final int MSG_RADIO_CLOSE = 136;
        public static final int MSG_RADIO_PLAY = 137;
        public static final int MSG_RADIO_SEARCH = 138;
        public static final int MSG_RADIO_OPENED_HINT = 139;
        public static final int MSG_RADIO_SEARCH_NONE = 140;
        public static final int MSG_RADIO_SEARCH_STOP = 141;
        /**
         * effect
         */
        public static final int MSG_EFFECT_PLAY_NONE = 143;
        public static final int MSG_EFFECT_PLAY = 144;

        /**
         * amplifier
         */
        public static final int MSG_AMPLIFIER_TEST_ALL = 147;
        public static final int MSG_AMPLIFIER_HIT_POS = 148;

        /**
         * fan
         */
        public static final int MSG_FAN_TURN_ON = 149;
        public static final int MSG_FAN_TURN_OFF = 150;
        public static final int MSG_FAN_NOTIFY_TEST = 151;

        /**
         * gps
         */
        public static final int MSG_GPS_START_LOCATE = 152;
        public static final int MSG_GPS_STOP_LOCATE = 153;
        public static final int MSG_GPS_STATUS = 154;
        public static final int MSG_GPS_START_LOCATE_NOTIFY = 155;
        public static final int MSG_GPS_STOP_LOCATE_NOTIFY = 156;
        public static final int MSG_GPS_LOCATE_RESULT = 157;
        /**
         * usb
         */
        public static final int MSG_USB_STATUS = 158;
        /**
         * main
         */
        public static final int MSG_MAIN_NOTIFY_STOP_BTN = 159;
        public static final int MSG_MAIN_NOTIFY_START_BTN = 160;
        public static final int MSG_MAIN_ENABLE_BTN = 161;
        public static final int MSG_MAIN_AUX_BAR_IMG = 162;
        public static final int MSG_MAIN_USB_BAR_IMG = 163;
        public static final int MSG_MAIN_TF_BAR_IMG = 164;
        public static final int MSG_MAIN_NOTIFY_TASK_START = 165;
    }

    public static class TestItemState{
        public static final int STATE_TESTING = 2;
        public static final int STATE_SUCCESS = 1;
        public static final int STATE_FAIL = 0;
    }

}
