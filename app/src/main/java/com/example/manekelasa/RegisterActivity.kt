package com.example.manekelasa

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.manekelasa.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val vm: WorkerViewModel by viewModels()
    private var selectedRole = "seeker"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spinnerSkill.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.skills_array))

        selectRole("seeker")
        binding.cardWorker.setOnClickListener { selectRole("worker") }
        binding.cardSeeker.setOnClickListener { selectRole("seeker") }

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString()
            if (name.isEmpty() || email.isEmpty() || pass.length < 6) {
                Toast.makeText(this, "Fill all fields (password min 6 chars)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            vm.register(email, pass, selectedRole, name)
        }

        binding.tvLogin.setOnClickListener { finish() }

        vm.uiState.observe(this) { state ->
            binding.progressBar.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
            when (state) {
                is UiState.Error -> Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                is UiState.Success<*> -> {
                    val role = state.data as? String ?: "seeker"
                    startActivity(Intent(this,
                        if (role == "worker") WorkerDashboardActivity::class.java
                        else SeekerDashboardActivity::class.java))
                    finishAffinity()
                }
                else -> {}
            }
        }
    }

    private fun selectRole(role: String) {
        selectedRole = role
        binding.workerFields.visibility = if (role == "worker") View.VISIBLE else View.GONE
        binding.cardWorker.setCardBackgroundColor(
            if (role == "worker") getColor(R.color.card_selected) else getColor(R.color.surface))
        binding.cardSeeker.setCardBackgroundColor(
            if (role == "seeker") getColor(R.color.card_selected) else getColor(R.color.surface))
    }
}
