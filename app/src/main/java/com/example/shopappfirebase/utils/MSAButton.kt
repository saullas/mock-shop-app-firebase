package com.example.shopappfirebase.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class MSAButton(context: Context, attributeSet: AttributeSet) : AppCompatButton(context, attributeSet) {

    init {
        typeface = Typeface.createFromAsset(context.assets, "Montserrat-Bold.ttf")
    }
}