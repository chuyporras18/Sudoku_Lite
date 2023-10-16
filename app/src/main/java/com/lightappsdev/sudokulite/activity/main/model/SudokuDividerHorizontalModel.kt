package com.lightappsdev.sudokulite.activity.main.model

import com.lightappsdev.sudokulite.activity.main.enums.SudokuModelTypes

data class SudokuDividerHorizontalModel(override val viewType: Int = SudokuModelTypes.HORIZONTAL.ordinal) :
    SudokuModel()
