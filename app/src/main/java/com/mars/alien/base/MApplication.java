package com.mars.alien.base;

import android.util.Log;

import com.mars.alien.utils.ContextUtils;
import com.mars.alien.utils.Strings;

public class MApplication extends ContextUtils {

    private static final String MARS_PROCESS_NAME = "com.mars.alien";
    ActivityLifecycleHelper mActivityLifecycleHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        String currentProcessName = getCurrentProcessName(this);
        System.out.print("MApplication:" + currentProcessName);
        if (Strings.equalsIgnoreCase(currentProcessName, MARS_PROCESS_NAME)) {
            try {
                registerActivityLifecycleCallbacks(mActivityLifecycleHelper = new ActivityLifecycleHelper());
            } catch (Exception e) {
                Log.i("application", "Activity lifecycle register error.");
            } finally {
                // TODO: 2019/2/12
            }
        } else {
            // TODO: 2019/2/12
        }
    }

    public ActivityLifecycleHelper getActivityLifecycleHelper() {
        return mActivityLifecycleHelper;
    }
}
