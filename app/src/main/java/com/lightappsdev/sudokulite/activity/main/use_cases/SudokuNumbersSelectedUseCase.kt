package com.lightappsdev.sudokulite.activity.main.use_cases

import com.lightappsdev.sudokulite.activity.main.model.SudokuModel
import com.lightappsdev.sudokulite.activity.main.model.SudokuRowsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SudokuNumbersSelectedUseCase @Inject constructor(private val fillDividersToSudokuUseCase: FillDividersToSudokuUseCase) {

    suspend operator fun invoke(list: List<SudokuModel>, sudokuNumber: Int): List<SudokuModel> {
        return withContext(Dispatchers.Default) {
            val rowsModels = list.filterIsInstance<SudokuRowsModel>()

            fillDividersToSudokuUseCase.invoke(rowsModels.map { rowsModel ->
                if (rowsModel.number == sudokuNumber) {
                    rowsModel.copy(isSelected = true)
                } else {
                    rowsModel
                }
            })
        }
    }
}