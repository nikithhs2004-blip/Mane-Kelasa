package com.example.manekelasa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.manekelasa.databinding.ActivitySeekerDetailBinding

class SeekerDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySeekerDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeekerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.tvSeekerName.text = intent.getStringExtra("SEEKER_NAME") ?: ""
        binding.tvSkillNeeded.text = "🔧 Needs: ${intent.getStringExtra("SKILL") ?: ""}"
        val area = intent.getStringExtra("AREA") ?: ""
        val town = intent.getStringExtra("TOWN") ?: ""
        binding.tvLocation.text = "📍 $area, $town"
        binding.tvBudget.text = "💰 Budget: ₹${intent.getIntExtra("BUDGET", 0)}/day"
        binding.tvDescription.text = intent.getStringExtra("DESCRIPTION")
            ?.takeIf { it.isNotEmpty() } ?: "No additional details."

        binding.btnViewOnMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java).apply { putExtra("QUERY", "$area $town") })
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
