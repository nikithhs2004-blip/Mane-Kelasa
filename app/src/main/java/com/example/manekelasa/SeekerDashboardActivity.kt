package com.example.manekelasa

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.manekelasa.databinding.ActivitySeekerDashboardBinding

class SeekerDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySeekerDashboardBinding
    private val vm: WorkerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeekerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.btnFindWorker.setOnClickListener { startActivity(Intent(this, SearchActivity::class.java)) }
        binding.btnPostJob.setOnClickListener { startActivity(Intent(this, PostJobActivity::class.java)) }
        binding.btnViewMap.setOnClickListener { startActivity(Intent(this, MapActivity::class.java)) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu); return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            vm.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity(); return true
        }
        return super.onOptionsItemSelected(item)
    }
}
