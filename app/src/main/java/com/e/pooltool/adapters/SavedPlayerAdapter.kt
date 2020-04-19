package com.e.pooltool.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.e.pooltool.ISavedPlayerCallback
import com.e.pooltool.MyViewModel
import com.e.pooltool.Player
import com.e.pooltool.R
import com.e.pooltool.database.PlayerRecordItem
import kotlinx.android.synthetic.main.item_saved_player.view.*

class SavedPlayerAdapter(
    private val viewModel: MyViewModel,
    private var list: ArrayList<Player>,
    private var callback: ISavedPlayerCallback,
    private val ctx: Context?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.item_saved_player, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.tvName.text = list[position].name
        val total = list[position].potted + list[position].missed
        val potted = list[position].potted
        val rate = list[position].getRate()
        val data = ctx!!.getString(R.string.rate_and_ratio, rate, potted, total)
        holder.itemView.tvRatio.text = data
        holder.itemView.setOnClickListener {
            callback.savedPlayerClicked(position)
        }
        holder.itemView.setOnLongClickListener {
            Log.e( "tag","long")
            true
        }

    }

    fun setData(list: ArrayList<Player>) {
        this.list = list
    }
}