package com.e.pooltool.charts

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class PercentValueFormatter :ValueFormatter(){

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return "${value.toInt()} %"
    }
}