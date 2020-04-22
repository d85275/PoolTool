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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.pooltool.DialogHelper
import com.e.pooltool.MyViewModel
import com.e.pooltool.adapters.PlayerListAdapter
import com.e.pooltool.PlayerSwipeCallback
import com.e.pooltool.R
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private lateinit var viewModel: MyViewModel
    private lateinit var playerListAdapter: PlayerListAdapter
    private lateinit var dialogHelper: DialogHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModel()
        getDialogHelper()
        setListeners()
        setAdapter()
        addDivider()
        registerLiveData()
    }

    private fun setListeners() {
        // set swipe action listener
        val itemTouchHelper = ItemTouchHelper(getSwipeToDeleteCallback())
        itemTouchHelper.attachToRecyclerView(rvPlayers)
    }

    private fun getDialogHelper() {
        dialogHelper = DialogHelper(context, viewModel)
    }

    private fun getViewModel() {
        viewModel = activity?.run { ViewModelProviders.of(this)[MyViewModel::class.java] }
            ?: throw Exception("Invalid Activity")
    }

    private fun setAdapter() {
        playerListAdapter = PlayerListAdapter(
            viewModel,
            viewModel.getPlayerList(),
            context
        )
        // use this setting to improve performance because we do not intent
        //        // to change the layout size of the RecyclerView
        rvPlayers.setHasFixedSize(true)

        rvPlayers.adapter = playerListAdapter
        rvPlayers.layoutManager = LinearLayoutManager(activity)
    }

    private fun addDivider() {
        val dividerItemDecoration =
            DividerItemDecoration(rvPlayers.context, LinearLayoutManager(activity).orientation)
        rvPlayers.addItemDecoration(dividerItemDecoration)
    }

    private fun registerLiveData() {
        viewModel.getPlayerListLiveData()
            .observe(this, Observer { list ->
                playerListAdapter.notifyDataSetChanged()
                if (list.size <= 0) {
                    activity!!.invalidateOptionsMenu()
                }
            })
    }


    private fun getSwipeToDeleteCallback(): PlayerSwipeCallback {
        return object : PlayerSwipeCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.LEFT) {
                    dialogHelper.deletePlayer(position, playerListAdapter)
                } else if (direction == ItemTouchHelper.RIGHT) {
                    dialogHelper.savePlayer(position, playerListAdapter)
                }
            }
        }
    }
}
