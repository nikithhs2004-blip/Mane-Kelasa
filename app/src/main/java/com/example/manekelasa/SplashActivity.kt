package com.example.manekelasa

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.manekelasa.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val vm: WorkerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vm.checkSession()
        vm.userRole.observe(this) { role ->
            lifecycleScope.launch {
                delay(1500)
                when (role) {
                    "worker" -> startActivity(Intent(this@SplashActivity, WorkerDashboardActivity::class.java))
                    "seeker" -> startActivity(Intent(this@SplashActivity, SeekerDashboardActivity::class.java))
                    else     -> startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                }
                finish()
            }
        }
    }
}
