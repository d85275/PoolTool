package com.e.pooltool.database

import android.content.Context
import androidx.room.Room
import io.reactivex.Single

class Repository(private val ctx: Context) {

    private val db = Room.databaseBuilder(ctx, AppDatabase::class.java, "User_Records_DB").build()

    fun getAll(): Single<List<PlayerRecordItem>> {
        return db.playerRecordDao().getAll()
    }

    fun getRecords(name: String): Single<List<PlayerRecordItem>> {
        return db.playerRecordDao().getRecords(name)
    }

    fun addRecord(playerRecord: PlayerRecordItem) {
        db.playerRecordDao().insertAll(playerRecord)
    }

    fun updateRecord(playerRecord: PlayerRecordItem) {
        db.playerRecordDao().update(playerRecord)
    }

    fun deleteRecord(playerRecord: PlayerRecordItem) {
        db.playerRecordDao().delete(playerRecord)
    }

    fun deletePlayer(name: String) {
        db.playerRecordDao().delete(name)
    }
}