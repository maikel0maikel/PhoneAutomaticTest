package com.sinohb.hardware.test.constant;

public class Constants {
    public static final int DEVICE_NOT_SUPPORT = -1;

    public static final int DEVICE_STATE_ERROR = -100;

    public static final int DEVICE_SUPPORTED = 0;

    private Constants() {
    }


    public static class HandlerMsg {

        /*****RGB******/
        public static final int MSG_RED = 110;

        public static final int MSG_GREEN = 111;

        public static final int MSG_BLUE = 112;
        /*****---end******/

        /**屏幕校准**/
        public static final int MSG_ADJUST_LEFT_TOP = 113;

        public static final int MSG_ADJUST_RIGHT_TOP = 114;

        public static final int MSG_ADJUST_LEFT_BOTTOM = 115;

        public static final int MSG_ADJUST_RIGHT_BOTTOM = 116;

        public static final int MSG_ADJUST_CENTER = 117;

        public static final int MSG_ADJUST_COMPLETE = 118;

        /**
         * 按键
         */
        public static final int MSG_KEY_PRESS_MUNU = 119;

        public static final int MSG_KEY_PRESS_UP = 120;

        public static final int MSG_KEY_PRESS_DOWN = 121;

        public static final int MSG_KEY_PRESS_ENTER = 122;

        public static final int MSG_KEY_PRESS_BACK = 123;

    }


}
