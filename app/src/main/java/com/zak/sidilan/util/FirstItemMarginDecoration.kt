package com.zak.sidilan.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class FirstItemMarginDecoration(private val marginInPx: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = marginInPx
        } else {
            outRect.top = 0
        }
    }
}