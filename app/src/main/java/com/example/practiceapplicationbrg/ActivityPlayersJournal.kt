package com.example.practiceapplicationbrg

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ActivityPlayersJournal : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var tierAdapter: TierAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players_journal)

        recyclerView = findViewById(R.id.TierRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val tiers = listOf(
            Tier("Fresh Meat", 100, R.drawable.fresh_meat),
//            Tier("Infected", 200, R.drawable.infected),
//            Tier("Walker", 300, R.drawable.walker),
//            Tier("Tier 4", 400, R.drawable.tier4),
//            Tier("Tier 5", 500, R.drawable.tier5),
//            Tier("Tier 6", 600, R.drawable.tier6),
//            Tier("Tier 7", 700, R.drawable.tier7),
//            Tier("Tier 8", 800, R.drawable.tier8),
//            Tier("Tier 9", 900, R.drawable.tier9)
        )

        tierAdapter = TierAdapter(tiers)
        recyclerView.adapter = tierAdapter
    }
}