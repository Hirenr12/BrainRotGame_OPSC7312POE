package com.example.practiceapplicationbrg.FloppyBird


import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.practiceapplicationbrg.R

class Bird (res : Resources) {
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
    val maxFrame : Int
    var currentFrame : Int = 0
        get() = field
        set(value) {
            field = value
        }
    var birdList : ArrayList<Bitmap>

    init{
        birdList = arrayListOf()
        birdList.add(BitmapFactory.decodeResource(res, R.drawable.floppybird_frame_0))
        birdList.add(BitmapFactory.decodeResource(res,R.drawable.floppybird_frame_1))
        birdList.add(BitmapFactory.decodeResource(res,R.drawable.floppybird_frame_2))

        maxFrame = birdList.size - 1

        x=ScreenSize.SCREEN_WIDTH/2 - birdList[0].width/2
        y=ScreenSize.SCREEN_HEIGHT/2 - birdList[0].width/2
    }

    fun getBird(current : Int) : Bitmap{
        return birdList.get(current)
    }


}