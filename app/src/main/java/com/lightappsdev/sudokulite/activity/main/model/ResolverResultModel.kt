package com.lightappsdev.sudokulite.activity.main.model

import android.graphics.Color
import com.lightappsdev.sudokulite.R

sealed class ResolverResultModel {
    data class Solved(
        val resolutionTime: Float,
        val color: Int = Color.GREEN,
        val icon: Int = R.drawable.baseline_check_24
    ) :
        ResolverResultModel() {

        fun getResolutionTime(): String {
            return "Solved in ${resolutionTime}s"
        }
    }

    data class Error(
        val message: String = "Can't resolve sudoku",
        val color: Int = Color.RED,
        val icon: Int = R.drawable.baseline_close_24
    ) :
        ResolverResultModel()

    fun getResultMessage(): String {
        return when (this) {
            is Solved -> getResolutionTime()
            is Error -> message
        }
    }

    fun getResultColor(): Int {
        return when (this) {
            is Solved -> color
            is Error -> color
        }
    }

    fun getResultIcon(): Int {
        return when (this) {
            is Solved -> icon
            is Error -> icon
        }
    }
}