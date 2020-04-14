package com.e.pooltool

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.player_records.*

class PlayerRecordsActivity : AppCompatActivity() {

    private lateinit var viewModel: MyViewModel
    private lateinit var playerRecordAdapter: PlayerRecordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_records)
        setupLineChartData()
        getViewModel()
        setAdapter()
        initData()
    }

    private fun getViewModel() {
        viewModel = this.run {
            ViewModelProviders.of(
                this,
                SavedStateViewModelFactory(application, this)
            )[MyViewModel::class.java]
        }
    }

    private fun setAdapter() {
        playerRecordAdapter = PlayerRecordAdapter(viewModel, getList(), this)
        rvRecords.adapter = playerRecordAdapter
        rvRecords.layoutManager = LinearLayoutManager(this)
    }

    private fun getList(): ArrayList<PlayerRecord> {
        val list: ArrayList<PlayerRecord> = arrayListOf()
        list.add(PlayerRecord("Test", 8, 14, "Apr 14, 9:36 AM"))
        list.add(PlayerRecord("Test", 7, 11, "Apr 13, 10:26 AM"))
        list.add(PlayerRecord("Test", 8, 17, "Apr 9, 9:02 PM"))
        list.add(PlayerRecord("Test", 8, 6, "Apr 5, 6:16 pM"))
        list.add(PlayerRecord("Test", 8, 9, "Apr 4, 8:20 PM"))
        list.add(PlayerRecord("Test", 8, 14, "Apr 1, 5:44 PM"))
        list.add(PlayerRecord("Test", 8, 16, "Mar 31, 03:54 PM"))
        list.add(PlayerRecord("Test", 8, 13, "Mar 29, 10:21 AM"))
        list.add(PlayerRecord("Test", 8, 13, "Mar 28, 7:55 AM"))
        return list
    }

    private fun initData() {
        // set player name
        val name = PlayerNames().getName()


        val list = getList()
        var potted: Int = 0
        var missed: Int = 0

        for (i in 0 until list.size) {
            potted += list[i].potted
            missed += list[i].missed
        }

        val player = Player(name, potted, missed)

        tvPlayerName.text = name
        tvPottedRecord.text = getString(R.string.potted_record, potted)
        tvMissedRecord.text = getString(R.string.missed_record, missed)
        tvAve.text = player.getRate().replace("%","")
    }

    private fun setupLineChartData() {
        val yVals = ArrayList<Entry>()
        yVals.add(Entry(0f, 30f, "0"))
        yVals.add(Entry(1f, 2f, "1"))
        yVals.add(Entry(2f, 4f, "2"))
        yVals.add(Entry(3f, 6f, "3"))
        yVals.add(Entry(4f, 8f, "4"))
        yVals.add(Entry(5f, 10f, "5"))
        yVals.add(Entry(6f, 22f, "6"))
        yVals.add(Entry(7f, 12.5f, "7"))
        yVals.add(Entry(8f, 22f, "8"))
        yVals.add(Entry(9f, 32f, "9"))
        yVals.add(Entry(10f, 54f, "10"))
        yVals.add(Entry(11f, 28f, "11"))

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

        // set data
        lineChart.data = data
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.setPinchZoom(true)
        lineChart.xAxis.enableGridDashedLine(5f, 5f, 0f)
        lineChart.axisRight.enableGridDashedLine(5f, 5f, 0f)
        lineChart.axisLeft.enableGridDashedLine(5f, 5f, 0f)
        //lineChart.setDrawGridBackground()
        lineChart.xAxis.labelCount = 11
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
    }
}