package com.e.pooltool

import android.content.Context
import androidx.room.Room
import com.e.pooltool.database.AppDatabase
import com.e.pooltool.database.PlayerRecordItem
import io.reactivex.Single

class Repository(private val ctx: Context) {

    private val db = Room.databaseBuilder(ctx, AppDatabase::class.java, "User_Records_DB").build()

    fun getAll(): List<PlayerRecordItem> {
        return db.playerRecordDao().getAll()
    }

    fun getRecords(name: String): Single<List<PlayerRecordItem>> {
        return db.playerRecordDao().getRecords(name)
    }

    fun addRecord(playerRecord: PlayerRecordItem) {
        db.playerRecordDao().insertAll(playerRecord)
    }


    fun deleteRecord(playerRecord: PlayerRecordItem) {
        db.playerRecordDao().delete(playerRecord)
    }
}