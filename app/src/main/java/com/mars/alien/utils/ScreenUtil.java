package com.mars.alien.utils;

import android.content.res.Resources;

public class ScreenUtil {

    private static int statusBarHeight = 0;

    public static int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            initConfigData(ContextUtils.getContext().getResources());
        }
        return statusBarHeight;
    }

    private static void initConfigData(Resources resources) {
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId);
        }
    }
}
