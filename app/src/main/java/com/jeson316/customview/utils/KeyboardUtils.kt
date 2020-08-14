package com.jeson316.customview.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardUtils {
    fun hideSoftInput(view: View): View {
        val context = view.context
        val manager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager?.hideSoftInputFromWindow(view.windowToken, 0)
        return view
    }

    fun showSoftInput(view: View): View {
        val context = view.context
        val manager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager?.showSoftInput(view, 2)
        return view
    }

    fun setSoftInputAlwaysHidden(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (activity.currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken,
                0
            )
        }
    }
}