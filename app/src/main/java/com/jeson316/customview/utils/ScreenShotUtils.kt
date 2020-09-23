package com.jeson316.customview.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AppCompatActivity

object ScreenShotUtils {

    /**
     * 屏幕截图
     */
    fun screenShot(appCompatActivity: AppCompatActivity): Bitmap {
        val decorView = appCompatActivity.window.decorView
        return viewShot(decorView);
    }


    /**
     * view 截图
     *
     * 建议使用 ARGB_8888 来进行处理， 否则 调用blur 高斯模糊时候会出现花屏显现
     *
     */
    fun viewShot(view: View): Bitmap {
        //根据view 创建一个空的bitmap
        val createBitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )
        //利用bitmap创建一个空的canvas
        val canvas = Canvas(createBitmap)
        //绘制一下背景
        canvas.drawColor(Color.WHITE)
        //绘制view 内容
        view.draw(canvas)

        return createBitmap
    }

}