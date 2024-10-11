package com.vijay.quiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vijay.quiz.databinding.ItemOptionBinding

class OptionsAdapter(
    private val options: List<String>,
    private val onOptionClick: (String) -> Unit
) : RecyclerView.Adapter<OptionsAdapter.OptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemOptionBinding.inflate(inflater, parent, false)
        return OptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val option = options[position]
        holder.bind(option)
    }

    override fun getItemCount(): Int = options.size

    inner class OptionViewHolder(private val binding: ItemOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(option: String) {
            // Bind the option text
            binding.option = option
            // Set the onClick listener for the button
            binding.optionButton.setOnClickListener {
                onOptionClick(option)
            }
        }
    }
}
