package com.zak.sidilan.ui.executivemenus

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.zak.sidilan.R
import com.zak.sidilan.data.entities.BookOutSellingTrx
import com.zak.sidilan.databinding.FragmentChartBinding
import com.zak.sidilan.ui.trxhistory.TrxHistoryViewModel
import com.zak.sidilan.util.CustomBubbleMarker
import com.zak.sidilan.util.Formatter
import org.koin.androidx.viewmodel.ext.android.viewModel
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.Calendar


class ChartFragment : Fragment() {
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!
    private lateinit var pieChart: PieChart
    private val viewModel: TrxHistoryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)
        setupViewModel()
        setupAction()
        return binding.root
    }

    fun setupViewModel() {
        viewModel.getAllSalesTrx()
        viewModel.trxSellingList.observe(viewLifecycleOwner){ trxSellList ->
            val shopeeQty = trxSellList
                .map { it.bookTrx as? BookOutSellingTrx }
                .filter { it?.sellingPlatform == "Shopee" }
                .sumOf { it?.totalBookQty ?: 0 }
            val tokopediaQty = trxSellList
                .map { it.bookTrx as? BookOutSellingTrx }
                .filter { it?.sellingPlatform == "Tokopedia" }
                .sumOf { it?.totalBookQty ?: 0 }
            val penelehQty = trxSellList
                .map { it.bookTrx as? BookOutSellingTrx }
                .filter { it?.sellingPlatform == "Yayasan" }
                .sumOf { it?.totalBookQty ?: 0 }
            val otherQty = trxSellList
                .map { it.bookTrx as? BookOutSellingTrx }
                .filter { it?.sellingPlatform == "Lainnya" }
                .sumOf { it?.totalBookQty ?: 0 }
            val whatsappQty = trxSellList
                .map { it.bookTrx as? BookOutSellingTrx }
                .filter { it?.sellingPlatform == "WhatsApp" }
                .sumOf { it?.totalBookQty ?: 0 }
            setupChart(shopeeQty, tokopediaQty, penelehQty, whatsappQty, otherQty)
        }
    }
    fun setupAction() {
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
                viewModel.getFilteredSalesByDate(binding.edStartDate.text.toString(), binding.edEndDate.text.toString())
                refreshChart()
            } else if (binding.cbLifetime.isChecked) {
                MotionToast.createColorToast(requireActivity(),
                    "Range",
                    "Lifetime",
                    MotionToastStyle.INFO,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(requireActivity(), www.sanju.motiontoast.R.font.helvetica_regular))
                binding.tvPeriodDate.text = getString(R.string.filter_period,"Lifetime")
                viewModel.getAllSalesTrx()
                refreshChart()
            } else {
                refreshChart()
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

    private fun setupChart(shopeeQty: Long, tokopediaQty: Long, penelehQty: Long, whatsappQty: Long, otherQty: Long) {
        pieChart = binding.pieChart
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.setExtraOffsets(20f, 10f, 20f, 10f)
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.holeRadius = 65f
        pieChart.rotationAngle = 0f
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true
        pieChart.animateY(1400, Easing.EaseInOutQuad)
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(shopeeQty.toFloat()))
        entries.add(PieEntry(tokopediaQty.toFloat()))
        entries.add(PieEntry(penelehQty.toFloat()))
        entries.add(PieEntry(whatsappQty.toFloat()))
        entries.add(PieEntry(otherQty.toFloat()))

        val dataSet = PieDataSet(entries, "Platform")

        dataSet.setDrawIcons(false)

        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.shopee_color))
        colors.add(resources.getColor(R.color.tokopedia_color))
        colors.add(resources.getColor(R.color.peneleh_color))
        colors.add(resources.getColor(R.color.whatsapp_color))
        colors.add(resources.getColor(R.color.other_color))

        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(14f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data

        val value = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.colorForeground, value, true)
        pieChart.setDrawCenterText(true)
        pieChart.centerText = getString(R.string.total_sold_qty, data.yValueSum.toLong().toString())
        pieChart.setCenterTextSize(20f)
        pieChart.setCenterTextColor(value.data)
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD)

        val markerView = CustomBubbleMarker(requireContext())
        pieChart.marker = markerView
        pieChart.highlightValues(null)
        pieChart.invalidate()
    }

    private fun refreshChart() {
        pieChart.data.notifyDataChanged()
        pieChart.notifyDataSetChanged()
        pieChart.highlightValues(null)
        pieChart.invalidate()
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