package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var buttonColor = 0
    private var textColor = 0
    private var loadColor = 0
    private var arcColor = 0
    private var percent = 0f


    private lateinit var valueAnimator: ValueAnimator

    var buttonState:
            ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new){
            ButtonState.Loading -> animateButton()
            ButtonState.Completed -> {
                valueAnimator.end()
                percent = 0f
            }
            else -> {}
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)

    }


    init {
        isClickable = true
        context.withStyledAttributes(attrs,R.styleable.LoadingButton){
            buttonColor = getColor(R.styleable.LoadingButton_readyColor,0)
            loadColor = getColor(R.styleable.LoadingButton_loadingColor,0)
            textColor = getColor(R.styleable.LoadingButton_textColor,0)
            arcColor = getColor(R.styleable.LoadingButton_arcColor,0)
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawDownloadButton(canvas)
        if(buttonState == ButtonState.Loading){
            drawLoadingButton(canvas)
            drawArc(canvas)
        }
        drawText(canvas)
    }

    private fun drawDownloadButton(canvas: Canvas){
        paint.color = buttonColor
        canvas.drawRect(0f,0f,widthSize.toFloat(),heightSize.toFloat(),paint)
    }

    private fun drawLoadingButton(canvas: Canvas){
        paint.color = loadColor
        canvas.drawRect(0f,0f,percent,heightSize.toFloat(),paint)
    }

    private fun drawText(canvas: Canvas){
        val buttonText = when(buttonState){
            ButtonState.Loading -> context.getString(R.string.button_loading)
            else -> context.getString(R.string.download)
        }
        drawText(canvas,buttonText)
    }


    //Centering text based on discussion at mentor post here:
    //https://knowledge.udacity.com/questions/438647
    private fun drawText(canvas: Canvas,text: String){
        paint.color = textColor
        val textHeight = paint.descent() - paint.ascent()
        val textOffset = textHeight / 2 - paint.descent()
        val textY = heightSize.toFloat() / 2 + textOffset
        val textX = widthSize.toFloat()/2
        canvas.drawText(text,textX,textY,paint)
    }

    private fun drawArc(canvas: Canvas){
        paint.color = arcColor
        val text = context.getString(R.string.button_loading)
        val textWidth = paint.measureText(text)
        val centerX = (widthSize.toFloat() + textWidth)/2 + 35f
        val centerY = heightSize.toFloat()/2
        val left = centerX - 30f
        val top = centerY - 30f
        val right = centerX + 30f
        val bottom = centerY + 30f
        val startAngle = 0f
        val sweepAngle = percent*360 / widthSize.toFloat()
        canvas.drawArc(left,top,right,bottom,startAngle,sweepAngle,true,paint)
    }

    private fun animateButton() {
        valueAnimator = ValueAnimator.ofFloat(0F,widthSize.toFloat()).apply{
            duration = 3000
            addUpdateListener {
                percent = it.animatedValue as Float
                it.interpolator = LinearInterpolator()
                this@LoadingButton.invalidate()
            }
            start()
        }
    }

//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        super.onSizeChanged(w, h, oldw, oldh)
//        heightSize = h
//        widthSize = w
//    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}