package com.e.pooltool.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PlayerRecordItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerRecordDao(): PlayerRecordDao
}