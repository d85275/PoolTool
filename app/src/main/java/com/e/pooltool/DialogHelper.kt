package com.e.pooltool

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.e.pooltool.database.PlayerRecordItem
import kotlinx.android.synthetic.main.player_name.view.*

class DialogHelper(private val context: Context?, private val viewModel: MyViewModel) {

    private fun showDialog(
        title: String,
        msg: String,
        posListener: DialogInterface.OnClickListener,
        negListener: DialogInterface.OnClickListener
    ) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(msg)
        dialogBuilder.setPositiveButton(R.string.confirm, posListener)
        dialogBuilder.setNegativeButton(R.string.cancel, negListener)
        dialogBuilder.show().setCanceledOnTouchOutside(false)
    }

    fun resetPlayer() {
        val title = context!!.getString(R.string.info)
        val msg = context.getString(R.string.reset_score)
        val posListener = DialogInterface.OnClickListener { _, _ -> viewModel.resetScores() }
        val negListener = DialogInterface.OnClickListener { _, _ -> }
        showDialog(title, msg, posListener, negListener)
    }

    fun deletePlayer(position: Int, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        val title = viewModel.getPlayerName(position)
        val msg = context!!.getString(R.string.delete)
        val posListener =
            DialogInterface.OnClickListener { _, _ -> viewModel.removePlayer(position) }
        val negListener = DialogInterface.OnClickListener { _, _ -> adapter.notifyDataSetChanged() }
        showDialog(title, msg, posListener, negListener)
    }

    fun deleteRecord(position: Int, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        val record = viewModel.getDisplayedRecordsList()[position]
        val title = context!!.getString(
            R.string.rate_and_ratio,
            record.rate,
            record.potted,
            record.getTotal()
        )
        val msg = context.getString(R.string.delete)
        val posListener =
            DialogInterface.OnClickListener { _, _ -> viewModel.removeRecord(position) }
        val negListener = DialogInterface.OnClickListener { _, _ -> adapter.notifyDataSetChanged() }
        showDialog(title, msg, posListener, negListener)
    }

    fun updateRecord(recordItem: PlayerRecordItem) {
        val title = recordItem.name
        val msg = context!!.getString(R.string.update_record)
        val posListener =
            DialogInterface.OnClickListener { _, _ ->
                viewModel.updateRecord(recordItem)
            }
        val negListener = DialogInterface.OnClickListener { _, _ -> }
        showDialog(title, msg, posListener, negListener)
    }

    fun savePlayer(position: Int, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {

        val title = viewModel.getPlayerName(position)
        val msg = context!!.getString(R.string.save)
        val posListener = DialogInterface.OnClickListener { _, _ -> viewModel.saveRecord(position) }
        val negListener = DialogInterface.OnClickListener { _, _ -> adapter.notifyDataSetChanged() }
        showDialog(title, msg, posListener, negListener)
    }

    fun deleteSavedPlayer(position: Int, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {

        val title = viewModel.getSavedPlayerName(position)
        val msg = context!!.getString(R.string.delete)
        val posListener =
            DialogInterface.OnClickListener { _, _ -> viewModel.removeSavedPlayer(position) }
        val negListener = DialogInterface.OnClickListener { _, _ -> adapter.notifyDataSetChanged() }
        showDialog(title, msg, posListener, negListener)
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
        dialog.setCanceledOnTouchOutside(false)
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