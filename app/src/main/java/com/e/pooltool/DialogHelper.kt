package com.e.pooltool

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.player_name.view.*

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
            record.getTotal()
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

    fun renameDialog(position: Int) {
        val dialogBuilder = AlertDialog.Builder(context)

        val view = LayoutInflater.from(context).inflate(R.layout.player_name, null)

        // init views
        setPlayerName(view.etName, viewModel.getPlayerList()[position].name)

        // set get random name button
        viewModel.setButtonClickedEffect(view.btRandom)
        view.btRandom.setOnClickListener {
            setPlayerName(view.etName, PlayerNames().getName())
        }

        // set dialog buttons
        dialogBuilder.setPositiveButton(R.string.confirm) { _, _ ->
            val input = view.etName.text.toString().trim()
            val name = when (input.isBlank() || input.isEmpty()) {
                true -> PlayerNames().getName()
                false -> input
            }
            viewModel.renamePlayer(name, position)
        }
        dialogBuilder.setNegativeButton(R.string.cancel) { _, _ -> }

        val dialog = dialogBuilder.create()
        dialog.setView(view, 40, 100, 40, 0)
        dialog.show()

        // show keyboard
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun setPlayerName(etName: EditText, name: String) {
        etName.setText(name)
        etName.requestFocus()
        etName.selectAll()
    }
}