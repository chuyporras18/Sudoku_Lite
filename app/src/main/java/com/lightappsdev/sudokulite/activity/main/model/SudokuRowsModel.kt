package com.lightappsdev.sudokulite.activity.main.model

import com.lightappsdev.sudokulite.activity.main.enums.SudokuModelTypes

data class SudokuRowsModel(
    var number: Int,
    var position: Int = 0,
    var isSelected: Boolean = false,
    var isLocked: Boolean = false,
    override val viewType: Int = SudokuModelTypes.ROW.ordinal
) : SudokuModel()
