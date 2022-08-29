package com.example.shopappfirebase.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class MSAEditText(context: Context, attributeSet: AttributeSet) : AppCompatEditText(context, attributeSet) {

    init {
        typeface = Typeface.createFromAsset(context.assets, "Montserrat-Bold.ttf")
    }
}