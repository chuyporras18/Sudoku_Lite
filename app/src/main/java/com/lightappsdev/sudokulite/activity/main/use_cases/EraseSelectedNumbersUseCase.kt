package com.lightappsdev.sudokulite.activity.main.use_cases

import androidx.annotation.WorkerThread
import com.lightappsdev.sudokulite.activity.main.model.SudokuModel
import com.lightappsdev.sudokulite.activity.main.model.SudokuRowsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EraseSelectedNumbersUseCase @Inject constructor(private val fillDividersToSudokuUseCase: FillDividersToSudokuUseCase) {

    @WorkerThread
    suspend operator fun invoke(list: List<SudokuModel>): List<SudokuModel> {
        return withContext(Dispatchers.Default) {
            val rowsModels = list.filterIsInstance<SudokuRowsModel>()

            fillDividersToSudokuUseCase.invoke(rowsModels.map { rowsModel ->
                if (rowsModel.isSelected) {
                    rowsModel.copy(number = 0, isSelected = false)
                } else {
                    rowsModel
                }
            })

        }
    }
}