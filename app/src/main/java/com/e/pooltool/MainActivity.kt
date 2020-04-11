package com.e.pooltool

import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var viewModel: MyViewModel
    private lateinit var playerListAdapter: PlayerListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_main)
        getViewModel()
        setListeners()
        setEffects()
        setAdapter()
        registerLiveData()
    }

    private fun setListeners() {
        btAdd.setOnClickListener(this)
        btReset.setOnClickListener(this)
        btSave.setOnClickListener(this)
        // set swipe action listener
        val itemTouchHelper = ItemTouchHelper(touchCallback)
        itemTouchHelper.attachToRecyclerView(rvPlayers)
    }

    private fun setEffects() {
        viewModel.setButtonClickedEffect(btAdd)
        viewModel.setButtonClickedEffect(btReset)
        viewModel.setButtonClickedEffect(btSave)
    }

    private fun getViewModel() {
        viewModel = this.run { ViewModelProviders.of(this)[MyViewModel::class.java] }
    }

    private fun setAdapter() {
        playerListAdapter = PlayerListAdapter(viewModel, viewModel.getPlayerList().value!!, this)
        rvPlayers.adapter = playerListAdapter
        rvPlayers.layoutManager = LinearLayoutManager(this)
    }

    private fun registerLiveData() {
        viewModel.getPlayerList()
            .observe(this, Observer { list ->
                playerListAdapter.notifyDataSetChanged()
                viewModel.setResetButtonVisibility(list.size, btReset)
            })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btAdd -> viewModel.addPlayer()
            R.id.btReset -> resetConfirmDialogue()
        }
    }


    private fun resetConfirmDialogue() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(R.string.reset_score)
        dialogBuilder.setPositiveButton(R.string.confirm) { _, _ -> viewModel.resetScores() }
        dialogBuilder.setNegativeButton(R.string.cancel) { _, _ -> }
        dialogBuilder.show()
    }

    private var touchCallback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                target: ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: ViewHolder, swipeDir: Int) {
                viewModel.removePlayer(viewHolder.adapterPosition)
            }
        }
}
