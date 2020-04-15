package com.e.pooltool

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
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
        setRepository()
    }

    private fun setListeners() {
        btAdd.setOnClickListener(this)
        btReset.setOnClickListener(this)
        btPlayerRecords.setOnClickListener(this)
        // set swipe action listener
        val itemTouchHelper = ItemTouchHelper(getSwipeToDeleteCallback())
        itemTouchHelper.attachToRecyclerView(rvPlayers)
    }

    private fun setEffects() {
        viewModel.setButtonClickedEffect(btAdd)
        viewModel.setButtonClickedEffect(btReset)
    }

    private fun getViewModel() {
        viewModel = this.run {
            ViewModelProviders.of(
                this,
                SavedStateViewModelFactory(application, this)
            )[MyViewModel::class.java]
        }
    }

    private fun setRepository(){
        viewModel.setRepository(Repository(this))
    }

    private fun setAdapter() {
        playerListAdapter = PlayerListAdapter(viewModel, viewModel.getPlayerList(), this)
        rvPlayers.adapter = playerListAdapter
        rvPlayers.layoutManager = LinearLayoutManager(this)
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
                val intent = Intent()
                intent.setClass(this, PlayerRecordsActivity::class.java)
                startActivity(intent)
            }
        }
    }


    private fun resetConfirmDialogue() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(getString(R.string.info))
        dialogBuilder.setMessage(R.string.reset_score)
        dialogBuilder.setPositiveButton(R.string.confirm) { _, _ -> viewModel.resetScores() }
        dialogBuilder.setNegativeButton(R.string.cancel) { _, _ -> }
        dialogBuilder.show()
    }

    private fun deleteConfirmDialogue(position: Int) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(viewModel.getPlayerListLiveData().value!![position].name)
        dialogBuilder.setMessage(getString(R.string.delete))
        dialogBuilder.setPositiveButton(R.string.confirm) { _, _ -> viewModel.removePlayer(position) }
        dialogBuilder.setNegativeButton(R.string.cancel) { _, _ -> playerListAdapter.notifyDataSetChanged() }
        dialogBuilder.show()
    }

    private fun saveConfirmDialogue(position: Int) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(viewModel.getPlayerList()[position].name)
        dialogBuilder.setMessage(getString(R.string.save))
        dialogBuilder.setPositiveButton(R.string.confirm) { _, _ ->
            //viewModel.removePlayer(position)
            viewModel.saveRecord(position)
        }
        dialogBuilder.setNegativeButton(R.string.cancel) { _, _ -> playerListAdapter.notifyDataSetChanged() }
        dialogBuilder.show()
    }

    private fun getSwipeToDeleteCallback(): PlayerSwipeCallback {
        return object : PlayerSwipeCallback(this) {
            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
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
