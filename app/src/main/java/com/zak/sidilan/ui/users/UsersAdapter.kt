package com.zak.sidilan.ui.users

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.databinding.LayoutUserCardBinding

class UsersAdapter(
    private val context: Context,
    private val viewModel: UserManagementViewModel,
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
            viewModel.validateWhitelist(user.email.toString()) { isWhitelisted ->
                adapterBinding.tvUserAction.text = user.displayName
                adapterBinding.ivProfilePicture.load(user.photoUrl)
                if (isWhitelisted) {
                    adapterBinding.tvUserName.text = context.getString(R.string.two_lines, user.email, user.role)
                    adapterBinding.cardUser.alpha = 1F
                } else {
                    adapterBinding.tvUserName.text = context.getString(R.string.two_lines_variant, user.email, user.role)
                    adapterBinding.cardUser.alpha = 0.5F
                }
            }
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
