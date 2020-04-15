package com.e.pooltool

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.Description
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
        getViewModel()
        setRepository()
        registerLiveData()
        //initData()
        setAdapter()
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
        playerRecordAdapter = PlayerRecordAdapter(viewModel, viewModel.getSavedRecordsList(), this)
        rvRecords.adapter = playerRecordAdapter
        rvRecords.layoutManager = LinearLayoutManager(this)
    }

    private fun registerLiveData() {
        viewModel.getSavedRecordLiveDate().observe(this, Observer {
            initData()
            setupLineChartData()
            playerRecordAdapter.notifyDataSetChanged()
        })
        viewModel.getSavedRecords("Chao")
    }

    private fun initData() {
        Log.e("123", "init user data")
        // set player name
        val name = "Chao"


        val list = viewModel.getSavedRecordsList()
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
        tvAve.text = player.getRate().replace("%", "")
    }

    private fun setRepository() {
        viewModel.setRepository(Repository(this))
    }

    private fun showList() {
        val list = viewModel.getSavedRecordsList()
        for (i in list.indices) {
            Log.e("date", "data $i: ${list[i].name}")
            Log.e("date", "data $i: ${list[i].potted}")
            Log.e("date", "data $i: ${list[i].missed}")
            Log.e("date", "data $i: ${list[i].rate}")
            Log.e("date", "data $i: ${list[i].date}")
            Log.e("date", "data $i: ${list[i].id}")
        }
    }

    private fun getCharData(): ArrayList<Entry> {
        val data = ArrayList<Entry>()
        val list = viewModel.getSavedRecordsList()
        Log.e("t","list size: ${list.size}")
        for (i in list.indices) {
            val rate = list[i].rate.replace("%", "").trim().toFloat()
            val entry = Entry(i.toFloat(), rate)
            data.add(entry)
        }
        return data
    }


    private fun setupLineChartData() {
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
        lineChart.xAxis.enableGridDashedLine(5f, 5f, 0f)
        lineChart.axisRight.enableGridDashedLine(5f, 5f, 0f)
        lineChart.axisLeft.enableGridDashedLine(5f, 5f, 0f)
        //lineChart.setDrawGridBackground()
        lineChart.xAxis.labelCount = viewModel.getLabelCount()
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
    }
}