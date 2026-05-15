package com.example.manekelasa

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manekelasa.databinding.ActivityWorkerDashboardBinding

class WorkerDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWorkerDashboardBinding
    private val vm: WorkerViewModel by viewModels()
    private lateinit var adapter: JobRequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        adapter = JobRequestAdapter { req ->
            startActivity(Intent(this, SeekerDetailActivity::class.java).apply {
                putExtra("SEEKER_NAME", req.seekerName)
                putExtra("SKILL", req.skillNeeded)
                putExtra("AREA", req.area)
                putExtra("TOWN", req.town)
                putExtra("DESCRIPTION", req.description)
                putExtra("BUDGET", req.budgetPerDay)
            })
        }
        binding.rvJobRequests.layoutManager = LinearLayoutManager(this)
        binding.rvJobRequests.adapter = adapter

        vm.myProfile.observe(this) { profile ->
            if (profile != null) {
                binding.tvAvailabilityStatus.text =
                    if (profile.available) "✅  I am Available Today" else "⛔  I am Unavailable"
                binding.switchAvailability.isChecked = profile.available
                vm.loadJobRequests(profile.skill)
            } else {
                startActivity(Intent(this, WorkerProfileActivity::class.java))
            }
        }

        binding.switchAvailability.setOnCheckedChangeListener { _, checked ->
            vm.setAvailability(checked)
            binding.tvAvailabilityStatus.text =
                if (checked) "✅  I am Available Today" else "⛔  I am Unavailable"
        }

        binding.btnMyProfile.setOnClickListener {
            startActivity(Intent(this, WorkerProfileActivity::class.java))
        }

        vm.jobRequests.observe(this) { list ->
            adapter.submitList(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        vm.uiState.observe(this) { state ->
            binding.progressBar.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() { super.onResume(); vm.loadMyProfile() }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu); return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            vm.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
