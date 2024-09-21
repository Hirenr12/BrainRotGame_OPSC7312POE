package com.example.practiceapplicationbrg
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GameAdapter(
    private val games: MutableList<Game>,
    private val onFavoriteClick: (Game) -> Unit,
    private val onGameClick: (Game) -> Unit // New parameter for game click
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.bind(game)

        holder.favoriteIcon.setOnClickListener {
            onFavoriteClick(game)
        }

        holder.itemView.setOnClickListener { // Add click listener for the entire item
            onGameClick(game) // Call the new callback
        }
    }

    override fun getItemCount(): Int = games.size

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameImage: ImageView = itemView.findViewById(R.id.gameImage)
        val gameTitle: TextView = itemView.findViewById(R.id.gameTitle)
        val favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteIcon)

        fun bind(game: Game) {
            gameImage.setImageResource(game.imageResId)
            gameTitle.text = game.title
            favoriteIcon.setImageResource(if (game.isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)
        }
    }
}