package com.mars.alien.utils

import android.app.Activity
import android.content.Context
import android.os.Build

/**
 * @author marszhang
 * @date on 2019/1/30
 */
abstract class UiRunnable : Runnable {

    private val activity: Context?

    constructor(activity: Context) {
        this.activity = activity
    }

    fun execute() {
        if (activity == null) {
            return
        }
        if (!isActivityFinished(activity as Activity?)) {
            run()
        }
    }

    private fun isActivityFinished(activity: Activity?): Boolean {
        if (activity == null) {
            return true
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.isFinishing || activity.isDestroyed
        } else {
            activity.isFinishing
        }
    }
}