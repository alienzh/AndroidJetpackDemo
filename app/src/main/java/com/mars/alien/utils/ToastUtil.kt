package com.mars.alien.utils

import android.content.Context
import android.widget.Toast
import com.mars.alien.base.MApplication

object ToastUtil {
    private var mToast: Toast? = null

    private val mContext: Context?
        get() = MApplication.getContext()

    fun show(context: Context?, info: String) {
        if (context == null) {
            return
        }
        if (mToast == null) {
            mToast = Toast.makeText(mContext, info, Toast.LENGTH_SHORT)
        } else {
            mToast?.let {
                it.setText(info)
                it.duration = Toast.LENGTH_SHORT
            }
        }
        mToast?.show()
    }

    fun show(context: Context, info: Int) {
        show(context, context.getString(info))
    }
}
