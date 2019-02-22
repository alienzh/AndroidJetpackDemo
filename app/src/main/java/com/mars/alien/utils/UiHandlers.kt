package com.mars.alien.utils

import android.os.Handler
import android.os.Looper

/**
 * @author marszhang
 * @date on 2019/1/30
 */
class UiHandlers {
    companion object {
        private const val MAIN_THREAD_ID: Long = 1

        fun post(runnable: Runnable) {
            getHandler().post(runnable)
        }


        fun post(runnable: UiRunnable) {
            getHandler().post { runnable.execute() }
        }


        fun postDelayed(runnable: Runnable, delayMillis: Long) {
            getHandler().postDelayed(runnable, delayMillis)
        }

        fun postDelayed(runnable: UiRunnable, delayMillis: Long) {
            getHandler().postDelayed({ runnable.execute() }, delayMillis)
        }

        fun removeCallback(runnable: Runnable) {
            getHandler().removeCallbacks(runnable)
        }

        fun getHandler(): Handler = HandlerHolder.HANDLER

        fun isMainThread(): Boolean {
            return Thread.currentThread().id == MAIN_THREAD_ID
        }
    }

    private object HandlerHolder {
        val HANDLER = Handler(Looper.getMainLooper())
    }
}