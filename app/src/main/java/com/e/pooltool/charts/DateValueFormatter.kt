package com.e.pooltool.charts

import com.e.pooltool.database.PlayerRecordItem
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DateValueFormatter(private var playerRecord: ArrayList<PlayerRecordItem>) : ValueFormatter() {


    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        // do nothing if the size for the player record is zero
        if (playerRecord.size == 0) {
            return super.getAxisLabel(value, axis)
        }
        val input = playerRecord[value.toInt()].date
        val outputFormat = SimpleDateFormat("MM/dd", Locale.ENGLISH)
        val inputFormatter = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH)
        val date = inputFormatter.parse(input)
        return outputFormat.format(date)
    }
}