package com.e.pooltool

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.e.pooltool.database.PlayerRecordItem
import com.e.pooltool.database.Repository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


class MyViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    // Keep the key as a constant
    companion object {
        private const val PLAYER_KEY = "PLAYER_KEY"
        private const val EXIT_DELAY_TIME = 400L // 0.4 second
    }

    private lateinit var repository: Repository

    private var _players: MutableLiveData<ArrayList<Player>> = MutableLiveData()
    private var _displayedRecord: MutableLiveData<ArrayList<PlayerRecordItem>> = MutableLiveData()
    private var _savedPlayers: MutableLiveData<ArrayList<Player>> = MutableLiveData()


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

    fun getPlayerName(idx: Int): String {
        return getPlayerList()[idx].name
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


    private fun resetScoreForPlayer(i: Int) {
        val list = _players.value!!
        list[i].reset()
        _players.postValue(list)
    }

    fun resetScores() {
        val list = _players.value!!
        for (i in 0 until list.size) {
            list[i].reset()
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

    fun playerFouled(i: Int) {
        val list = _players.value!!
        list[i].fouled = list[i].fouled + 1
        _players.postValue(list)
    }

    fun undoPlayerFouled(i: Int) {
        val list = _players.value!!
        if (list[i].fouled <= 0) {
            return
        }
        list[i].fouled = list[i].fouled - 1
        _players.postValue(list)
    }

    fun setTextColor(rate: String, view: TextView) {
        view.setTextColor(getRateTextColor(rate))
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
                    setColorFilter(v.background)
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

    private fun setColorFilter(background: Drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            background.colorFilter = BlendModeColorFilter(Color.WHITE, BlendMode.SRC_ATOP)
        } else {
            background.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        }
    }

    // the number of the players
    // if there is no player, don't show the reset button
    fun setResetButtonVisibility(menu: Menu?) {
        val btReset = menu!!.findItem(R.id.btReset)

        if (getPlayerList().size > 0 && !btReset.isVisible) {
            btReset.isVisible = true
        } else if (getPlayerList().size <= 0) {
            btReset.isVisible = false
        }
    }

    // line chart --
    fun getLabelCount(): Int {
        if (getDisplayedRecordsList().size <= 0) {
            return 0
        }
        return getDisplayedRecordsList().size - 1
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
        resetScoreForPlayer(idx)
        //removePlayer(idx)
    }

    private fun saveRecord(player: Player, date: String) {
        val record = PlayerRecordItem(
            0,
            player.name, player.potted, player.missed,
            player.fouled, player.getRate(), date
        )
        //Thread(Runnable { repository.addRecord(record) }).start()
        // save the data into db in background
        Single.fromCallable { repository.addRecord(record) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }


    fun getDisplayedRecordLiveDate(): LiveData<ArrayList<PlayerRecordItem>> {
        if (_displayedRecord.value == null) {
            _displayedRecord.value = arrayListOf()
        }
        return _displayedRecord
    }

    fun getDisplayedRecordsList(): ArrayList<PlayerRecordItem> {
        return getDisplayedRecordLiveDate().value!!
    }

    fun getDateDuration(): String {
        val list = getDisplayedRecordsList()
        if (list.size <= 0) {
            return ""
        }
        val date1 = list[0].date.split(" ")[0]
        val date2 = list[list.lastIndex].date.split(" ")[0]
        return "$date1 - $date2"
    }

    fun getDisplayedData(): Player {
        val list = getDisplayedRecordsList()
        val name = if (list.size > 0) list[0].name else ""
        var potted = 0
        var missed = 0
        var fouled = 0

        for (i in 0 until list.size) {
            potted += list[i].potted
            missed += list[i].missed
            fouled += list[i].fouled
        }

        return Player(name, potted, missed, fouled)
    }

    fun getDisplayedName(): String {
        return getDisplayedData().name
    }

    fun getDisplayedPotted(): String {
        return getDisplayedValue(getDisplayedData().potted)
    }

    fun getDisplayedMissed(): String {
        return getDisplayedValue(getDisplayedData().missed)
    }

    fun getDisplayedFouled(): String {
        return getDisplayedValue(getDisplayedData().fouled)
    }

    fun getDisplayedRate(): String {
        return getDisplayedData().getRate().replace("%", "")
    }

    private fun getDisplayedValue(num: Int): String {
        val player = getDisplayedData()
        val total = player.potted + player.missed + player.fouled
        return getRate(num, total)
    }

    private fun getRate(num: Int, total: Int): String {
        if (num == 0) {
            return "0/$total  (0%)"
        }
        val n = (num.toDouble() / total) * 100
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.CEILING
        return "$num/$total  (${df.format(n)} %)"
    }

    private fun getDisplayedRecords(name: String) {
        _displayedRecord = MutableLiveData()
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
                    _displayedRecord.postValue(list as ArrayList<PlayerRecordItem>?)
                }
        )
    }

    fun removeRecord(i: Int) {
        val record = getDisplayedRecordsList()[i]
        val name = record.name
        Single.fromCallable { repository.deleteRecord(record) }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).doOnSuccess { getDisplayedRecords(name) }
            .subscribe()
    }

    fun updateRecord(recordItem: PlayerRecordItem) {
        Single.fromCallable { repository.updateRecord(recordItem) }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                getDisplayedRecords(getDisplayedRecordsList()[0].name)
            }.subscribe()
    }

    // -- editing fragment
    private val _editingRecord = MutableLiveData<PlayerRecordItem>()
    private lateinit var savedEditingRecord: PlayerRecordItem

    fun isRecordChanged(): Boolean {
        return savedEditingRecord != getEditingRecord()
    }

    fun onRecordClicked(i: Int) {
        val record = getDisplayedRecordsList()[i].copy()
        savedEditingRecord = getDisplayedRecordsList()[i]
        _editingRecord.postValue(record)
    }

    fun getEditingRecordLiveData(): LiveData<PlayerRecordItem> {
        return _editingRecord
    }

    fun getEditingRecord(): PlayerRecordItem {
        return _editingRecord.value!!
    }

    fun addPotted() {
        val record = getEditingRecord()
        record.potted += 1
        record.updateRate()
        _editingRecord.postValue(record)
    }

    fun removePotted() {
        val record = getEditingRecord()
        if (record.potted > 0) {
            record.potted -= 1
            record.updateRate()
            _editingRecord.postValue(record)
        }
    }

    fun addMissed() {
        val record = getEditingRecord()
        record.missed += 1
        record.updateRate()
        _editingRecord.postValue(record)
    }

    fun removeMissed() {
        val record = getEditingRecord()
        if (record.missed > 0) {
            record.missed -= 1
            record.updateRate()
            _editingRecord.postValue(record)
        }
    }

    fun addFouled() {
        val record = getEditingRecord()
        record.fouled += 1
        record.updateRate()
        _editingRecord.postValue(record)
    }

    fun removeFouled() {
        val record = getEditingRecord()
        if (record.fouled > 0) {
            record.fouled -= 1
            record.updateRate()
            _editingRecord.postValue(record)
        }
    }

    // --

    fun onSavedPlayerClicked(i: Int) {
        val name = getSavedPlayerList()[i].name
        getDisplayedRecords(name)
        Log.e("vm", "name: $name")
    }


    fun getAllSavedPlayers() {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(
            repository.getAll().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { e -> Log.e("tag", "error when $e") }.subscribe { list ->
                    _savedPlayers.postValue(getSavedPlayers(list.sortedBy { it.name }))
                }
        )
    }

    fun getSavedPlayerLiveDate(): LiveData<ArrayList<Player>> {
        if (_savedPlayers.value == null) {
            _savedPlayers.value = arrayListOf()
        }
        return _savedPlayers
    }

    fun getSavedPlayerList(): ArrayList<Player> {
        return getSavedPlayerLiveDate().value!!
    }

    private fun getSavedPlayers(list: List<PlayerRecordItem>): ArrayList<Player> {
        val players: ArrayList<Player> = arrayListOf()
        var name = ""
        for (i in list.indices) {
            if (list[i].name != name) {
                // a new player, add to the list
                val player = Player(list[i].name, list[i].potted, list[i].missed, list[i].fouled)
                players.add(player)
                name = list[i].name
            } else {
                players[players.lastIndex].potted += list[i].potted
                players[players.lastIndex].missed += list[i].missed
                players[players.lastIndex].fouled += list[i].fouled
            }
        }
        return players
    }

    fun getSavedPlayerName(i: Int): String {
        return getSavedPlayerList()[i].name

    }

    fun removeSavedPlayer(i: Int) {
        val name = getSavedPlayerName(i)

        Single.fromCallable { repository.deletePlayer(name) }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).doOnSuccess {
                // update the saved players list when the data is deleted
                getAllSavedPlayers()
            }.subscribe()
    }

    // database --

    // fragments
    private var desId = -1


    fun setDesId(id: Int) {
        desId = id
    }

    private fun getDesId(): Int {
        return desId
    }

    fun isMainFragment(): Boolean {
        return getDesId() == R.id.mainFragment
    }

    // fragments

    // double click to exit the app
    private var isBackClicked = false


    fun isAllowedToExit(): Boolean {
        return isBackClicked
    }

    fun requestToExit() {
        isBackClicked = true
        Single.fromCallable {}.subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread()).delay(EXIT_DELAY_TIME, TimeUnit.MILLISECONDS)
            .doOnSuccess { isBackClicked = false }.subscribe()
    }

    // double click to exit the app
}