package com.e.pooltool

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_player.view.*


class PlayerListAdapter(
    private val viewModel: MyViewModel,
    private val playerList: ArrayList<Player>,
    private val ctx: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.item_player, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.tvName.text = playerList[position].name
        holder.itemView.tvPotted.text = playerList[position].potted.toString()
        holder.itemView.tvMissed.text = playerList[position].missed.toString()
        holder.itemView.tvRate.text = playerList[position].getRate()

        holder.itemView.tvRate.setTextColor(viewModel.getRateTextColor(playerList[position].getRate()))

        holder.itemView.tvName.setOnClickListener { viewModel.playerRename(position) }
        holder.itemView.tvPotted.setOnClickListener { viewModel.playerPotted(position) }
        holder.itemView.tvMissed.setOnClickListener { viewModel.playerMissed(position) }

        holder.itemView.tvPotted.setOnLongClickListener {
            viewModel.undoPlayerPotted(position)

            // return true.  Don't trigger the on click event when on long clicked is triggered
            true
        }
        holder.itemView.tvMissed.setOnLongClickListener {
            viewModel.undoPlayerMissed(position)

            // return true.  Don't trigger the on click event when on long clicked is triggered
            true
        }

        /*val myCustomFont: Typeface? = ResourcesCompat.getFont(ctx, R.font.playtime)
        holder.itemView.tvName.typeface = myCustomFont
        holder.itemView.tvPotted.typeface = myCustomFont
        holder.itemView.tvMissed.typeface = myCustomFont
        holder.itemView.tvRate.typeface = myCustomFont*/

        /**
         * Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
         * Icons made by <a href="https://www.flaticon.com/authors/smalllikeart" title="smalllikeart">smalllikeart</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
         * Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
         * Icons made by <a href="https://www.flaticon.com/authors/those-icons" title="Those Icons">Those Icons</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
         */
    }
}
