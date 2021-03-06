package com.e.pooltool

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.math.RoundingMode
import java.text.DecimalFormat

@Parcelize
data class Player(var name: String, var potted: Int = 0, var missed: Int = 0, var fouled: Int = 0) :
    Parcelable {

    fun getRate(): String {
        if (potted == 0) {
            return "0 %"
        }
        val num = (potted.toDouble() / (potted + missed + fouled)) * 100
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.CEILING
        return "${df.format(num)} %"
    }

    fun getTotal(): Int {
        return potted + missed + fouled
    }

    fun reset() {
        potted = 0
        missed = 0
        fouled = 0
    }
}