package com.lightappsdev.sudokulite.activity.main.use_cases

import com.lightappsdev.sudokulite.activity.main.enums.SudokuModelTypes
import com.lightappsdev.sudokulite.activity.main.model.SudokuDividerCrossModel
import com.lightappsdev.sudokulite.activity.main.model.SudokuDividerHorizontalModel
import com.lightappsdev.sudokulite.activity.main.model.SudokuDividerVerticalModel
import com.lightappsdev.sudokulite.activity.main.model.SudokuModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FillDividersToSudokuUseCase @Inject constructor() {

    suspend operator fun invoke(list: List<SudokuModel>): List<SudokuModel> {
        return withContext(Dispatchers.Default) {
            val filled: MutableList<SudokuModel> = list.toMutableList()

            fun fill(position: Int, orientation: SudokuModelTypes) {
                when (orientation) {
                    SudokuModelTypes.VERTICAL -> filled.add(position, SudokuDividerVerticalModel())
                    SudokuModelTypes.HORIZONTAL -> filled.add(
                        position,
                        SudokuDividerHorizontalModel()
                    )

                    SudokuModelTypes.CROSS -> filled.add(position, SudokuDividerCrossModel())
                    SudokuModelTypes.ROW -> {}
                }
            }

            fill(3, SudokuModelTypes.VERTICAL)
            fill(7, SudokuModelTypes.VERTICAL)

            fill(14, SudokuModelTypes.VERTICAL)
            fill(18, SudokuModelTypes.VERTICAL)

            fill(25, SudokuModelTypes.VERTICAL)
            fill(29, SudokuModelTypes.VERTICAL)

            for (i in 33..43) {
                if (i % 4 == 0) {
                    fill(i, SudokuModelTypes.CROSS)
                } else {
                    fill(i, SudokuModelTypes.HORIZONTAL)
                }
            }

            fill(47, SudokuModelTypes.VERTICAL)
            fill(51, SudokuModelTypes.VERTICAL)

            fill(58, SudokuModelTypes.VERTICAL)
            fill(62, SudokuModelTypes.VERTICAL)

            fill(69, SudokuModelTypes.VERTICAL)
            fill(73, SudokuModelTypes.VERTICAL)

            for (i in 77..87) {
                if (i % 4 == 0) {
                    fill(i, SudokuModelTypes.CROSS)
                } else {
                    fill(i, SudokuModelTypes.HORIZONTAL)
                }
            }

            fill(91, SudokuModelTypes.VERTICAL)
            fill(95, SudokuModelTypes.VERTICAL)

            fill(102, SudokuModelTypes.VERTICAL)
            fill(106, SudokuModelTypes.VERTICAL)

            fill(113, SudokuModelTypes.VERTICAL)
            fill(117, SudokuModelTypes.VERTICAL)

            filled
        }
    }
}