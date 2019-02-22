package com.mars.alien.lifecycle

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.os.Bundle
import com.mars.alien.base.BaseActivity
import com.mars.alien.R
import com.mars.alien.utils.LogUtil

class LifecycleActivity : BaseActivity(), LifecycleOwner {

    private val lifecycleRegistry: LifecycleRegistry by lazy {
        LifecycleRegistry(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_life_cycler)

        lifecycleRegistry.markState(Lifecycle.State.CREATED)

        val myObserver = MyObserver(lifecycle, object : MyObserver.Callback {
            override fun update(string: String) {
                LogUtil.d(componentName.className, string)
            }
        })
        lifecycle.addObserver(myObserver)
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    override fun onResume() {
        super.onResume()
        lifecycleRegistry.markState(Lifecycle.State.RESUMED)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
    }
}
