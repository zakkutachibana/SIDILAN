package com.zak.sidilan.ui.users

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.databinding.LayoutUserCardBinding

class UsersAdapter(
    private val context: Context,
    private val onClickListener: (User) -> Unit
) : ListAdapter<User, UsersAdapter.UsersViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val binding = LayoutUserCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    inner class UsersViewHolder(private val adapterBinding: LayoutUserCardBinding) : RecyclerView.ViewHolder(adapterBinding.root) {
        init {
            adapterBinding.cardUser.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClickListener(getItem(position))
                }
            }
        }

        fun bind(user: User) {
            adapterBinding.tvUserAction.text = user.displayName
            adapterBinding.tvUserName.text = context.getString(R.string.two_lines, user.email, user.role)
            adapterBinding.ivProfilePicture.load(user.photoUrl)
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
