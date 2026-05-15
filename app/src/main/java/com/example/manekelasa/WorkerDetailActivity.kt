package com.example.manekelasa

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.manekelasa.databinding.ActivityWorkerDetailBinding

class WorkerDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWorkerDetailBinding
    private val vm: WorkerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val workerId = intent.getStringExtra("WORKER_ID") ?: ""
        val name = intent.getStringExtra("NAME") ?: ""
        val skill = intent.getStringExtra("SKILL") ?: ""
        val phone = intent.getStringExtra("PHONE") ?: ""
        val area = intent.getStringExtra("AREA") ?: ""
        val town = intent.getStringExtra("TOWN") ?: ""
        val rate = intent.getIntExtra("RATE", 0)
        val available = intent.getBooleanExtra("AVAILABLE", false)
        val thumbs = intent.getIntExtra("THUMBS", 0)

        supportActionBar?.title = name
        binding.tvDetailName.text = name
        binding.tvDetailSkill.text = skill
        binding.tvDetailArea.text = "📍 $area, $town"
        binding.tvDetailRate.text = "💰 ₹$rate / day"
        binding.tvDetailThumbs.text = "👍 $thumbs"
        binding.tvDetailAvailable.text = if (available) "✅ Available Today" else "⛔ Not Available"

        binding.btnDetailCall.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
        }
        binding.btnDetailThumbsUp.setOnClickListener {
            vm.thumbsUp(workerId)
            binding.tvDetailThumbs.text = "👍 ${thumbs + 1}"
            binding.btnDetailThumbsUp.isEnabled = false
            binding.btnDetailThumbsUp.text = "✅ Rated!"
        }
        binding.btnViewOnMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java).apply { putExtra("QUERY", "$area $town") })
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
