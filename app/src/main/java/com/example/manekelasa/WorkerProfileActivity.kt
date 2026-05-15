package com.example.manekelasa

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.manekelasa.databinding.ActivityWorkerProfileBinding

class WorkerProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWorkerProfileBinding
    private val vm: WorkerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val skills = resources.getStringArray(R.array.skills_array)
        binding.spinnerSkill.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, skills)

        vm.loadMyProfile()
        vm.myProfile.observe(this) { p ->
            if (p != null) {
                binding.etName.setText(p.name)
                binding.etPhone.setText(p.phone)
                binding.etArea.setText(p.area)
                binding.etTown.setText(p.town)
                binding.etRate.setText(p.dailyRate.toString())
                val idx = skills.indexOf(p.skill); if (idx >= 0) binding.spinnerSkill.setSelection(idx)
                binding.switchAvailability.isChecked = p.available
                binding.tvAvailabilityStatus.text = if (p.available) "✅  I am Available Today" else "⛔  I am Unavailable"
            }
        }

        binding.switchAvailability.setOnCheckedChangeListener { _, checked ->
            binding.tvAvailabilityStatus.text = if (checked) "✅  I am Available Today" else "⛔  I am Unavailable"
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val area = binding.etArea.text.toString().trim()
            val town = binding.etTown.text.toString().trim()
            val rate = binding.etRate.text.toString().toIntOrNull() ?: 0
            if (name.isEmpty() || phone.isEmpty() || area.isEmpty()) {
                Toast.makeText(this, "Fill Name, Phone, Area", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            vm.saveProfile(Worker(name = name, phone = phone, area = area, town = town,
                dailyRate = rate, skill = binding.spinnerSkill.selectedItem.toString(),
                role = "worker", available = binding.switchAvailability.isChecked))
        }

        vm.uiState.observe(this) { state ->
            binding.progressBar.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
            when (state) {
                is UiState.Error -> Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                is UiState.Success<*> -> { Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show(); finish() }
                else -> {}
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
