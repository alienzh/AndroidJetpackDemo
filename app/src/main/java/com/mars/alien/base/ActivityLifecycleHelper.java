package com.mars.alien.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.mars.alien.MainActivity;

import java.util.Stack;


public class ActivityLifecycleHelper implements Application.ActivityLifecycleCallbacks {

    private static Stack<Activity> activityStack;
    private Activity topActivity;

    public ActivityLifecycleHelper() {
        activityStack = new Stack<>();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        this.topActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        removeActivity(activity);
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activity instanceof MainActivity){
            return;
        }
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity getCurrentActivity() {
        if (activityStack.isEmpty()) {
            return null;
        }
        return activityStack.lastElement();
    }

    /**
     * 获取栈顶Activity
     */
    public Activity getTopActivity() {
        if (this.topActivity != null) {
            return this.topActivity;
        } else {
            return getCurrentActivity();
        }
    }

    public Activity getPreActivity() {
        int size = activityStack.size();
        if(size < 2)return null;
        return activityStack.elementAt(size - 2);
    }

    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                Activity activity = activityStack.get(i);
                if (!activity.isFinishing()) {
                    activity.finish();
                }
            }
        }
        activityStack.clear();
        this.topActivity = null;
    }
}
