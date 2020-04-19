package com.e.pooltool

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class SwipeToDeleteCallback(context: Context?) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val deleteIcon = ContextCompat.getDrawable(context!!, R.drawable.ic_delete_white_24)

    private val intrinsicWidth = deleteIcon!!.intrinsicWidth
    private val intrinsicHeight = deleteIcon!!.intrinsicHeight
    private val background = ColorDrawable()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        TODO("Not yet implemented")
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        // draw background
        drawBackground(c, viewHolder, dX)
        drawIcon(c, viewHolder)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    // dir: 0 -> swipe right
    // dir: 1 -> swipe left
    private fun drawBackground(c: Canvas, viewHolder: RecyclerView.ViewHolder, dX: Float) {

        // red eb4d42
        background.color = Color.parseColor("#eb4d42")
        // swipe left
        background.setBounds(
            viewHolder.itemView.right + dX.toInt() + 15,
            viewHolder.itemView.top,
            viewHolder.itemView.right,
            viewHolder.itemView.bottom
        )

        background.draw(c)

    }

    private fun drawIcon(c: Canvas, viewHolder: RecyclerView.ViewHolder) {
        val itemHeight = viewHolder.itemView.bottom - viewHolder.itemView.top

        val iconTop: Int = viewHolder.itemView.top + (itemHeight - intrinsicHeight) / 2
        val iconMargin: Int = (itemHeight - intrinsicHeight) / 2
        val iconBottom: Int = iconTop + intrinsicHeight

        val iconLeft: Int = viewHolder.itemView.right - iconMargin - intrinsicWidth
        val iconRight: Int = viewHolder.itemView.right - iconMargin


        // Draw the delete icon
        deleteIcon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        deleteIcon.draw(c)
    }

}