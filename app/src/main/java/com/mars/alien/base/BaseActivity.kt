package com.mars.alien.base

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import com.mars.alien.utils.LogUtil

/**
 * @author zhangw
 * @date on 2019/1/29
 */
open class BaseActivity: AppCompatActivity() {

    fun getContext(): Context = this

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setRequestWindowFeature()
    }

    protected fun setRequestWindowFeature() {
        this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
    }

    private var mSwipeWindowHelper: SwipeWindowHelper? = null
    private var isNeedSwipe = true

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        try {
            if (!supportSlideBack()) {
                return super.dispatchTouchEvent(ev)
            }
            if (!isNeedSwipe) {
                if (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_CANCEL) {
                    isNeedSwipe = true
                }
                return super.dispatchTouchEvent(ev)
            }

            if (mSwipeWindowHelper == null) {
                mSwipeWindowHelper = SwipeWindowHelper(this)
            }
            return mSwipeWindowHelper!!.processTouchEvent(ev) || super.dispatchTouchEvent(ev)
        } catch (e: Exception) {
            LogUtil.d(e.toString())
            return false
        }

    }

    fun setIsNeedSwipe(isNeedSwipe: Boolean) {
        this.isNeedSwipe = isNeedSwipe
    }

    fun isSliding(): Boolean {
        return if (mSwipeWindowHelper == null) {
            false
        } else mSwipeWindowHelper!!.isSliding
    }

    fun isSlidFinish(): Boolean {
        return if (mSwipeWindowHelper == null) {
            true
        } else mSwipeWindowHelper!!.isSlideFinish()
    }

    fun supportSlideBack(): Boolean {
        return true
    }

    fun isMovementForBackPressed(motionEvent: MotionEvent): Boolean {
        return true
    }
}