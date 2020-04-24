package com.e.pooltool.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.pooltool.*
import com.e.pooltool.adapters.PlayerRecordAdapter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.fragment_player_records.*
import java.lang.Exception

class PlayerRecordsFragment : Fragment(), IClickedCallback {

    private lateinit var viewModel: MyViewModel
    private lateinit var playerRecordAdapter: PlayerRecordAdapter
    private lateinit var navController: NavController  // we'll initialise it later
    private lateinit var dialogHelper: DialogHelper


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player_records, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        getViewModel()
        getDialogHelper()
        setAdapter()
        registerLiveData()
        //loadRecords()
        setListeners()
    }

    private fun getViewModel() {
        viewModel = activity?.run { ViewModelProviders.of(this)[MyViewModel::class.java] }
            ?: throw Exception("Invalid Activity")
    }

    private fun getDialogHelper() {
        dialogHelper = DialogHelper(context, viewModel)
    }

    private fun setAdapter() {
        playerRecordAdapter =
            PlayerRecordAdapter(
                viewModel,
                viewModel.getDisplayedRecordsList(),
                context,
                this
            )
        // use this setting to improve performance because we do not intent
        // to change the layout size of the RecyclerView
        rvRecords.setHasFixedSize(true)

        rvRecords.adapter = playerRecordAdapter
        rvRecords.layoutManager = LinearLayoutManager(context)
    }

    private fun registerLiveData() {
        viewModel.getDisplayedRecordLiveDate().observe(this, Observer { list ->
            initData()
            setupLineChartData()
            playerRecordAdapter.setData(list)
            playerRecordAdapter.notifyDataSetChanged()
        })
    }

    /*
    private fun loadRecords() {
        viewModel.getDisplayedRecords()
    }

     */

    private fun setListeners() {
        val itemTouchHelper = ItemTouchHelper(getSwipeToDeleteCallback())
        itemTouchHelper.attachToRecyclerView(rvRecords)
    }

    private fun getSwipeToDeleteCallback(): SwipeToDeleteCallback {
        return object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                dialogHelper.deleteRecord(position, playerRecordAdapter)
            }
        }
    }

    private fun initData() {
        val player = viewModel.getDisplayedData()
        tvPlayerName.text = player.name
        val total = player.potted + player.missed + player.fouled
        tvPottedRecord.text = viewModel.getRate(player.potted, total)
        tvMissedRecord.text = viewModel.getRate(player.missed, total)
        tvFouledRecord.text = viewModel.getRate(player.fouled, total)
        tvAve.text = player.getRate().replace("%", "")
        viewModel.setTextColor(player.getRate(), tvAve)
        tvDate.text = viewModel.getDateDuration()
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

    override fun itemClicked(position: Int) {
        viewModel.onRecordClicked(position)
        navController.navigate(R.id.action_playerRecordsFragment_to_editFragment)
    }
}