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
import com.e.pooltool.adapters.SavedPlayerAdapter
import kotlinx.android.synthetic.main.fragment_saved_player.*
import java.lang.Exception

class SavedPlayerFragment : Fragment(), IClickedCallback {

    private lateinit var navController: NavController
    private lateinit var viewModel: MyViewModel
    private lateinit var savedPlayerAdapter: SavedPlayerAdapter
    private lateinit var dialogHelper: DialogHelper

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
        getDialogHelper()
        setAdapter()
        registerLiveData()
        loadPlayers()
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
        savedPlayerAdapter =
            SavedPlayerAdapter(
                viewModel,
                viewModel.getSavedPlayerList(),
                this,
                context
            )
        // use this setting to improve performance because we do not intent
        // to change the layout size of the RecyclerView
        rvPlayers.setHasFixedSize(true)

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

    private fun setListeners() {
        val itemTouchHelper = ItemTouchHelper(getSwipeToDeleteCallback())
        itemTouchHelper.attachToRecyclerView(rvPlayers)
    }

    private fun getSwipeToDeleteCallback(): SwipeToDeleteCallback {
        return object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                dialogHelper.deleteSavedPlayer(position, savedPlayerAdapter)
            }
        }
    }

    private fun loadPlayers() {
        viewModel.getAllSavedPlayers()
    }

    override fun itemClicked(position: Int) {
        viewModel.onSavedPlayerClicked(position)
        navController.navigate(R.id.action_savedPlayerFragment_to_playerRecordsFragment)
    }

}