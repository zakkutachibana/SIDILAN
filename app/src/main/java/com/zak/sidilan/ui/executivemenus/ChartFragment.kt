package com.zak.sidilan.ui.executivemenus

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import org.koin.androidx.viewmodel.ext.android.viewModel


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
        return binding.root
    }

    fun setupViewModel() {
        viewModel.calculateTotalBookQty()
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

}