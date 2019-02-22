package com.mars.alien.base;

import android.content.Context;
import android.util.Log;

import com.mars.alien.BuildConfig;
import com.mars.alien.utils.ContextUtils;
import com.mars.alien.utils.Strings;
import com.tencent.bugly.crashreport.CrashReport;

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

        initBugly();
    }

    private void initBugly() {
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getCurrentProcessName(context);
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        CrashReport.initCrashReport(getApplicationContext(), "262eb985e0", BuildConfig.DEBUG, strategy);
    }

    public ActivityLifecycleHelper getActivityLifecycleHelper() {
        return mActivityLifecycleHelper;
    }
}
