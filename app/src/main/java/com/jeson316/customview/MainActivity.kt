package com.jeson316.customview

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.jeson316.customview.utils.ImageBlurUtils

class MainActivity : AppCompatActivity() {

    private lateinit var iv: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        iv = findViewById(R.id.image_new)

        blur()
    }

    private fun blur() {
        val decodeResource = BitmapFactory.decodeResource(resources, R.drawable.mm)
//        val fastBlur = ImageBlurUtils.fastFastBlur(this, decodeResource, 20f)
        val fastBlur = ImageBlurUtils.lowBlur(decodeResource, 1 / 4f, 20)
        iv.setImageBitmap(fastBlur)

    }
}