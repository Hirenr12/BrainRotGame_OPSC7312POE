package com.example.practiceapplicationbrg.FloppyBird

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import android.view.SurfaceHolder
import com.example.practiceapplicationbrg.R
import kotlin.random.Random


class PlayThread(
    private val holder: SurfaceHolder,
    private val resources: Resources,
    private val context: Context
) : Thread() {

    private val TAG: String = "PlayThread"

    private var startTimeMillis: Long = 0
    private var endTimeMillis: Long = 0

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("PracticeApplicationBRG", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    var isRunning: Boolean = false
        get() = field
        set(value) {
            field = value
        }

    private val FPS: Int = (1000.0 / 60.0).toInt()
    private val backgroundImage = BackgroundImage()
    private var bitmapImage: Bitmap? = null
    private var startTime: Long = 0
    private var frameTime: Long = 0
    private val velocity = 3
    private val bird: Bird = Bird(resources)

    private var state: Int = 0
    private var velocityBird: Int = 0

    private var cot: Cot? = null
    private val numCot = 2
    private val velocityCot = 10

    private val minY = 250
    private val maxY = ScreenSize.SCREEN_HEIGHT - minY - 500
    private val kc = ScreenSize.SCREEN_WIDTH * 3 / 4
    private var cotArray: ArrayList<Cot> = arrayListOf()
    private val ran: Random = Random

    private var iCot = 0
    private val birdDie: BirdDie = BirdDie(resources)

    var isDie = false
    var score: Int = 0

    init {
        bitmapImage = BitmapFactory.decodeResource(resources, R.drawable.floppybird_run_background)
        bitmapImage = bitmapImage?.let { ScaleResize(it) }
        cot = Cot(resources)
        createCot(resources)
    }

    private fun createCot(resources: Resources) {
        for (i in 0 until numCot) {
            val cot = Cot(resources)
            cot.x = ScreenSize.SCREEN_WIDTH + i * kc
            cot.ccY = ran.nextInt(maxY - minY) + minY
            cotArray.add(cot)
        }
    }

    override fun run() {
        Log.d(TAG, "Thread Started")

        // Start the timer
        startTimeMillis = System.currentTimeMillis()

        while (isRunning) {
            val canvas = holder.lockCanvas()
            if (canvas != null) {
                try {
                    synchronized(holder) {
                        render(canvas)
                        RenderBird(canvas)
                        RenderCot(canvas)
                        RenderDie(canvas)
                    }
                } finally {
                    holder.unlockCanvasAndPost(canvas)
                }
            }

            frameTime = (System.nanoTime() - startTime) / 1000000

            if (frameTime < FPS)
                try {
                    Thread.sleep(FPS - frameTime)
                } catch (e: InterruptedException) {
                    Log.e("Interrupted", "Thread Sleep Error")
                }
        }

        // End the timer
        endTimeMillis = System.currentTimeMillis()

        // Save the time and check for best time
        val elapsedMillis = endTimeMillis - startTimeMillis
        val bestTimeMillis = sharedPreferences.getLong("best_time", 0)
        if (elapsedMillis > bestTimeMillis) {
            editor.putLong("best_time", elapsedMillis)
            editor.apply()
        }

        Log.d(TAG, "Thread finish")
    }

    private fun RenderDie(canvas: Canvas?) {
        if (isDie) {
            var i: Int = birdDie.currentFrame
            canvas!!.drawBitmap(birdDie.getBirdDie(i), birdDie.x.toFloat(), birdDie.y.toFloat(), null)
            i++
            birdDie.currentFrame = i

            if (i == birdDie.maxFrame) {
                isRunning = false

                /*// Save the score and check for high score
                val highScore = sharedPreferences.getInt("high_score", 0)
                if (score > highScore) {
                    editor.putInt("high_score", score)
                    editor.apply()
                }*/

                // Redirect to FloppyBird_MainActivity with the score
                val intent = Intent(context, FloppyBird_MainActivity::class.java)
                intent.putExtra("score", score)
                context.startActivity(intent)
                (context as Activity).finish() // Close the current activity
            }
        }
    }

    private fun RenderCot(canvas: Canvas?) {
        if (state == 1) {
            if (cotArray[iCot].x < bird.x - cot!!.w) {
                iCot++
                if (iCot >= numCot) {
                    iCot = 0
                }
            }

            for (i in 0 until numCot) {
                if (cotArray[i].x < -cot!!.w) {
                    cotArray[i].x = cotArray[(i + numCot - 1) % numCot].x + kc
                    cotArray[i].ccY = ran.nextInt(maxY - minY) + minY
                    // Increase the score when a pipe set is removed from the screen
                    score++
                    Log.d(TAG, "Score: $score")
                }

                cotArray[i].x -= velocityCot
                canvas!!.drawBitmap(cot!!.cotTop, cotArray[i].x.toFloat(), cotArray[i].getTopY().toFloat(), null)
                canvas!!.drawBitmap(cot!!.cotBottom, cotArray[i].x.toFloat(), cotArray[i].getBottomY().toFloat(), null)
            }

            // Check if the bird passes between the pipes
            if ((cotArray[iCot].x) < bird.x + bird.getBird(0).width &&
                (cotArray[iCot].ccY > bird.y || cotArray[iCot].getBottomY() < bird.y + bird.getBird(0).height)
            ) {
                if (!isDie) {
                    Log.d(TAG, "Score: $score")
                }
                isDie = true
            }
        }
    }

    private fun RenderBird(canvas: Canvas?) {
        if (state == 1) {
            if (!isDie) {
                if (bird.y < (ScreenSize.SCREEN_HEIGHT - bird.getBird(0).height) || velocityBird < 0) {
                    velocityBird = velocityBird + 2
                    bird.y = bird.y + velocityBird
                }
            }
        }

        if (!isDie) {
            var current: Int = bird.currentFrame
            canvas!!.drawBitmap(bird.getBird(current), bird.x.toFloat(), bird.y.toFloat(), null)
            current++
            if (current > bird.maxFrame)
                current = 0
            bird.currentFrame = current
        }
    }

    private fun render(canvas: Canvas) {
        Log.d(TAG, "Render Canvas")
        backgroundImage.x = backgroundImage.x - velocity
        if (backgroundImage.x < -bitmapImage!!.width) {
            backgroundImage.x = 0
        }

        bitmapImage?.let { canvas.drawBitmap(it, (backgroundImage.x).toFloat(), (backgroundImage.y).toFloat(), null) }

        if (backgroundImage.x < -(bitmapImage!!.width - ScreenSize.SCREEN_WIDTH)) {
            bitmapImage?.let { canvas.drawBitmap(it, (backgroundImage.x + bitmapImage!!.width).toFloat(), (backgroundImage.y).toFloat(), null) }
        }
    }

    private fun ScaleResize(bitmap: Bitmap): Bitmap {
        val ratio: Float = (bitmap.width / bitmap.height).toFloat()
        val scaleWidth: Int = (ratio * ScreenSize.SCREEN_HEIGHT).toInt()
        return Bitmap.createScaledBitmap(bitmap, scaleWidth, ScreenSize.SCREEN_HEIGHT, false)
    }

    fun Jump() {
        state = 1
        if (bird.y > 0)
            velocityBird = -30
    }
}