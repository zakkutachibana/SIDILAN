package com.zak.sidilan.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import coil.load
import com.zak.sidilan.MainActivity
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.User
import com.zak.sidilan.databinding.FragmentDashboardBinding
import com.zak.sidilan.ui.addbook.AddBookActivity
import com.zak.sidilan.ui.users.UserDetailActivity
import com.zak.sidilan.util.Formatter
import com.zak.sidilan.util.HawkManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

val dashboardFragmentModule = module {
    factory { AddBookActivity() }
}
class DashboardFragment : Fragment() {
    private val viewModel: DashboardViewModel by viewModel()
    private lateinit var binding: FragmentDashboardBinding
    private lateinit var hawkManager: HawkManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hawkManager = HawkManager(requireActivity())
        binding.shimmerView.startShimmer()
        Handler(Looper.getMainLooper()).postDelayed({
            setViewModel()
            setAction()
        }, 300)
    }

    private fun setView() {
        val currentUser = hawkManager.retrieveData<User>("user")

        binding.userCard.tvUserAction.text = currentUser?.displayName
        binding.userCard.tvUserName.text = getString(R.string.two_lines, currentUser?.email, currentUser?.role)
        binding.userCard.ivProfilePicture.load(currentUser?.photoUrl)

        binding.itemStock1.tvItemTitle.text = "Item Buku"
        binding.itemStock2.tvItemTitle.text = "Total Stok"
        binding.itemStock3.tvItemTitle.text = "Total Value"
        binding.itemSales1.tvItemTitle.text = "Buku Terjual"
        binding.itemSales2.tvItemTitle.text = "Total Penjualan"

        binding.itemStock1.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_bookshelf, null)
        binding.itemStock2.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_warehouse, null)
        binding.itemStock3.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_rupiah, null)
        binding.itemSales1.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_cart_check, null)
        binding.itemSales2.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_rupiah, null)

        activity?.let {
            (activity as MainActivity).binding.fab.hide()
        }
    }

    private fun setViewModel() {

        viewModel.getBookCount()
        viewModel.bookCount.observe(viewLifecycleOwner) { bookCount ->
            binding.itemStock1.tvItemValue.text = getString(R.string.book_count, bookCount.toString())
        }

        viewModel.getTotalStockQty()
        viewModel.totalStockQty.observe(viewLifecycleOwner) { totalStockQty ->
            binding.itemStock2.tvItemValue.text = getString(R.string.total_stock_qty, totalStockQty.toString())
        }

        viewModel.getTotalValue()
        viewModel.totalValue.observe(viewLifecycleOwner) { totalValue ->
            binding.itemStock3.tvItemValue.text = Formatter.addThousandSeparatorTextView(totalValue)
        }

        viewModel.getTotalSalesQty()
        viewModel.totalSalesQty.observe(viewLifecycleOwner) { totalSalesQty ->
            binding.itemSales1.tvItemValue.text = getString(R.string.total_stock_qty, totalSalesQty.toString())
        }

        viewModel.getTotalSales()
        viewModel.totalSales.observe(viewLifecycleOwner) { totalSales ->
            binding.itemSales2.tvItemValue.text = Formatter.addThousandSeparatorTextView(totalSales)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            setView()
            binding.userCard.root.visibility = View.VISIBLE
            binding.cardSaleSummary.visibility = View.VISIBLE
            binding.cardStockSummary.visibility = View.VISIBLE
            binding.shimmerView.visibility = View.GONE
            binding.shimmerView.stopShimmer()
        }, 1500)


    }

    private fun setAction() {
        val currentUser = hawkManager.retrieveData<User>("user")

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
        binding.userCard.cardUser.setOnClickListener{
            val intent = Intent(requireActivity(), UserDetailActivity::class.java)
            intent.putExtra("userId", currentUser?.id)
            startActivity(intent)
        }
        binding.drawerIcon.setOnClickListener {
            activity?.let {
                (activity as MainActivity).binding.drawerLayout.open()
            }
        }
    }
}