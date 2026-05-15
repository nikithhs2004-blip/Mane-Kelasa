package com.example.manekelasa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.manekelasa.databinding.ItemJobRequestBinding
import java.text.SimpleDateFormat
import java.util.*

class JobRequestAdapter(
    private val onClick: (JobRequest) -> Unit
) : ListAdapter<JobRequest, JobRequestAdapter.VH>(DIFF) {

    inner class VH(val b: ItemJobRequestBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemJobRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val req = getItem(position); val b = holder.b
        b.tvSeekerName.text = req.seekerName
        b.tvSkillNeeded.text = "Needs: ${req.skillNeeded}"
        b.tvLocation.text = "📍 ${req.area}, ${req.town}"
        b.tvBudget.text = if (req.budgetPerDay > 0) "₹${req.budgetPerDay}/day" else ""
        b.tvTime.text = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(req.timestamp))
        b.root.setOnClickListener { onClick(req) }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<JobRequest>() {
            override fun areItemsTheSame(a: JobRequest, b: JobRequest) = a.id == b.id
            override fun areContentsTheSame(a: JobRequest, b: JobRequest) = a == b
        }
    }
}
