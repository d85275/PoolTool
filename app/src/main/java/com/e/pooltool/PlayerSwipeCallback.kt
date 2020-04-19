package com.e.pooltool

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class PlayerSwipeCallback(context: Context?) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val deleteIcon = ContextCompat.getDrawable(context!!, R.drawable.ic_delete_white_24)
    private val saveIcon = ContextCompat.getDrawable(context!!, R.drawable.ic_archive_white_24)

    private val intrinsicWidth = deleteIcon!!.intrinsicWidth
    private val intrinsicHeight = deleteIcon!!.intrinsicHeight
    private val background = ColorDrawable()
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }


    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        /**
         * To disable "swipe" for specific item return 0 here.
         * For example:
         * if (viewHolder?.itemViewType == YourAdapter.SOME_TYPE) return 0
         * if (viewHolder?.adapterPosition == 0) return 0
         */
        //if (viewHolder.adapterPosition == 10) return 0
        return super.getMovementFlags(recyclerView, viewHolder)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
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

        val itemView = viewHolder.itemView
        val isCanceled = dX == 0f && !isCurrentlyActive

        val dir: Int = when (dX > 0) {
            true -> 0 // swiping right
            false -> 1 // swiping left
        }

        /*
        if (isCanceled) {
            clearCanvas(
                c,
                itemView.right + dX + 15,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }
         */

        // draw background
        drawBackground(c, dir, viewHolder, dX)
        drawIcon(c, dir, viewHolder)


        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }


    private var left: Float = 0f
    private var top: Float = 0f
    private var right: Float = 0f
    private var bottom: Float = 0f
    private var canvas: Canvas? = null

    // dir: 0 -> swipe right
    // dir: 1 -> swipe left
    private fun drawBackground(
        c: Canvas, dir: Int, viewHolder: RecyclerView.ViewHolder, dX: Float
    ) {
        if (dir == 0) {
            // green 5fb55c
            background.color = Color.parseColor("#5fb55c")
            background.setBounds(
                viewHolder.itemView.left, viewHolder.itemView.top,
                (viewHolder.itemView.left + dX - 15).toInt(),
                viewHolder.itemView.bottom
            )
            left = (viewHolder.itemView.left).toFloat()
            right = viewHolder.itemView.left + dX - 15
        } else {
            // red eb4d42
            background.color = Color.parseColor("#eb4d42")
            // swipe left
            background.setBounds(
                viewHolder.itemView.right + dX.toInt() + 15,
                viewHolder.itemView.top,
                viewHolder.itemView.right,
                viewHolder.itemView.bottom
            )

            left = (viewHolder.itemView.right + dX.toInt() + 15).toFloat()
            right = viewHolder.itemView.right.toFloat()
        }

        // save state

        canvas = c
        top = viewHolder.itemView.top.toFloat()
        bottom = viewHolder.itemView.bottom.toFloat()

        background.draw(c)

    }

    private fun drawIcon(c: Canvas, dir: Int, viewHolder: RecyclerView.ViewHolder) {
        val itemHeight = viewHolder.itemView.bottom - viewHolder.itemView.top

        val iconTop: Int = viewHolder.itemView.top + (itemHeight - intrinsicHeight) / 2
        val iconMargin: Int = (itemHeight - intrinsicHeight) / 2
        val iconBottom: Int = iconTop + intrinsicHeight

        val iconLeft: Int
        val iconRight: Int
        val icon: Drawable

        if (dir == 0) {
            icon = saveIcon!!
            iconLeft = viewHolder.itemView.left + iconMargin
            iconRight = viewHolder.itemView.left + iconMargin + intrinsicWidth
        } else {
            icon = deleteIcon!!
            // Calculate position of delete icon
            iconLeft = viewHolder.itemView.right - iconMargin - intrinsicWidth
            iconRight = viewHolder.itemView.right - iconMargin
        }

        // Draw the delete icon
        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        icon.draw(c)
    }


    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}