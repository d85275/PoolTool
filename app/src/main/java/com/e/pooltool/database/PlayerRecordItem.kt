package com.e.pooltool.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
 class PlayerRecordItem(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "potted") val potted: Int,
    @ColumnInfo(name = "missed") val missed: Int,
    @ColumnInfo(name = "rate") val rate: String,
    @ColumnInfo(name = "date") val date: String
)