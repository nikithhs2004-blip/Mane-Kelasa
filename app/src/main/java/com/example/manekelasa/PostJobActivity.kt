package com.example.manekelasa

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.manekelasa.databinding.ActivityPostJobBinding

class PostJobActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostJobBinding
    private val vm: WorkerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostJobBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.spinnerSkill.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.skills_array))
        binding.spinnerTown.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.towns_array))

        binding.btnPost.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val area = binding.etArea.text.toString().trim()
            if (name.isEmpty() || area.isEmpty()) {
                Toast.makeText(this, "Name and area required", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            vm.postJobRequest(JobRequest(
                seekerName = name, skillNeeded = binding.spinnerSkill.selectedItem.toString(),
                area = area, town = binding.spinnerTown.selectedItem.toString(),
                description = binding.etDescription.text.toString().trim(),
                budgetPerDay = binding.etBudget.text.toString().toIntOrNull() ?: 0))
        }

        vm.uiState.observe(this) { state ->
            binding.progressBar.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
            when (state) {
                is UiState.Error -> Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                is UiState.Success<*> -> { Toast.makeText(this, "Job posted!", Toast.LENGTH_SHORT).show(); finish() }
                else -> {}
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
