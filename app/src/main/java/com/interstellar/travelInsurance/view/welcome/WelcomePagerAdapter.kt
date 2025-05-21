package com.interstellar.travelInsurance.view.welcome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.interstellar.travelInsurance.core.model.WelcomeScreenItem
import com.interstellar.travelInsurance.databinding.ItemWelcomeScreenBinding


class WelcomePagerAdapter(private val welcomeItems: List<WelcomeScreenItem>) :
    RecyclerView.Adapter<WelcomePagerAdapter.WelcomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WelcomeViewHolder {
        val binding = ItemWelcomeScreenBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WelcomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WelcomeViewHolder, position: Int) {
        holder.bind(welcomeItems[position])
    }

    override fun getItemCount(): Int = welcomeItems.size

    inner class WelcomeViewHolder(private val binding: ItemWelcomeScreenBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WelcomeScreenItem) {
            binding.apply {
                welcomeImage.setImageResource(item.imageResId)
                welcomeTitle.text = item.title
                welcomeDescription.text = item.description
            }
        }
    }
}