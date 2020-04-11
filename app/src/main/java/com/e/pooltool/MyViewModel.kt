package com.e.pooltool

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MyViewModel : ViewModel() {
    private val mldPlayers: MutableLiveData<ArrayList<Player>> = MutableLiveData()

    fun getPlayerList(): LiveData<ArrayList<Player>> {
        if (mldPlayers.value == null) {
            mldPlayers.value = arrayListOf()
        }
        return mldPlayers
    }

    fun addPlayer() {
        val player = Player(PlayerNames().getName())
        val list = mldPlayers.value
        list?.add(player)
        mldPlayers.postValue(list)
    }

    fun removePlayer(idx: Int) {
        val list = mldPlayers.value
        list?.removeAt(idx)
        mldPlayers.postValue(list)
    }

    fun resetScores() {
        val list = mldPlayers.value!!
        for (i in 0 until list.size) {
            list[i].potted = 0
            list[i].missed = 0
        }
        mldPlayers.postValue(list)
    }


    fun renamePlayer(name: String, i: Int) {
        val list = mldPlayers.value!!
        list[i].name = name
        mldPlayers.postValue(list)
    }

    // rename player i
    fun playerRename(i: Int) {
        val list = mldPlayers.value!!
        list[i].name = PlayerNames().getName()
        mldPlayers.postValue(list)
    }

    // player i has potted a shot
    fun playerPotted(i: Int) {
        val list = mldPlayers.value!!
        list[i].potted = list[i].potted + 1
        mldPlayers.postValue(list)
    }

    fun undoPlayerPotted(i: Int) {
        val list = mldPlayers.value!!
        if (list[i].potted <= 0) {
            return
        }
        list[i].potted = list[i].potted - 1
        mldPlayers.postValue(list)
    }

    // player i has missed a shot
    fun playerMissed(i: Int) {
        val list = mldPlayers.value!!
        list[i].missed = list[i].missed + 1
        mldPlayers.postValue(list)
    }

    fun undoPlayerMissed(i: Int) {
        val list = mldPlayers.value!!
        if (list[i].missed <= 0) {
            return
        }
        list[i].missed = list[i].missed - 1
        mldPlayers.postValue(list)
    }

    fun getRateTextColor(rate: String): Int {
        val r: Double = rate.replace("%", "").toDouble()
        val base: Int
        val color: Int
        val alpha: Int

        base = when (r >= 50.0) {
            true -> 0x55aa22 // green
            false -> 0xaa5522 // red
        }

        alpha = when (r >= 50.0) {
            true -> (255 * r / 100).toInt()
            false -> (255 * (100 - r) / 100).toInt()
        }

        color = Color.argb(
            alpha,
            Color.red(base),
            Color.green(base),
            Color.blue(base)
        )
        return color
    }

    fun setButtonClickedEffect(button: View) {
        button.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                    v.invalidate()
                }

                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }

            false
        }
    }

    // the number of the players
    // if there is no player, don't show the reset button
    fun setResetButtonVisibility(size: Int, btReset: View) {
        if (size > 0 && btReset.visibility == View.INVISIBLE) {
            btReset.visibility = View.VISIBLE
        } else if (size <= 0) {
            btReset.visibility = View.INVISIBLE
        }
    }

}