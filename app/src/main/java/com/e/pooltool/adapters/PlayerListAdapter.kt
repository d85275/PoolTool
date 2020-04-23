package com.e.pooltool.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.e.pooltool.*
import kotlinx.android.synthetic.main.item_player.view.*


class PlayerListAdapter(
    private val viewModel: MyViewModel,
    private val playerList: ArrayList<Player>,
    private val ctx: Context?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var dialogHelper: DialogHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        dialogHelper = DialogHelper(ctx, viewModel)
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

        viewModel.setTextColor(playerList[position].getRate(),holder.itemView.tvRate)
    }

    private fun setClickListeners(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.tvName.setOnClickListener { dialogHelper.renameDialog(position) }
        holder.itemView.btAddPotted.setOnClickListener { viewModel.playerPotted(position) }
        holder.itemView.btRemovePotted.setOnClickListener { viewModel.undoPlayerPotted(position) }
        holder.itemView.btAddMissed.setOnClickListener { viewModel.playerMissed(position) }
        holder.itemView.btRemoveMissed.setOnClickListener { viewModel.undoPlayerMissed(position) }
        holder.itemView.btAddFouled.setOnClickListener { viewModel.playerFouled(position) }
        holder.itemView.btRemoveFouled.setOnClickListener { viewModel.undoPlayerFouled(position) }
    }


}
