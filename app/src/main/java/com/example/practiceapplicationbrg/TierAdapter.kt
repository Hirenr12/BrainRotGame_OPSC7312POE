package com.example.practiceapplicationbrg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TierAdapter(private val tiers: List<Tier>) :
    RecyclerView.Adapter<TierAdapter.TierViewHolder>() {

    class TierViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tierImage: ImageView = itemView.findViewById(R.id.tierImage)
        val tierTitle: TextView = itemView.findViewById(R.id.tierTitle)
        val tierPoints: TextView = itemView.findViewById(R.id.tierPoints)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TierViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tier_item, parent, false)
        return TierViewHolder(view)
    }

    override fun onBindViewHolder(holder: TierViewHolder, position: Int) {
        val tier = tiers[position]
        holder.tierTitle.text = tier.name
        holder.tierPoints.text = "Points required: ${tier.requiredPoints}"
        holder.tierImage.setImageResource(tier.imageResId)
    }

    override fun getItemCount(): Int {
        return tiers.size
    }
}