package com.jeson316.circleprogress

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator

class CircleProgressView : View {


    private var centerX = 0.0f
    private var centerY = 0.0f
    private var radius = 0.0f
    private var colors = intArrayOf()
    private var maxOffset = 0
    private var minOffset = 0
    private var duration = 0
    private var animationSet: AnimatorSet? = null
    private lateinit var paint: Paint
    private var canvasAngle = 0
    private var offset = 200f  //the distance to center

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }


    private fun init(attrs: AttributeSet?) {
        val array: TypedArray =
            this.context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView)

        radius = array.getDimensionPixelSize(
            R.styleable.CircleProgressView_radius,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10f,
                context.resources.displayMetrics
            ).toInt()
        ).toFloat()

        offset = array.getDimensionPixelSize(
            R.styleable.CircleProgressView_offset,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                50f,
                context.resources.displayMetrics
            ).toInt()
        ).toFloat()

        maxOffset = array.getDimensionPixelSize(
            R.styleable.CircleProgressView_maxOffset,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                24f,
                context.resources.displayMetrics
            ).toInt()
        )
        minOffset = array.getDimensionPixelSize(
            R.styleable.CircleProgressView_minOffset,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                12f,
                context.resources.displayMetrics
            ).toInt()
        )
        colors = array.resources.getIntArray(
            array.getResourceId(
                R.styleable.CircleProgressView_colors, R.array.defaut_colors
            )
        )
        duration = array.getDimensionPixelSize(R.styleable.CircleProgressView_p_duration, 2000)
        array.recycle()

        paint = Paint()
        paint.apply {
            isAntiAlias = true
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = (w / 2).toFloat();
        centerY = (h / 2).toFloat();
        startAnim();
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (i in colors.indices) {
            paint.color = colors[i]
            canvas?.rotate((canvasAngle + i * (360 / colors.size)).toFloat(), centerX, centerY)
            canvas?.drawCircle(centerX + offset, centerY + offset,
                (i * 2.5).toFloat(), paint)
            canvas?.rotate((-(canvasAngle + i * (360 / colors.size))).toFloat(), centerX, centerY);
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        var sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        var sizeHeight = MeasureSpec.getSize(heightMeasureSpec)
        val modeWidth = MeasureSpec.getMode(widthMeasureSpec)
        val modeHeight = MeasureSpec.getMode(heightMeasureSpec)


        if (modeWidth == MeasureSpec.AT_MOST || modeWidth == MeasureSpec.UNSPECIFIED) {
            sizeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                100f,
                context.resources.displayMetrics
            ).toInt()
            sizeWidth += paddingLeft + paddingRight
        }
        if (modeHeight == MeasureSpec.AT_MOST || modeHeight == MeasureSpec.UNSPECIFIED) {
            sizeHeight = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                100f,
                context.resources.displayMetrics
            ).toInt()
            sizeHeight += paddingBottom + paddingTop
        }

        setMeasuredDimension(sizeWidth, sizeHeight)
    }


    fun startAnim() {
        animationSet = AnimatorSet()
        val animList: MutableCollection<Animator> = ArrayList()
        val canvasAnim: ValueAnimator = ValueAnimator.ofFloat(
            0f, ((colors.size) * 180 / colors.size).toFloat(), ((colors.size) * 360 / colors.size).toFloat()
        )
        canvasAnim.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator?) {
                var v = animation?.getAnimatedValue() as Number
                canvasAngle = v.toInt()
                invalidate()
            }
        })

        canvasAnim.repeatCount = ValueAnimator.INFINITE
        animList.add(canvasAnim)

        animationSet!!.setDuration(duration.toLong())
        animationSet!!.playTogether(animList)
        animationSet!!.setInterpolator(LinearInterpolator())
        animationSet!!.start()
    }


    private fun initialAnim() {
        if (animationSet != null) {
            if (animationSet!!.isRunning) {
                animationSet!!.end()
            }
            animationSet = null
            startAnim()
        }
    }

    fun setColors(colors: IntArray?) {
        this.colors = colors!!
        initialAnim()
    }

    fun setRadius(radius: Int) {
        this.radius = radius.toFloat()
    }

    fun setMaxMinSizeLength(maxOffset: Int, minOffset: Int) {
        this.maxOffset = maxOffset
        this.minOffset = minOffset
        initialAnim()
    }

    fun setDuration(duration: Int) {
        this.duration = duration
        initialAnim()
    }

}