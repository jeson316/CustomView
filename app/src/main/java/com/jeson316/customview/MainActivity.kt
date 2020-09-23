package com.jeson316.customview

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.jeson316.customview.utils.ImageBlurUtils
import com.jeson316.customview.utils.ScreenShotUtils

class MainActivity : AppCompatActivity() {

    private lateinit var iv: ImageView
    private lateinit var iv_Input: ImageView

    private lateinit var root: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        iv = findViewById(R.id.image_new)
        iv_Input = findViewById(R.id.image_input)
        root = findViewById(R.id.container_view)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        shot()
    }


    private fun blur() {
        val decodeResource = BitmapFactory.decodeResource(resources, R.drawable.mm)
//        val fastBlur = ImageBlurUtils.fastFastBlur(this, decodeResource, 20f)
        val fastBlur = ImageBlurUtils.lowBlur(decodeResource, 1 / 4f, 20)
        iv.setImageBitmap(fastBlur)
    }


    private fun shot() {
//        iv.setImageBitmap(OtherUtils.screenShot(this))
        val viewShot = ScreenShotUtils.viewShot(iv_Input)
        val fastFastBlur = ImageBlurUtils.fastFastBlur(this, viewShot)
        val bitmapDrawable = BitmapDrawable(resources, fastFastBlur)
        root.setBackgroundDrawable(bitmapDrawable)
    }
}