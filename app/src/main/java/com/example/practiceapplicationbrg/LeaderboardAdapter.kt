package com.example.practiceapplicationbrg

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class LeaderboardEntry(
    val HighScore: Int,
    val Username: String,
    val GameName: String
)

class LeaderboardAdapter(
    private var entries: List<LeaderboardEntry>,
    private val onUserClick: (String) -> Unit  // Lambda to handle user click
) : RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {


    class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val scoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.leaderboard_item, parent, false)
        return LeaderboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val entry = entries[position]
        holder.usernameTextView.text = entry.Username
        holder.scoreTextView.text = entry.HighScore.toString()

        holder.itemView.setOnClickListener {
            Log.d("LeaderboardAdapter", "Clicked on user: ${entry.Username}")
            onUserClick(entry.Username)  // Pass selected username to the click handler
        }
    }

    override fun getItemCount(): Int = entries.size

    fun updateData(newEntries: List<LeaderboardEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }
}
