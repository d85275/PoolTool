package com.e.pooltool

import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.*
import com.e.pooltool.database.PlayerRecordItem
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class MyViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    // Keep the key as a constant
    companion object {
        private const val PLAYER_KEY = "PLAYER_KEY"
    }

    private lateinit var repository: Repository

    private var _players: MutableLiveData<ArrayList<Player>> = MutableLiveData()
    private var _savedRecord: MutableLiveData<ArrayList<PlayerRecordItem>> = MutableLiveData()

    private fun getPlayerState(): MutableLiveData<ArrayList<Player>> {
        return savedStateHandle.getLiveData(PLAYER_KEY)
    }

    fun getPlayerListLiveData(): LiveData<ArrayList<Player>> {
        _players = getPlayerState()
        if (_players.value == null) {
            _players.value = arrayListOf()
        }
        return _players
    }

    fun getPlayerList(): ArrayList<Player> {
        return getPlayerListLiveData().value!!
    }

    fun addPlayer() {
        val player = Player(PlayerNames().getName())
        val list = _players.value
        list?.add(player)
        _players.postValue(list)

    }

    fun removePlayer(idx: Int) {
        val list = _players.value
        list?.removeAt(idx)
        _players.postValue(list)
    }


    fun resetScores() {
        val list = _players.value!!
        for (i in 0 until list.size) {
            list[i].potted = 0
            list[i].missed = 0
        }
        _players.postValue(list)
    }


    fun renamePlayer(name: String, i: Int) {
        val list = _players.value!!
        list[i].name = name
        _players.postValue(list)
    }

    // player i has potted a shot
    fun playerPotted(i: Int) {
        val list = _players.value!!
        list[i].potted = list[i].potted + 1
        _players.postValue(list)
    }

    fun undoPlayerPotted(i: Int) {
        val list = _players.value!!
        if (list[i].potted <= 0) {
            return
        }
        list[i].potted = list[i].potted - 1
        _players.postValue(list)
    }

    // player i has missed a shot
    fun playerMissed(i: Int) {
        val list = _players.value!!
        list[i].missed = list[i].missed + 1
        _players.postValue(list)
    }

    fun undoPlayerMissed(i: Int) {
        val list = _players.value!!
        if (list[i].missed <= 0) {
            return
        }
        list[i].missed = list[i].missed - 1
        _players.postValue(list)
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

    // line chart --
    fun getLabelCount(): Int {
        if (getSavedRecordsList().size <= 0) {
            return 0
        }
        return getSavedRecordsList().size - 1
    }
    // line chart --

    // database --

    fun setRepository(repository: Repository) {
        this.repository = repository
    }

    // using the current date and time as the primary key for the database entry
    fun saveRecord(idx: Int) {
        val saved = getPlayerList()[idx]
        val current = LocalDateTime.now()

        // h - Hour in am/pm (1-12)
        //using two h's ("hh") gives you a leading zero (i.e. 01:23 AM). One "h" gives you the hour without the leading zero (1:23 AM)
        // H - Hour in 24 hours format
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")
        var date = current.format(formatter)

        saveRecord(saved, date)

        removePlayer(idx)
    }

    private fun saveRecord(player: Player, date: String) {
        val record = PlayerRecordItem(
            0,
            player.name, player.potted, player.missed,
            player.getRate(), date
        )
        Log.e("te", "id: ${record.id}")

        //Thread(Runnable { repository.addRecord(record) }).start()
        // save the data into db in background
        Single.fromCallable { repository.addRecord(record) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }


    fun getSavedRecordLiveDate(): LiveData<ArrayList<PlayerRecordItem>> {
        if (_savedRecord.value == null) {
            _savedRecord.value = arrayListOf()
        }
        return _savedRecord
    }

    fun getSavedRecordsList(): ArrayList<PlayerRecordItem> {
        return getSavedRecordLiveDate().value!!
    }

    fun getSavedRecords(name: String) {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(
            repository.getRecords(name).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()
            ).doOnError { e ->
                Log.e(
                    "PP",
                    "Error when getting saved records: $e"
                )
            }
                .subscribe { list ->
                    _savedRecord.postValue(list as ArrayList<PlayerRecordItem>?)
                }
        )
    }
    // database --
}