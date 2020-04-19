package com.e.pooltool

import android.app.AlertDialog
import android.content.Context
import androidx.recyclerview.widget.RecyclerView

class DialogHelper(private val context: Context?, private val viewModel: MyViewModel) {
    fun resetPlayer() {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(context!!.getString(R.string.info))
        dialogBuilder.setMessage(R.string.reset_score)
        dialogBuilder.setPositiveButton(R.string.confirm) { _, _ -> viewModel.resetScores() }
        dialogBuilder.setNegativeButton(R.string.cancel) { _, _ -> }
        dialogBuilder.show()
    }

    fun deletePlayer(position: Int, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(viewModel.getPlayerName(position))
        dialogBuilder.setMessage(context!!.getString(R.string.delete))
        dialogBuilder.setPositiveButton(R.string.confirm) { _, _ -> viewModel.removePlayer(position) }
        dialogBuilder.setNegativeButton(R.string.cancel) { _, _ -> adapter.notifyDataSetChanged() }
        dialogBuilder.show()
    }

    fun deleteRecord(position: Int, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        val dialogBuilder = AlertDialog.Builder(context)
        val record = viewModel.getDisplayedRecordsList()[position]
        val title = context!!.getString(
            R.string.rate_and_ratio,
            record.rate,
            record.potted,
            record.potted + record.missed
        )
        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(context.getString(R.string.delete))
        dialogBuilder.setPositiveButton(R.string.confirm) { _, _ -> viewModel.removeRecord(position) }
        dialogBuilder.setNegativeButton(R.string.cancel) { _, _ -> adapter.notifyDataSetChanged() }
        dialogBuilder.show()
    }

    fun savePlayer(position: Int, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(viewModel.getPlayerName(position))
        dialogBuilder.setMessage(context!!.getString(R.string.save))
        dialogBuilder.setPositiveButton(R.string.confirm) { _, _ ->
            viewModel.saveRecord(position)
        }
        dialogBuilder.setNegativeButton(R.string.cancel) { _, _ -> adapter.notifyDataSetChanged() }
        dialogBuilder.show()
    }

    fun deleteSavedPlayer(position: Int, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(viewModel.getSavedPlayerName(position))
        dialogBuilder.setMessage(context!!.getString(R.string.delete))
        dialogBuilder.setPositiveButton(R.string.confirm) { _, _ ->
            viewModel.removeSavedPlayer(
                position
            )
        }
        dialogBuilder.setNegativeButton(R.string.cancel) { _, _ -> adapter.notifyDataSetChanged() }
        dialogBuilder.show()
    }
}