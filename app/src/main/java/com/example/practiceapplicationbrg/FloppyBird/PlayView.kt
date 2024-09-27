package com.example.practiceapplicationbrg.FloppyBird

import android.content.Context
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView


class PlayView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private val TAG = "PlayView"
    private var playThread: PlayThread? = null

    init {
        val holder = holder
        holder.addCallback(this)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        playThread = PlayThread(holder, resources, context)
        playThread?.isRunning = true
        playThread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Implement if needed
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        playThread?.isRunning = false
        while (retry) {
            try {
                playThread?.join()
                retry = false
            } catch (e: InterruptedException) {
                // Try again
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            playThread?.Jump()
        }
        return true
    }
}