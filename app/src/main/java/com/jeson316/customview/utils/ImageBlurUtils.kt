package com.jeson316.customview.utils

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import androidx.annotation.RequiresApi

/**
 * 怼图片进行高斯模糊的工具类
 */
class ImageBlurUtils {

    companion object {
        val TAG = this.javaClass.name

        /**
         * 更更快速进行高斯模糊
         * 思路： 通过先对原始图片进行缩小，减少一些像素，再进行模糊处理，再放大到原图大小
         * @param context  Android上下文
         * @param sourceBitmap 要处理的图片
         * @param radius 模糊成都， 取值范围为1-25，默认为5
         * @param scale 缩放比率，取值范围 ,默认为 1/2。一般为 2的整数次幂，如 1、1/2、1/4、1/8
         * @return 返回一个新的 bitmap 对象
         */
        @RequiresApi(17)
        @JvmStatic
        fun fastFastBlur(
            context: Context,
            sourceBitmap: Bitmap,
            radius: Float = 5f,
            scale: Float = 1 / 2F
        ): Bitmap {

            val w = sourceBitmap.width
            val h = sourceBitmap.height

            Log.i(TAG, "source size:" + w + "*" + h)
            var inputBitmap: Bitmap = Bitmap.createScaledBitmap(
                sourceBitmap,
                (w * (scale)).toInt(),
                (h * (scale)).toInt(),
                false
            )

//            创建RenderScript 对象
            val renderScript = RenderScript.create(context)
//            创建配置器 Allocation
            val input = Allocation.createFromBitmap(renderScript, inputBitmap)
            val output = Allocation.createTyped(renderScript, input.type)

            val scriptIntrinsicBlur =
                ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

//            对图片进行处理
            scriptIntrinsicBlur.setInput(input)
//            不设置 radius 的话默认为 5
            scriptIntrinsicBlur.setRadius(radius)
            scriptIntrinsicBlur.forEach(output)
//             复制处理后的资源bitmap
            output.copyTo(inputBitmap)
//            释放资源
            renderScript.destroy()
            return inputBitmap;
        }


        /**
         *
         * 快速进行高斯模糊
         * 底层调用的是 c ，所以能进行快速的处理
         *
         * @param context  Android上下文
         * @param sourceBitmap 要处理的图片
         * @param radius 模糊成都， 取值范围为1-25，默认为5
         * @return 返回一个新的 bitmap 对象
         */
        @RequiresApi(17)
        @JvmStatic
        fun fastBlur(context: Context, sourceBitmap: Bitmap, radius: Float = 5f): Bitmap {

            var inputBitmap: Bitmap = sourceBitmap
//            创建RenderScript 对象
            val renderScript = RenderScript.create(context)
//            创建配置器 Allocation
            val input = Allocation.createFromBitmap(renderScript, inputBitmap)
            val output = Allocation.createTyped(renderScript, input.type)

            val scriptIntrinsicBlur =
                ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

//            对图片进行处理
            scriptIntrinsicBlur.setInput(input)
//            不设置 radius 的话默认为 5
            scriptIntrinsicBlur.setRadius(radius)
            scriptIntrinsicBlur.forEach(output)
//             复制处理后的资源bitmap
            output.copyTo(inputBitmap)
//            释放资源
            renderScript.destroy()
            return inputBitmap;
        }


        /**
         * 比较老的代码了， 基于java层面。
         * 你内存爆炸了不要怪我。
         * 看的懂的可以尝试， 反正我是不会用的。
         *
         * @param scale 取值范围 0 - 1 ，不包含0。 最好是2的倍数。如 1/2、1/4、1/8、、、、
         * 太小了就爆炸了要，糊的鬼也看不出来
         * @param radius 大于1 ， 整数就行， 也别太大。否则模糊的啥也看不出来了
         *
         *
         *
         * Stack Blur v1.0 from
         * http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
         * Java Author: Mario Klingemann <mario at quasimondo.com>
         * http://incubator.quasimondo.com
         *
         * created Feburary 29, 2004
         * Android port : Yahel Bouaziz <yahel at kayenko.com>
         * http://www.kayenko.com
         * ported april 5th, 2012
         *
         * This is a compromise between Gaussian Blur and Box blur
         * It creates much better looking blurs than Box Blur, but is
         * 7x faster than my Gaussian Blur implementation.
         *
         * I called it Stack Blur because this describes best how this
         * filter works internally: it creates a kind of moving stack
         * of colors whilst scanning through the image. Thereby it
         * just has to add one new block of color to the right side
         * of the stack and remove the leftmost color. The remaining
         * colors on the topmost layer of the stack are either added on
         * or reduced by one, depending on if they are on the right or
         * on the left side of the stack.
         *
         * If you are using this algorithm in your code please add
         * the following line:
         * Stack Blur Algorithm by Mario Klingemann <mario></mario>@quasimondo.com>
        </yahel></mario>
         */
        @JvmStatic
        fun lowBlur(sentBitmap: Bitmap, scale: Float, radius: Int): Bitmap? {
            var sentBitmap = sentBitmap
            val width = Math.round(sentBitmap.width * scale)
            val height = Math.round(sentBitmap.height * scale)
            sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false)
            val bitmap = sentBitmap.copy(sentBitmap.config, true)
            if (radius < 1) {
                return null
            }
            val w = bitmap.width
            val h = bitmap.height
            val pix = IntArray(w * h)
            Log.e("pix", w.toString() + " " + h + " " + pix.size)
            bitmap.getPixels(pix, 0, w, 0, 0, w, h)
            val wm = w - 1
            val hm = h - 1
            val wh = w * h
            val div = radius + radius + 1
            val r = IntArray(wh)
            val g = IntArray(wh)
            val b = IntArray(wh)
            var rsum: Int
            var gsum: Int
            var bsum: Int
            var x: Int
            var y: Int
            var i: Int
            var p: Int
            var yp: Int
            var yi: Int
            var yw: Int
            val vmin = IntArray(Math.max(w, h))
            var divsum = div + 1 shr 1
            divsum *= divsum
            val dv = IntArray(256 * divsum)
            i = 0
            while (i < 256 * divsum) {
                dv[i] = i / divsum
                i++
            }
            yi = 0
            yw = yi
            val stack = Array(div) {
                IntArray(
                    3
                )
            }
            var stackpointer: Int
            var stackstart: Int
            var sir: IntArray
            var rbs: Int
            val r1 = radius + 1
            var routsum: Int
            var goutsum: Int
            var boutsum: Int
            var rinsum: Int
            var ginsum: Int
            var binsum: Int
            y = 0
            while (y < h) {
                bsum = 0
                gsum = bsum
                rsum = gsum
                boutsum = rsum
                goutsum = boutsum
                routsum = goutsum
                binsum = routsum
                ginsum = binsum
                rinsum = ginsum
                i = -radius
                while (i <= radius) {
                    p = pix[yi + Math.min(wm, Math.max(i, 0))]
                    sir = stack[i + radius]
                    sir[0] = p and 0xff0000 shr 16
                    sir[1] = p and 0x00ff00 shr 8
                    sir[2] = p and 0x0000ff
                    rbs = r1 - Math.abs(i)
                    rsum += sir[0] * rbs
                    gsum += sir[1] * rbs
                    bsum += sir[2] * rbs
                    if (i > 0) {
                        rinsum += sir[0]
                        ginsum += sir[1]
                        binsum += sir[2]
                    } else {
                        routsum += sir[0]
                        goutsum += sir[1]
                        boutsum += sir[2]
                    }
                    i++
                }
                stackpointer = radius
                x = 0
                while (x < w) {
                    r[yi] = dv[rsum]
                    g[yi] = dv[gsum]
                    b[yi] = dv[bsum]
                    rsum -= routsum
                    gsum -= goutsum
                    bsum -= boutsum
                    stackstart = stackpointer - radius + div
                    sir = stack[stackstart % div]
                    routsum -= sir[0]
                    goutsum -= sir[1]
                    boutsum -= sir[2]
                    if (y == 0) {
                        vmin[x] = Math.min(x + radius + 1, wm)
                    }
                    p = pix[yw + vmin[x]]
                    sir[0] = p and 0xff0000 shr 16
                    sir[1] = p and 0x00ff00 shr 8
                    sir[2] = p and 0x0000ff
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                    rsum += rinsum
                    gsum += ginsum
                    bsum += binsum
                    stackpointer = (stackpointer + 1) % div
                    sir = stack[stackpointer % div]
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                    rinsum -= sir[0]
                    ginsum -= sir[1]
                    binsum -= sir[2]
                    yi++
                    x++
                }
                yw += w
                y++
            }
            x = 0
            while (x < w) {
                bsum = 0
                gsum = bsum
                rsum = gsum
                boutsum = rsum
                goutsum = boutsum
                routsum = goutsum
                binsum = routsum
                ginsum = binsum
                rinsum = ginsum
                yp = -radius * w
                i = -radius
                while (i <= radius) {
                    yi = Math.max(0, yp) + x
                    sir = stack[i + radius]
                    sir[0] = r[yi]
                    sir[1] = g[yi]
                    sir[2] = b[yi]
                    rbs = r1 - Math.abs(i)
                    rsum += r[yi] * rbs
                    gsum += g[yi] * rbs
                    bsum += b[yi] * rbs
                    if (i > 0) {
                        rinsum += sir[0]
                        ginsum += sir[1]
                        binsum += sir[2]
                    } else {
                        routsum += sir[0]
                        goutsum += sir[1]
                        boutsum += sir[2]
                    }
                    if (i < hm) {
                        yp += w
                    }
                    i++
                }
                yi = x
                stackpointer = radius
                y = 0
                while (y < h) {

                    // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                    pix[yi] =
                        -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]
                    rsum -= routsum
                    gsum -= goutsum
                    bsum -= boutsum
                    stackstart = stackpointer - radius + div
                    sir = stack[stackstart % div]
                    routsum -= sir[0]
                    goutsum -= sir[1]
                    boutsum -= sir[2]
                    if (x == 0) {
                        vmin[y] = Math.min(y + r1, hm) * w
                    }
                    p = x + vmin[y]
                    sir[0] = r[p]
                    sir[1] = g[p]
                    sir[2] = b[p]
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                    rsum += rinsum
                    gsum += ginsum
                    bsum += binsum
                    stackpointer = (stackpointer + 1) % div
                    sir = stack[stackpointer]
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                    rinsum -= sir[0]
                    ginsum -= sir[1]
                    binsum -= sir[2]
                    yi += w
                    y++
                }
                x++
            }
            Log.e("pix", w.toString() + " " + h + " " + pix.size)
            bitmap.setPixels(pix, 0, w, 0, 0, w, h)
            return bitmap
        }
    }
}