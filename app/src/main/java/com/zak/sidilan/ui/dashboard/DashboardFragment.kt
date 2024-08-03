package com.zak.sidilan.ui.dashboard

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
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
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hawkManager = HawkManager(requireActivity())
        binding.shimmerView.startShimmer()
        Handler(Looper.getMainLooper()).postDelayed({
            setViewModel()
            setAction()
            setView()
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
        binding.itemStatus1.tvItemTitle.text = "Pembayaran Belum Diterima"
        binding.itemStatus2.tvItemTitle.text = "Perjanjian Kerja Sama Selesai"

        binding.itemStock1.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_bookshelf, null)
        binding.itemStock2.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_warehouse, null)
        binding.itemStock3.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_rupiah, null)
        binding.itemSales1.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_cart_check, null)
        binding.itemSales2.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_rupiah, null)
        binding.itemStatus1.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_delayed_payment, null)
        binding.itemStatus2.btnIcon.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_contract, null)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

        viewModel.getSales()
        viewModel.totalSalesQty.observe(viewLifecycleOwner) { totalSalesQty ->
            binding.itemSales1.tvItemValue.text = getString(R.string.total_stock_qty, totalSalesQty.toString())
        }
        viewModel.totalSales.observe(viewLifecycleOwner) { totalSales ->
            binding.itemSales2.tvItemValue.text = Formatter.addThousandSeparatorTextView(totalSales)
        }

        viewModel.getSalesUndone()
        viewModel.totalSalesUndone.observe(viewLifecycleOwner) { totalUndone ->
            binding.itemStatus1.tvItemValue.text = getString(R.string.transaction, totalUndone.toString())
        }

        val currentDate = LocalDate.now()
        val fullFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("id", "ID"))
        val fullDate = currentDate.format(fullFormat)

        viewModel.getFilteredBookContract(fullDate)
        viewModel.totalBookContractDone.observe(viewLifecycleOwner) { totalContractDone ->
            binding.itemStatus2.tvItemValue.text = getString(R.string.total_stock_qty, totalContractDone.toString())
        }

        Handler(Looper.getMainLooper()).postDelayed({
            binding.userCard.root.visibility = View.VISIBLE
            binding.cardSaleSummary.visibility = View.VISIBLE
            binding.cardStockSummary.visibility = View.VISIBLE
            binding.cardBookStatus.visibility = View.VISIBLE
            binding.shimmerView.visibility = View.GONE
            binding.shimmerViewUser.visibility = View.GONE
            binding.shimmerView.stopShimmer()
            binding.shimmerViewUser.stopShimmer()
        }, 1500)

    }

    private fun setAction() {
        val currentUser = hawkManager.retrieveData<User>("user")

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
        binding.edStartDate.setOnClickListener {
            showDatePicker(binding.edStartDate, "Mulai")
        }
        binding.edEndDate.setOnClickListener {
            showDatePicker(binding.edEndDate, "Selesai")
        }
        binding.cbLifetime.setOnCheckedChangeListener { _, isChecked ->
            binding.edlStartDate.isEnabled = !isChecked
            binding.edlEndDate.isEnabled = !isChecked
            binding.edStartDate.text?.clear()
            binding.edEndDate.text?.clear()
        }
        binding.btnApplyFilter.setOnClickListener {
            if (binding.edStartDate.text?.isEmpty() == false && binding.edEndDate.text?.isEmpty() == false) {
                MotionToast.createColorToast(requireActivity(),
                    "Range",
                   "${Formatter.convertDateFirebaseToDisplay(binding.edStartDate.text.toString())} - ${Formatter.convertDateFirebaseToDisplay(binding.edEndDate.text.toString())}",
                    MotionToastStyle.INFO,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(requireActivity(), www.sanju.motiontoast.R.font.helvetica_regular))
                binding.tvPeriodDate.text = getString(R.string.filter_period,"${Formatter.convertDateFirebaseToDisplay(binding.edStartDate.text.toString())} - ${Formatter.convertDateFirebaseToDisplay(binding.edEndDate.text.toString())}")
                viewModel.getFilteredSales(binding.edStartDate.text.toString(), binding.edEndDate.text.toString())
            } else if (binding.cbLifetime.isChecked) {
                MotionToast.createColorToast(requireActivity(),
                    "Range",
                    "Lifetime",
                    MotionToastStyle.INFO,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(requireActivity(), www.sanju.motiontoast.R.font.helvetica_regular))
                binding.tvPeriodDate.text = getString(R.string.filter_period,"Lifetime")
                viewModel.getSales()
            } else {
                MotionToast.createColorToast(requireActivity(),
                    "Error",
                    "Tanggal tidak lengkap!",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(requireActivity(), www.sanju.motiontoast.R.font.helvetica_regular))
            }
        }

    }

    private fun showDatePicker(dateEditText: TextView, title: String) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDay = String.format("%02d", selectedDay)
                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val selectedDate = "$formattedDay/$formattedMonth/$selectedYear"
                dateEditText.text = selectedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.setTitle(title)
        datePickerDialog.show()
    }
}