package com.udacity

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
    private var percent = 0f


    private val valueAnimator = ValueAnimator.ofFloat(0F,widthSize.toFloat())

    var buttonState:
            ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new){
            ButtonState.Loading -> animateButton()
            else -> {}
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f //TODO(check paint text size)
        typeface = Typeface.create("", Typeface.BOLD) // TODO(check typeface)

    }


    init {
        isClickable = true
        context.withStyledAttributes(attrs,R.styleable.LoadingButton){
            buttonColor = getColor(R.styleable.LoadingButton_readyColor,0)
            loadColor = getColor(R.styleable.LoadingButton_loadingColor,0)
            textColor = getColor(R.styleable.LoadingButton_textColor,0)
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawDownloadButton(canvas)
        if(buttonState == ButtonState.Loading){
            drawLoadingButton(canvas)
        }
    }

    private fun drawDownloadButton(canvas: Canvas){
        paint.color = buttonColor
        canvas.drawRect(0f,0f,widthSize.toFloat(),heightSize.toFloat(),paint)
        val buttonText = resources.getString(R.string.download)
        drawText(canvas,buttonText)
    }

    private fun drawLoadingButton(canvas: Canvas){
        paint.color = loadColor
        canvas.drawRect(0f,0f,percent,heightSize.toFloat(),paint)
        val buttonText = resources.getString(R.string.button_loading)
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

    private fun animateButton() {
        valueAnimator.repeatCount = 1
        valueAnimator.duration = 3000
        //valueAnimator.repeatMode = ValueAnimator.REVERSE
        valueAnimator.interpolator = LinearInterpolator()
        percent = valueAnimator.animatedValue as Float
        this@LoadingButton.invalidate()
        valueAnimator.start()
//        valueAnimator.apply{
//            duration = 3000
//            addUpdateListener {
//                percent = it.animatedValue as Float
//                it.repeatCount = ValueAnimator.INFINITE
//                it.repeatMode = ValueAnimator.REVERSE
//                it.interpolator = LinearInterpolator()
//                this@LoadingButton.invalidate()
//            }
//            //TODO(disable button)
//            start()
//        }
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