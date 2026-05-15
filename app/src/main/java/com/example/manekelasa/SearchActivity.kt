package com.example.manekelasa

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manekelasa.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private val vm: WorkerViewModel by viewModels()
    private lateinit var adapter: WorkerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val skills = listOf("All") + resources.getStringArray(R.array.skills_array).toList()
        val towns = resources.getStringArray(R.array.towns_array).toList()
        binding.spinnerSkill.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, skills)
        binding.spinnerTown.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, towns)

        adapter = WorkerAdapter(
            onCall = { w -> startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${w.phone}"))) },
            onThumbsUp = { w -> vm.thumbsUp(w.id) },
            onClick = { w ->
                startActivity(Intent(this, WorkerDetailActivity::class.java).apply {
                    putExtra("WORKER_ID", w.id); putExtra("NAME", w.name)
                    putExtra("SKILL", w.skill); putExtra("PHONE", w.phone)
                    putExtra("AREA", w.area); putExtra("TOWN", w.town)
                    putExtra("RATE", w.dailyRate); putExtra("AVAILABLE", w.available)
                    putExtra("THUMBS", w.thumbsUpCount)
                })
            }
        )
        binding.rvWorkers.layoutManager = LinearLayoutManager(this)
        binding.rvWorkers.adapter = adapter
        binding.btnSearch.setOnClickListener { doSearch() }
        doSearch()

        vm.workers.observe(this) { list ->
            adapter.submitList(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        vm.uiState.observe(this) { state ->
            binding.progressBar.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
        }
    }

    private fun doSearch() {
        vm.searchWorkers(
            binding.spinnerSkill.selectedItem.toString(),
            binding.spinnerTown.selectedItem.toString(),
            binding.switchAvailableOnly.isChecked)
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
