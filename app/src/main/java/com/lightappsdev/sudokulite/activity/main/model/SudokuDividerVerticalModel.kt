package com.lightappsdev.sudokulite.activity.main.model

import com.lightappsdev.sudokulite.activity.main.enums.SudokuModelTypes

data class SudokuDividerVerticalModel(override val viewType: Int = SudokuModelTypes.VERTICAL.ordinal) :
    SudokuModel()
