package com.jeson316.customview.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.widget.EditText

class UIUtils() {

    companion object {

        @JvmStatic
        fun getScreenDpiName(ctx: Context): String? {
            val density = ctx.resources.displayMetrics.densityDpi
            return when (density) {
                160 -> "mdpi"
                240 -> "hdpi"
                320 -> "xhdpi"
                480 -> "xxhdpi"
                else -> "xxxhdpi"
            }
        }

        @JvmStatic
        fun getScreenWidth(ctx: Context): Int {
            return ctx.resources.displayMetrics.widthPixels
        }


        @JvmStatic
        fun pxToDp(px: Int): Int {
            return (px.toFloat() / Resources.getSystem()
                .displayMetrics.density).toInt()
        }

        @JvmStatic
        fun dpToPx(dp: Int): Int {
            return (dp.toFloat() * Resources.getSystem()
                .displayMetrics.density).toInt()
        }


        fun hideKeyboardWhenClickedOutside(
            event: MotionEvent,
            currentFocus: View
        ) {
            if (event.action == 0 && currentFocus is EditText) {
                val outRect = Rect()
                currentFocus.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    currentFocus.clearFocus()
                    KeyboardUtils.hideSoftInput(currentFocus)
                }
            }
        }
    }

}