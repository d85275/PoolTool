package com.e.pooltool

import java.math.RoundingMode
import java.text.DecimalFormat

data class Player(var name: String, var potted: Int = 0, var missed: Int = 0) {

    fun getRate(): String {
        if (potted + missed == 0) {
            return "0 %"
        }
        var num = (potted.toDouble() / (potted + missed)) * 100
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.CEILING
        return "${df.format(num)} %"
    }
}