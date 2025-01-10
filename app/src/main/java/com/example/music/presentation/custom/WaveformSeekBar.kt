package com.example.music.presentation.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class WaveformSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val waveformPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }

    private var waveformData: List<Int> = emptyList()
    var progress: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    fun setWaveform(data: List<Int>) {
        waveformData = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (waveformData.isEmpty()) return

        val barWidth = width / waveformData.size.toFloat()
        waveformData.forEachIndexed { index, amplitude ->
            val left = index * barWidth
            val top = height / 2f - amplitude / 2f
            val right = left + barWidth
            val bottom = height / 2f + amplitude / 2f

            val paint = if (index / waveformData.size.toFloat() <= progress) {
                waveformPaint
            } else {
                progressPaint
            }

            canvas.drawRect(left, top, right, bottom, paint)
        }
    }
}
