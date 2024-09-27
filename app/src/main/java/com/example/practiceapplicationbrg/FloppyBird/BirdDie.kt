package com.example.practiceapplicationbrg.FloppyBird

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.practiceapplicationbrg.R

class BirdDie(res: Resources) {
    var x : Int = 0
        get() = field
        set(value) {
            field = value
        }
    var y : Int = 0
        get() = field
        set(value) {
            field = value
        }

    var maxFrame = 1
    var currentFrame = 0
        get() = field
        set(value) {
            field = value
        }
    var dieArr : ArrayList<Bitmap>

    init{
        dieArr = arrayListOf()
        dieArr.add(BitmapFactory.decodeResource(res, R.drawable.floppybird_explosion0))

        x = ScreenSize.SCREEN_WIDTH/2 - dieArr[0].width/2
        y = ScreenSize.SCREEN_HEIGHT/2 - dieArr[0].width/2

    }

    fun getBirdDie(i : Int) : Bitmap{
        return dieArr.get(i)
    }


}