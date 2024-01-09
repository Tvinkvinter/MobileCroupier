package com.atarusov.pokerapp.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import com.atarusov.pokerapp.R
import com.atarusov.pokerapp.databinding.ViewColorPickSquareBinding

class ColorPickSquare(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttrs: Int,
    defStylesRes: Int
) : FrameLayout(context, attrs, defStyleAttrs, defStylesRes) {
    var color: Int = context.getColor(R.color.white)
        set(value) {
            field = value
            binding.colorSquare.background.mutate().setTint(value)
        }
    var picked: Boolean = false
        set(value) {
            field = value
            binding.colorSquare.setImageDrawable(
                if (value)
                    AppCompatResources.getDrawable(context, R.drawable.color_pick_square_stroke)
                else null
            )
        }
    var blocked: Boolean = false
        set(value) {
            field = value
            binding.colorSquareMask.visibility =
                if (value) View.VISIBLE else View.GONE
            binding.colorSquare.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.color_pick_square_stroke
                )
            )
        }

    private val binding: ViewColorPickSquareBinding

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_color_pick_square, this, true)
        binding = ViewColorPickSquareBinding.bind(this)

        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.ColorPickSquare,
            defStyleAttrs,
            defStylesRes
        )

        color = typedArray.getInt(R.styleable.ColorPickSquare_color, context.getColor(R.color.white))
        blocked = typedArray.getBoolean(R.styleable.ColorPickSquare_blocked, false)
        picked = typedArray.getBoolean(R.styleable.ColorPickSquare_picked, false)
        typedArray.recycle()
    }
}