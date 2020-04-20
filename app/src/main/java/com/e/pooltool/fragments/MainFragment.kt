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

class MainFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: MyViewModel
    private lateinit var playerListAdapter: PlayerListAdapter
    private lateinit var navController: NavController  // we'll initialise it later
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
        navController = Navigation.findNavController(view)
        getViewModel()
        getDialogHelper()
        setListeners()
        setEffects()
        setAdapter()
        addDivider()
        registerLiveData()
    }

    private fun setListeners() {
        btAdd.setOnClickListener(this)
        btReset.setOnClickListener(this)
        btSavedPlayers.setOnClickListener(this)
        // set swipe action listener
        val itemTouchHelper = ItemTouchHelper(getSwipeToDeleteCallback())
        itemTouchHelper.attachToRecyclerView(rvPlayers)
    }

    private fun setEffects() {
        viewModel.setButtonClickedEffect(btAdd)
        viewModel.setButtonClickedEffect(btReset)
        viewModel.setButtonClickedEffect(btSavedPlayers)
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
                viewModel.setResetButtonVisibility(list.size, btReset)
            })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btAdd -> viewModel.addPlayer()
            R.id.btReset -> dialogHelper.resetPlayer()
            R.id.btSavedPlayers -> {
                navController.navigate(R.id.action_mainFragment_to_savedPlayerFragment)
            }
        }
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
