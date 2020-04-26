package com.e.pooltool.charts

import android.graphics.Color
import com.e.pooltool.MyViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class PlayerRecordChart(private val viewModel: MyViewModel, private val lineChart: LineChart) {


    fun setup() {
        val yVals = getCharData()

        val set1: LineDataSet
        set1 = LineDataSet(yVals, "DataSet 1")

        //set1.fillAlpha = 110
        //set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        // set1.enableDashedLine(5f, 5f, 0f);
        // set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.color = Color.DKGRAY
        set1.setCircleColor(Color.DKGRAY)
        set1.lineWidth = 1f
        set1.circleRadius = 3f
        set1.setDrawCircleHole(false)
        set1.valueTextSize = 0f
        set1.setDrawFilled(false)

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(set1)
        val data = LineData(dataSets)

        // clear the previous data before setting a new set of data
        lineChart.clear()
        lineChart.data = data
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.setPinchZoom(true)

        setX()
        setY()
    }

    private fun setX() {
        lineChart.axisLeft.isEnabled = false
        lineChart.axisRight.enableGridDashedLine(5f, 5f, 0f)
        //lineChart.axisLeft.enableGridDashedLine(5f, 5f, 0f)
        lineChart.axisRight.valueFormatter = PercentValueFormatter()
        //lineChart.axisLeft.valueFormatter = PercentValueFormatter()
        lineChart.axisRight.textColor = Color.GRAY
        //lineChart.axisLeft.textColor = Color.GRAY

    }

    private fun setY() {
        lineChart.xAxis.enableGridDashedLine(5f, 5f, 0f)
        lineChart.xAxis.labelCount = viewModel.getLabelCount()
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.valueFormatter = DateValueFormatter(viewModel.getDisplayedRecordsList())
        lineChart.xAxis.labelRotationAngle = -45f
        lineChart.xAxis.textSize = 10f
        lineChart.xAxis.textColor = Color.GRAY
    }

    private fun getCharData(): ArrayList<Entry> {
        val data = ArrayList<Entry>()
        val list = viewModel.getDisplayedRecordsList()
        for (i in list.indices) {
            val rate = list[i].rate.replace("%", "").trim().toFloat()
            val entry = Entry(i.toFloat(), rate)
            data.add(entry)
        }
        return data
    }
}