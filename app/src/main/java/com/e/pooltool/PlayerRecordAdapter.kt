package com.e.pooltool

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_player.view.tvRate
import kotlinx.android.synthetic.main.item_player_record.view.*

class PlayerRecordAdapter(
    private val viewModel: MyViewModel,
    private val playerRecord: ArrayList<PlayerRecord>,
    private val ctx: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val fileIcon = ContextCompat.getDrawable(ctx, R.drawable.ic_chevron_right_black_24dp)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(ctx).inflate(R.layout.item_player_record, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount(): Int {
        return playerRecord.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val potted = playerRecord[position].potted
        val missed = playerRecord[position].missed

        holder.itemView.ivIcon.setImageDrawable(fileIcon)
        holder.itemView.tvRatio.text = ctx.getString(R.string.ratio, potted, missed)
        holder.itemView.tvRate.text = playerRecord[position].getRate()
        holder.itemView.tvDate.text = playerRecord[position].date


        //holder.itemView.tvRate.setTextColor(viewModel.getRateTextColor(playerRecord[position].getRate()))
    }
}