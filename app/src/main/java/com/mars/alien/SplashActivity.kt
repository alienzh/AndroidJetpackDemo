package com.mars.alien

import android.content.Intent
import android.os.Bundle
import com.mars.alien.base.BaseActivity
import com.mars.alien.utils.UiHandlers
import com.mars.alien.utils.UiRunnable

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Constant.NUB = 2

        UiHandlers.postDelayed(object : UiRunnable(getContext()) {
            override fun run() {
                System.out.print("MYNUM" + Constant.NUB.toString())
                startActivity(Intent(getContext(), MainActivity::class.java))
                finish()
            }
        }, 2000)
    }
}
