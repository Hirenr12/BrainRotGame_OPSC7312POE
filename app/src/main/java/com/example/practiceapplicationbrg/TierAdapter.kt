package com.example.practiceapplicationbrg

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TierAdapter(private val tiers: List<Tier>) :
    RecyclerView.Adapter<TierAdapter.TierViewHolder>() {

    // ViewHolder class to hold references to the views for each list item
    class TierViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tierImage: ImageView = itemView.findViewById(R.id.tierImage)
        val tierTitle: TextView = itemView.findViewById(R.id.tierTitle)
        val tierPoints: TextView = itemView.findViewById(R.id.tierPoints)
    }

    // Inflates the item layout and returns the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TierViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tier_item, parent, false)
        return TierViewHolder(view)
    }

    // Binds the data to the views in each item
    override fun onBindViewHolder(holder: TierViewHolder, position: Int) {
        val tier = tiers[position]
        holder.tierTitle.text = tier.name
        holder.tierPoints.text = "Points required: ${tier.requiredPoints}"
        holder.tierImage.setImageResource(tier.imageResId)

        // Handle touch event to scale the image
        holder.tierImage.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Scale up when pressed
                    val scaleUp = AnimationUtils.loadAnimation(view.context, R.anim.scale_up)
                    view.startAnimation(scaleUp)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Scale down when released or canceled
                    val scaleDown = AnimationUtils.loadAnimation(view.context, R.anim.scale_down)
                    view.startAnimation(scaleDown)
                }
            }
            true
        }
    }

    // Returns the size of the data list
    override fun getItemCount(): Int {
        return tiers.size
    }
}
