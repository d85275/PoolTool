package com.e.pooltool.fragments

import android.app.AlertDialog
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
import com.e.pooltool.MyViewModel
import com.e.pooltool.adapters.PlayerListAdapter
import com.e.pooltool.PlayerSwipeCallback
import com.e.pooltool.R
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: MyViewModel
    private lateinit var playerListAdapter: PlayerListAdapter
    lateinit var navController: NavController  // we'll initialise it later

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
        setListeners()
        setEffects()
        setAdapter()
        registerLiveData()
    }

    private fun setListeners() {
        btAdd.setOnClickListener(this)
        btReset.setOnClickListener(this)
        btSavedPlayers.setOnClickListener(this)
        btPlayerRecords.setOnClickListener(this)
        // set swipe action listener
        val itemTouchHelper = ItemTouchHelper(getSwipeToDeleteCallback())
        itemTouchHelper.attachToRecyclerView(rvPlayers)
    }

    private fun setEffects() {
        viewModel.setButtonClickedEffect(btAdd)
        viewModel.setButtonClickedEffect(btReset)
        viewModel.setButtonClickedEffect(btSavedPlayers)
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
        rvPlayers.adapter = playerListAdapter
        rvPlayers.layoutManager = LinearLayoutManager(activity)
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
            R.id.btReset -> resetConfirmDialogue()
            R.id.btPlayerRecords -> {
                navController.navigate(R.id.action_mainFragment_to_savedPlayerFragment)
            }
            R.id.btSavedPlayers -> {

            }
        }
    }


    private fun resetConfirmDialogue() {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(getString(R.string.info))
        dialogBuilder.setMessage(R.string.reset_score)
        dialogBuilder.setPositiveButton(R.string.confirm) { _, _ -> viewModel.resetScores() }
        dialogBuilder.setNegativeButton(R.string.cancel) { _, _ -> }
        dialogBuilder.show()
    }

    private fun deleteConfirmDialogue(position: Int) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(viewModel.getPlayerName(position))
        dialogBuilder.setMessage(getString(R.string.delete))
        dialogBuilder.setPositiveButton(R.string.confirm) { _, _ -> viewModel.removePlayer(position) }
        dialogBuilder.setNegativeButton(R.string.cancel) { _, _ -> playerListAdapter.notifyDataSetChanged() }
        dialogBuilder.show()
    }

    private fun saveConfirmDialogue(position: Int) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(viewModel.getPlayerName(position))
        dialogBuilder.setMessage(getString(R.string.save))
        dialogBuilder.setPositiveButton(R.string.confirm) { _, _ ->
            //viewModel.removePlayer(position)
            viewModel.saveRecord(position)
        }
        dialogBuilder.setNegativeButton(R.string.cancel) { _, _ -> playerListAdapter.notifyDataSetChanged() }
        dialogBuilder.show()
    }

    private fun getSwipeToDeleteCallback(): PlayerSwipeCallback {
        return object : PlayerSwipeCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.LEFT) {
                    deleteConfirmDialogue(position)
                } else if (direction == ItemTouchHelper.RIGHT) {
                    saveConfirmDialogue(position)
                }
            }
        }
    }
}
