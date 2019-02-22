package com.mars.alien.lifecycle

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent

/**
 * @author mars
 * @date on 2019/1/29
 */
class MyObserver(var lifecycle: Lifecycle, var callback: Callback) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public fun connectOnCreate() {
        p("connectOnCreate")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public fun connectOnResume() {
        p("connectOnResume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public fun connectOnDestroy() {
        p("connectOnDestroy")
    }

    private fun p(string: String) {
        callback.update(string)
    }

    interface Callback {
        fun update(string: String)
    }
}