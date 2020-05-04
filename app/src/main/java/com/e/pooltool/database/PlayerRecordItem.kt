package com.e.pooltool.database

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.RoundingMode
import java.text.DecimalFormat


@Entity
class PlayerRecordItem(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "potted") var potted: Int,
    @ColumnInfo(name = "missed") var missed: Int,
    @ColumnInfo(name = "fouled") var fouled: Int,
    @ColumnInfo(name = "rate") var rate: String,
    @ColumnInfo(name = "date") var date: String
) {
    fun getTotal(): Int {
        return potted + missed + fouled
    }

    override fun hashCode(): Int {
        return id // the primary key in the database
    }

    fun updateRate() {
        if (potted == 0) {
            rate = "0 %"
            return
        }
        val num = (potted.toDouble() / (potted + missed + fouled)) * 100
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.CEILING
        rate = "${df.format(num)} %"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || javaClass != other.javaClass) return false
        val record = other as PlayerRecordItem
        return id == record.id && name == record.name && potted == record.potted &&
                missed == record.missed && fouled == record.fouled && rate == record.rate &&
                date == record.date
    }
}