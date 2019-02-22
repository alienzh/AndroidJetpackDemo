package com.mars.alien

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.mars.alien.base.BaseActivity
import com.mars.alien.databinding.ActivityMainBinding
import com.mars.alien.lifecycle.LifecycleActivity
import com.mars.alien.utils.LogUtil

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.handler = this
        binding.setLifecycleOwner(this)
        System.out.print("MYNUM" + Constant.NUB.toString())
    }

    fun lifecycle() {
        startActivity(Intent(getContext(), LifecycleActivity::class.java))
    }
}
