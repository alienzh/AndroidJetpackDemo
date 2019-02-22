package com.mars.alien.utils;

import android.content.Context;

import com.mars.alien.base.MApplication;

public class ViewUtil {

    public static float density = MApplication.getContext().getResources().getDisplayMetrics().density;

    public static void setDensity(float value) {
        density = value;
    }

    public static int dip2px(float dpValue) {
        return (int) (dpValue * density + 0.5f);
    }

    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }

    public static int dip2px(String dpValue) {
        try {
            if (dpValue.endsWith("dp")) {
                float dp = Integer.parseInt(dpValue.substring(0, dpValue.indexOf("dp")));
                return dip2px(dp);
            } else if (dpValue.endsWith("px")) {
                return Integer.parseInt(dpValue.substring(0, dpValue.indexOf("px")));
            }
        } catch (Exception e) {
            LogUtil.d("","dip to px error!");
        }
        return 0;

    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
}
