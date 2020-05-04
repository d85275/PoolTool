package com.e.pooltool.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.e.pooltool.DialogHelper
import com.e.pooltool.MyViewModel
import com.e.pooltool.R
import com.e.pooltool.database.PlayerRecordItem
import kotlinx.android.synthetic.main.fragment_edit.*
import java.lang.Exception

class EditFragment : Fragment() {

    private lateinit var viewModel: MyViewModel
    private lateinit var dialogHelper: DialogHelper
    private lateinit var navController: NavController
    private var updated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModel()
        getDialogHelper()
        navController = findNavController()
        setListeners()
        registerLiveData()
    }

    private fun getViewModel() {
        viewModel = activity?.run { ViewModelProviders.of(this)[MyViewModel::class.java] }
            ?: throw Exception("Invalid Activity")
    }

    private fun getDialogHelper() {
        dialogHelper = DialogHelper(context, viewModel)
    }

    private fun initData(record: PlayerRecordItem) {
        etName.setText(record.name)
        tvPotted.text = record.potted.toString()
        tvFouled.text = record.fouled.toString()
        tvMissed.text = record.missed.toString()
        tvRate.text = record.rate
        tvDate.text = record.date
        viewModel.setTextColor(record.rate, tvRate)
    }

    private fun setListeners() {
        btAddPotted.setOnClickListener { viewModel.addPotted() }
        btRemovePotted.setOnClickListener { viewModel.removePotted() }
        btAddMissed.setOnClickListener { viewModel.addMissed() }
        btRemoveMissed.setOnClickListener { viewModel.removeMissed() }
        btAddFouled.setOnClickListener { viewModel.addFouled() }
        btRemoveFouled.setOnClickListener { viewModel.removeFouled() }

        btConfirm.setOnClickListener {
            dialogHelper.updateRecord(viewModel.getEditingRecord())
            updated = true
        }
    }


    private fun registerLiveData() {
        viewModel.getEditingRecordLiveData().observe(this, Observer { record -> initData(record) })


        viewModel.getDisplayedRecordLiveDate().observe(this, Observer { list ->
            // do nothing if the data haven't been updated
            if (updated) {
                // there is no data left for the displayed player, go back to saved player fragment
                if (list.size <= 0) {
                    navController.popBackStack(R.id.savedPlayerFragment, false)
                } else {
                    // go back to player record fragment
                    activity!!.onBackPressed()
                }
            }
        })
    }
}