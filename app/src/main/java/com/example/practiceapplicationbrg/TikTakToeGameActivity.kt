package com.example.practiceapplicationbrg

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView

class TikTakToeGameActivity : AppCompatActivity() {

    private var activePlayer = true // true for Player X, false for Player O
    private var boardStatus = Array(3) { IntArray(3) } // 0 for empty, 1 for X, 2 for O
    private var board = arrayOfNulls<ImageButton>(9)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tik_tak_toe_game)

        // Initialize ImageButtons
        board[0] = findViewById(R.id.button1)
        board[1] = findViewById(R.id.button2)
        board[2] = findViewById(R.id.button3)
        board[3] = findViewById(R.id.button4)
        board[4] = findViewById(R.id.button5)
        board[5] = findViewById(R.id.button6)
        board[6] = findViewById(R.id.button7)
        board[7] = findViewById(R.id.button8)
        board[8] = findViewById(R.id.button9)

        for (i in board.indices) {
            board[i]?.setOnClickListener {
                onBoardClick(it as ImageButton, i)
            }
        }

        resetBoard()
    }

    private fun onBoardClick(button: ImageButton, index: Int) {
        if (boardStatus[index / 3][index % 3] != 0) return

        val row = index / 3
        val col = index % 3

        if (activePlayer) {
            button.setImageResource(R.drawable.x) // Set X image
            boardStatus[row][col] = 1
        } else {
            button.setImageResource(R.drawable.o) // Set O image
            boardStatus[row][col] = 2
        }

        button.isClickable = false
        activePlayer = !activePlayer
        checkForWinner()
    }

    private fun checkForWinner() {
        val statusTextView: TextView = findViewById(R.id.statusTextView)

        // Check rows and columns
        for (i in 0..2) {
            if (boardStatus[i][0] == boardStatus[i][1] && boardStatus[i][1] == boardStatus[i][2] && boardStatus[i][0] != 0) {
                showWinner(boardStatus[i][0])
                return
            }
            if (boardStatus[0][i] == boardStatus[1][i] && boardStatus[1][i] == boardStatus[2][i] && boardStatus[0][i] != 0) {
                showWinner(boardStatus[0][i])
                return
            }
        }

        // Check diagonals
        if (boardStatus[0][0] == boardStatus[1][1] && boardStatus[1][1] == boardStatus[2][2] && boardStatus[0][0] != 0) {
            showWinner(boardStatus[0][0])
            return
        }
        if (boardStatus[0][2] == boardStatus[1][1] && boardStatus[1][1] == boardStatus[2][0] && boardStatus[0][2] != 0) {
            showWinner(boardStatus[0][2])
            return
        }

        // Check for draw
        if (board.none { it?.isClickable == true }) {
            statusTextView.text = "It's a draw!"
        } else {
            statusTextView.text = if (activePlayer) "Player X's turn" else "Player O's turn"
        }
    }

    private fun showWinner(player: Int) {
        val statusTextView: TextView = findViewById(R.id.statusTextView)
        statusTextView.text = if (player == 1) "Player X wins!" else "Player O wins!"

        // Disable all buttons
        for (button in board) {
            button?.isClickable = false
        }
    }

    private fun resetBoard() {
        for (i in board.indices) {
            board[i]?.setImageResource(0) // Remove image
            board[i]?.isClickable = true
        }
        boardStatus = Array(3) { IntArray(3) }
        activePlayer = true
        val statusTextView: TextView = findViewById(R.id.statusTextView)
        statusTextView.text = "Player X's turn"
    }

    fun onResetClick(view: View) {
        resetBoard()
    }
}
