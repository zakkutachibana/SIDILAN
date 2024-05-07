package com.zak.sidilan.util

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.zak.sidilan.R

class CustomBubbleMarker(context: Context) : MarkerView(
    context, R.layout.layout_chart_marker
) {

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            val titleTv: TextView = this.findViewById(R.id.tv_marker_value)
            titleTv.text = context.getString(R.string.total_stock_qty, it.y.toLong().toString())
        }
        super.refreshContent(e, highlight)
    }

}