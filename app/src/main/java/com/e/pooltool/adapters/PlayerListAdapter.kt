package com.e.pooltool.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.e.pooltool.MyViewModel
import com.e.pooltool.Player
import com.e.pooltool.PlayerNames
import com.e.pooltool.R
import kotlinx.android.synthetic.main.item_player.view.*
import kotlinx.android.synthetic.main.player_name.view.*


class PlayerListAdapter(
    private val viewModel: MyViewModel,
    private val playerList: ArrayList<Player>,
    private val ctx: Context?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.item_player, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        initViews(holder, position)
        setClickListeners(holder, position)
        //setClickEffects(holder)
    }

    private fun initViews(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.tvName.text = playerList[position].name
        holder.itemView.tvPotted.text = playerList[position].potted.toString()
        holder.itemView.tvMissed.text = playerList[position].missed.toString()
        holder.itemView.tvFouled.text = playerList[position].fouled.toString()
        holder.itemView.tvRate.text = playerList[position].getRate()

        holder.itemView.tvRate.setTextColor(viewModel.getRateTextColor(playerList[position].getRate()))
    }

    private fun setClickListeners(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.tvName.setOnClickListener { renameDialog(position) }
        holder.itemView.btAddPotted.setOnClickListener { viewModel.playerPotted(position) }
        holder.itemView.btRemovePotted.setOnClickListener { viewModel.undoPlayerPotted(position) }
        holder.itemView.btAddMissed.setOnClickListener { viewModel.playerMissed(position) }
        holder.itemView.btRemoveMissed.setOnClickListener { viewModel.undoPlayerMissed(position) }
        holder.itemView.btAddFouled.setOnClickListener { viewModel.playerFouled(position) }
        holder.itemView.btRemoveFouled.setOnClickListener { viewModel.undoPlayerFouled(position) }
    }

    private fun setClickEffects(holder: RecyclerView.ViewHolder) {
        viewModel.setButtonClickedEffect(holder.itemView.btAddPotted)
        viewModel.setButtonClickedEffect(holder.itemView.btRemovePotted)
        viewModel.setButtonClickedEffect(holder.itemView.btAddMissed)
        viewModel.setButtonClickedEffect(holder.itemView.btRemoveMissed)
        viewModel.setButtonClickedEffect(holder.itemView.btAddFouled)
        viewModel.setButtonClickedEffect(holder.itemView.btRemoveFouled)
    }


    private fun renameDialog(position: Int) {
        val dialogBuilder = AlertDialog.Builder(ctx)

        val view = LayoutInflater.from(ctx).inflate(R.layout.player_name, null)

        // init views
        setPlayerName(view.etName, playerList[position].name)

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
