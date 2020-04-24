package com.e.pooltool.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.e.pooltool.DialogHelper
import com.e.pooltool.MyViewModel
import com.e.pooltool.R
import kotlinx.android.synthetic.main.fragment_edit.*
import java.lang.Exception

class EditFragment : Fragment() {

    private lateinit var viewModel: MyViewModel
    private lateinit var dialogHelper: DialogHelper

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
        initData()
    }

    private fun getViewModel() {
        viewModel = activity?.run { ViewModelProviders.of(this)[MyViewModel::class.java] }
            ?: throw Exception("Invalid Activity")
    }

    private fun getDialogHelper() {
        dialogHelper = DialogHelper(context, viewModel)
    }

    private fun initData() {
        val record = viewModel.getEdittingRecord()
        etName.setText(record.name)
        tvPotted.text = record.potted.toString()
        tvFouled.text = record.fouled.toString()
        tvMissed.text = record.missed.toString()
        tvRate.text = record.rate
        tvDate.text = record.date
    }
}