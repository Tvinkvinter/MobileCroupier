package com.atarusov.pokerapp.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.atarusov.pokerapp.R

class ColorPickSquare(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttrs: Int,
    defStylesRes: Int
) : FrameLayout(context, attrs, defStyleAttrs, defStylesRes) {
    private var color: Int? = context.getColor(R.color.white)
        set(value) {
            field = value
            rootView.findViewById<ImageView>(R.id.color_square).drawable.setTint(
                context.getColor(value!!) ?: context.getColor(R.color.white)
            )
        }
    private var blocked: Boolean = false
        set(value) {
            field = value
            rootView.findViewById<ImageView>(R.id.color_square_mask).visibility =
                if (value) View.GONE else View.VISIBLE
            rootView.findViewById<ImageView>(R.id.color_square_stroke).drawable.apply {
                if (value) setTint(context.getColor(R.color.black))
                else setTint(context.getColor(R.color.white))
            }
        }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_color_pick_square, this)

        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.ColorPickSquare,
            defStyleAttrs,
            defStylesRes
        )

        color =
            typedArray.getColor(R.styleable.ColorPickSquare_color, context.getColor(R.color.white))
        blocked = typedArray.getBoolean(R.styleable.ColorPickSquare_blocked, false)

        typedArray.recycle()
    }
}