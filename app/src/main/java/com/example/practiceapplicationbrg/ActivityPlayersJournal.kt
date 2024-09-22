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
            Tier("Infected", 200, R.drawable.infected),
            Tier("Walker", 300, R.drawable.walker),
            Tier("Rotter", 400, R.drawable.rotter),
            Tier("Revenant 5", 500, R.drawable.revenant),
            Tier("Night stalker 6", 600, R.drawable.nightstalker),
            Tier("Necromancer 7", 700, R.drawable.necromancer),
            Tier("Warlord 8", 800, R.drawable.warlord),
            Tier("Dread lord", 900, R.drawable.dreadlord)
        )

        tierAdapter = TierAdapter(tiers)
        recyclerView.adapter = tierAdapter
    }
}