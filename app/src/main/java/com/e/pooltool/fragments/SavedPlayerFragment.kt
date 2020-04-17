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
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.pooltool.ISavedPlayerCallback
import com.e.pooltool.MyViewModel
import com.e.pooltool.R
import com.e.pooltool.adapters.SavedPlayerAdapter
import kotlinx.android.synthetic.main.fragment_saved_player.*
import java.lang.Exception

class SavedPlayerFragment : Fragment(), ISavedPlayerCallback {

    private lateinit var navController: NavController
    private lateinit var viewModel: MyViewModel
    private lateinit var savedPlayerAdapter: SavedPlayerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_saved_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        getViewModel()
        setAdapter()
        registerLiveData()
        loadPlayers()
    }

    private fun getViewModel() {
        viewModel = activity?.run { ViewModelProviders.of(this)[MyViewModel::class.java] }
            ?: throw Exception("Invalid Activity")
    }

    private fun setAdapter() {
        savedPlayerAdapter =
            SavedPlayerAdapter(
                viewModel,
                viewModel.getSavedPlayerList(),
                this,
                context
            )
        rvPlayers.adapter = savedPlayerAdapter
        rvPlayers.layoutManager = LinearLayoutManager(context)
    }

    private fun registerLiveData() {
        viewModel.getSavedPlayerLiveDate().observe(this, Observer { list ->
            savedPlayerAdapter.setData(list)
            savedPlayerAdapter.notifyDataSetChanged()
            //Log.e("tag", "notify data changed, list size: ${list.size}")
        })
    }

    private fun loadPlayers() {
        viewModel.getAllSavedPlayers()
    }

    override fun savedPlayerClicked(position: Int) {
        viewModel.onSavedPlayerClicked(position)
        navController.navigate(R.id.action_savedPlayerFragment_to_playerRecordsFragment)
    }

}