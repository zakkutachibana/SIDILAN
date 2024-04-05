package com.zak.sidilan.ui.dashboard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.zak.sidilan.R
import com.zak.sidilan.databinding.FragmentDashboardBinding
import com.zak.sidilan.ui.addbook.AddBookActivity
import com.zak.sidilan.util.AuthManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

val dashboardFragmentModule = module {
    factory { AddBookActivity() }
}
class DashboardFragment : Fragment() {
    private val viewModel: DashboardViewModel by viewModel()
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setView()

        Handler(Looper.getMainLooper()).postDelayed({
            setViewModel()
            setAction()
        }, 300)
    }

    private fun setView() {
        val currentUser = AuthManager.getCurrentUser()
        binding.userCard.tvUserAction.text = currentUser.displayName
        binding.userCard.tvUserName.text = currentUser.email
        binding.userCard.ivProfilePicture.load(currentUser.photoUrl)

        binding.itemStock1.tvItemTitle.text = "Item Buku"
        binding.itemStock2.tvItemTitle.text = "Total Stok"
        binding.itemStock3.tvItemTitle.text = "Total Value"
        binding.itemStock3.tvItemValue.text = "30.000.000"
        binding.itemSales1.tvItemTitle.text = "Buku Terjual"
        binding.itemSales1.tvItemValue.text = "379 buku"
        binding.itemSales2.tvItemTitle.text = "Pendapatan"
        binding.itemSales2.tvItemValue.text = "10.000.000"
    }

    private fun setViewModel() {
        binding.itemStock1.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_bookshelf, null)
        binding.itemStock2.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_warehouse, null)
        binding.itemStock3.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_rupiah, null)
        binding.itemSales1.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_cart_check, null)
        binding.itemSales2.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_rupiah, null)

        viewModel.getBookCount()
        viewModel.bookCount.observe(viewLifecycleOwner) { bookCount ->
            binding.itemStock1.tvItemValue.text = getString(R.string.book_count, bookCount.toString())
        }
        viewModel.getTotalStockQty()
        viewModel.totalStockQty.observe(viewLifecycleOwner) { totalStockQty ->
            binding.itemStock2.tvItemValue.text = getString(R.string.total_stock_qty, totalStockQty.toString())
        }
    }

    private fun setAction() {
        binding.btnExpand.setOnClickListener {
            val expVis = if (binding.clExpand.visibility == View.GONE) {
                binding.btnExpand.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_up, null)
                View.VISIBLE
            } else {
                binding.btnExpand.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_down, null)
                View.GONE
            }
            binding.clExpand.visibility = expVis
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}