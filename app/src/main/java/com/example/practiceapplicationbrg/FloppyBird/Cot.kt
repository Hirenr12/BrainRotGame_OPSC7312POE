package com.example.practiceapplicationbrg.FloppyBird

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.practiceapplicationbrg.R

class Cot (res:Resources){
    val cotTop = BitmapFactory.decodeResource(res, R.drawable.floppybird_cot_top)
        get() = field
    val cotBottom = BitmapFactory.decodeResource(res, R.drawable.floppybird_cot_bottom)
        get() = field

    val w = cotTop.width
    val h = cotTop.height

    var x : Int = 0
        get() = field
        set(value) {
            field = value
        }

    var ccY : Int = 0
        get() = field
        set(value) {
            field = value
        }

    fun getTopY() : Int{
        return ccY-h
    }

    fun getBottomY() : Int {
        return ccY+500
    }

}