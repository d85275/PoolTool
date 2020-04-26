package com.e.pooltool.fragments

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
import com.e.pooltool.charts.PlayerRecordChart
import kotlinx.android.synthetic.main.fragment_player_records.*
import java.lang.Exception

class PlayerRecordsFragment : Fragment(), IClickedCallback {

    private lateinit var viewModel: MyViewModel
    private lateinit var playerRecordAdapter: PlayerRecordAdapter
    private lateinit var navController: NavController  // we'll initialise it later
    private lateinit var dialogHelper: DialogHelper
    private lateinit var playerRecoradChart: PlayerRecordChart

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
        initChart()
        getDialogHelper()
        setAdapter()
        registerLiveData()
        setListeners()
    }

    private fun getViewModel() {
        viewModel = activity?.run { ViewModelProviders.of(this)[MyViewModel::class.java] }
            ?: throw Exception("Invalid Activity")
    }

    private fun initChart() {
        playerRecoradChart = PlayerRecordChart(viewModel, lineChart)
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
            playerRecoradChart.setup()
            playerRecordAdapter.setData(list)
            playerRecordAdapter.notifyDataSetChanged()
        })
    }

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
        tvPlayerName.text = viewModel.getDisplayedName()
        tvPottedRecord.text = viewModel.getDisplayedPotted()
        tvMissedRecord.text = viewModel.getDisplayedMissed()
        tvFouledRecord.text = viewModel.getDisplayedFouled()
        tvAve.text = viewModel.getDisplayedRate()
        viewModel.setTextColor(viewModel.getDisplayedData().getRate(), tvAve)
        tvDate.text = viewModel.getDateDuration()
    }

    override fun itemClicked(position: Int) {
        viewModel.onRecordClicked(position)
        navController.navigate(R.id.action_playerRecordsFragment_to_editFragment)
    }
}