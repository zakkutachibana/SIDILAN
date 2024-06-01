package com.zak.sidilan.ui.stockopnamehistory

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.StockOpname
import com.zak.sidilan.databinding.LayoutPeriodStockOpnameBinding
import com.zak.sidilan.util.Formatter

class StockOpnameHistoryAdapter(
    private val context: Context,
    private val onClickListener: (StockOpname) -> Unit

) : ListAdapter<StockOpname, StockOpnameHistoryAdapter.UsersViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val binding = LayoutPeriodStockOpnameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val stockOpname = getItem(position)
        holder.bind(stockOpname)
    }

    inner class UsersViewHolder(private val adapterBinding: LayoutPeriodStockOpnameBinding) :
        RecyclerView.ViewHolder(adapterBinding.root) {
        init {
            adapterBinding.cardStockOpname.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClickListener(getItem(position))
                }
            }
        }
        fun bind(stockOpname: StockOpname) {
            adapterBinding.tvCheckDate.text = Formatter.convertDateFirebaseToDisplay(stockOpname.date)
            adapterBinding.tvPeriod.text = Formatter.convertDateMonthToDisplay(stockOpname.id)
            when (stockOpname.overallAppropriate) {
                true -> {
                    adapterBinding.ivIcon.load(R.drawable.img_check)
                    adapterBinding.tvOverallAppropriate.text = "Stok Sesuai"
                }
                false -> {
                    adapterBinding.ivIcon.load(R.drawable.img_warning)
                    adapterBinding.tvOverallAppropriate.text = "Stok Tidak Sesuai"
                }
                else -> {}
            }
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<StockOpname>() {
        override fun areItemsTheSame(oldItem: StockOpname, newItem: StockOpname): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StockOpname, newItem: StockOpname): Boolean {
            return oldItem == newItem
        }
    }
}