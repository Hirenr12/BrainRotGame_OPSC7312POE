package com.example.practiceapplicationbrg

class Snake {
    companion object {
        var headX = 0f
        var headY = 0f
        var bodyParts = mutableListOf(arrayOf(0f, 0f))
        var direction = "right"
        var alive = false

        fun possibleMove(): Boolean {
            return headX >= 0f && headX <= 1000f && headY >= 0f && headY <= 1000f
        }

        fun reset() {
            headX = 0f
            headY = 0f
            bodyParts = mutableListOf(arrayOf(0f, 0f))
            direction = "right"
        }
    }
}
