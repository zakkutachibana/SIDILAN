package com.zak.sidilan.ui.users

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zak.sidilan.data.entities.Whitelist
import com.zak.sidilan.databinding.LayoutUserCardBinding

class WhitelistAdapter(
    private val context: Context,
    private val onClickListener: (Whitelist) -> Unit
) : ListAdapter<Whitelist, WhitelistAdapter.WhitelistViewHolder>(WhitelistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WhitelistViewHolder {
        val binding = LayoutUserCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WhitelistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WhitelistViewHolder, position: Int) {
        val whitelist = getItem(position)
        holder.bind(whitelist)
    }

    inner class WhitelistViewHolder(private val adapterBinding: LayoutUserCardBinding) : RecyclerView.ViewHolder(adapterBinding.root) {
        init {
            adapterBinding.cardUser.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClickListener(getItem(position))
                }
            }
        }

        fun bind(whitelist: Whitelist) {
            adapterBinding.tvUserAction.text = whitelist.email
            adapterBinding.tvUserName.text = whitelist.role
            adapterBinding.ivProfilePicture.visibility = View.GONE
        }
    }

    class WhitelistDiffCallback : DiffUtil.ItemCallback<Whitelist>() {
        override fun areItemsTheSame(oldItem: Whitelist, newItem: Whitelist): Boolean {
            return oldItem.email == newItem.email
        }

        override fun areContentsTheSame(oldItem: Whitelist, newItem: Whitelist): Boolean {
            return oldItem == newItem
        }
    }
}
