package com.example.manekelasa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.manekelasa.databinding.ItemWorkerBinding

class WorkerAdapter(
    private val onCall: (Worker) -> Unit,
    private val onThumbsUp: (Worker) -> Unit,
    private val onClick: (Worker) -> Unit
) : ListAdapter<Worker, WorkerAdapter.VH>(DIFF) {

    inner class VH(val b: ItemWorkerBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemWorkerBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val w = getItem(position); val b = holder.b
        b.tvName.text = w.name
        b.tvSkill.text = w.skill
        b.tvArea.text = "📍 ${w.area}, ${w.town}"
        b.tvRate.text = "₹${w.dailyRate}/day"
        b.tvThumbsUp.text = "${w.thumbsUpCount}"
        b.tvAvailability.text = if (w.available) "✅ Available" else "⛔ Unavailable"
        b.tvAvailability.setBackgroundColor(
            if (w.available) b.root.context.getColor(R.color.available_green)
            else b.root.context.getColor(R.color.unavailable_red))
        b.btnCall.setOnClickListener { onCall(w) }
        b.btnThumbsUp.setOnClickListener { onThumbsUp(w) }
        b.root.setOnClickListener { onClick(w) }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Worker>() {
            override fun areItemsTheSame(a: Worker, b: Worker) = a.id == b.id
            override fun areContentsTheSame(a: Worker, b: Worker) = a == b
        }
    }
}
