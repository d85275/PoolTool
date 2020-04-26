package com.e.pooltool.charts

import android.content.Context
import android.widget.TextView
import com.e.pooltool.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF


class DataMarker(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {
    private val textView = findViewById<TextView>(R.id.tvContent)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e!!.y == 100f) {
            textView.text = "100 %"
        } else {
            textView.text = "${e!!.y} %"
        }
        super.refreshContent(e, highlight)
    }

    private var mOffset: MPPointF? = null

    override fun getOffset(): MPPointF? {
        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
        }
        return mOffset
    }
}