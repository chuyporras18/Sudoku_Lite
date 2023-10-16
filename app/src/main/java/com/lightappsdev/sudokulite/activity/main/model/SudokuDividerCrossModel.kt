package com.lightappsdev.sudokulite.activity.main.model

import com.lightappsdev.sudokulite.activity.main.enums.SudokuModelTypes

data class SudokuDividerCrossModel(override val viewType: Int = SudokuModelTypes.CROSS.ordinal) :
    SudokuModel()
