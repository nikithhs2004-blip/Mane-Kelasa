package com.example.manekelasa

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.manekelasa.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val vm: WorkerViewModel by viewModels()
    private var selectedRole = "worker"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectRole("worker")
        binding.cardWorker.setOnClickListener { selectRole("worker") }
        binding.cardSeeker.setOnClickListener { selectRole("seeker") }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString()
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            vm.login(email, pass)
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        vm.uiState.observe(this) { state ->
            binding.progressBar.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
            when (state) {
                is UiState.Error -> Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                is UiState.Success<*> -> {
                    val role = state.data as? String ?: "seeker"
                    startActivity(Intent(this,
                        if (role == "worker") WorkerDashboardActivity::class.java
                        else SeekerDashboardActivity::class.java))
                    finish()
                }
                else -> {}
            }
        }
    }

    private fun selectRole(role: String) {
        selectedRole = role
        binding.cardWorker.setCardBackgroundColor(
            if (role == "worker") getColor(R.color.card_selected) else getColor(R.color.surface))
        binding.cardSeeker.setCardBackgroundColor(
            if (role == "seeker") getColor(R.color.card_selected) else getColor(R.color.surface))
    }
}
