package com.example.shopappfirebase.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class MSARadioButton(context: Context, attributeSet: AttributeSet) : AppCompatRadioButton(context, attributeSet) {

    init {
        typeface = Typeface.createFromAsset(context.assets, "Montserrat-Bold.ttf")
    }
}