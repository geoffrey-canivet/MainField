package com.example.mainfieldmanager.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mainfieldmanager.R
import com.example.mainfieldmanager.model.Trophee

class TropheeAdapter(
    private val trophees: List<Trophee>
) : RecyclerView.Adapter<TropheeAdapter.TropheeViewHolder>() {

    // ViewHolder : Récupère les éléments de l'item
    inner class TropheeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tropheeName: TextView = itemView.findViewById(R.id.cardTropheeName)
        val tropheeDescription: TextView = itemView.findViewById(R.id.cardTropheeDescription)
        val tropheeDate: TextView = itemView.findViewById(R.id.cardTropheeDate)
        val tropheeImage: ImageView = itemView.findViewById(R.id.imgTrophee)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TropheeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tropheecard, parent, false)
        return TropheeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TropheeViewHolder, position: Int) {
        val trophee = trophees[position]
        holder.tropheeName.text = trophee.name
        holder.tropheeDescription.text = trophee.description
        holder.tropheeDate.text = trophee.date
        when (trophee.img) {
            "t1" -> holder.tropheeImage.setImageResource(R.drawable.t1)
            "t2" -> holder.tropheeImage.setImageResource(R.drawable.t2)
            "t3" -> holder.tropheeImage.setImageResource(R.drawable.t3)
            "t4" -> holder.tropheeImage.setImageResource(R.drawable.t4)
            "t5" -> holder.tropheeImage.setImageResource(R.drawable.t5)
            "t6" -> holder.tropheeImage.setImageResource(R.drawable.t6)
            "t7" -> holder.tropheeImage.setImageResource(R.drawable.t7)
            "t8" -> holder.tropheeImage.setImageResource(R.drawable.t8)
        }


    }

    override fun getItemCount() = trophees.size
}
