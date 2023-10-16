package com.lightappsdev.sudokulite.activity.main.use_cases

import com.lightappsdev.sudokulite.activity.main.core.SudokuResolverProvider
import com.lightappsdev.sudokulite.activity.main.model.SudokuModel
import com.lightappsdev.sudokulite.activity.main.model.SudokuRowsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class KeyboardNumberSelectedUseCase @Inject constructor(
    private val fillDividersToSudokuUseCase: FillDividersToSudokuUseCase,
    private val sudokuResolverProvider: SudokuResolverProvider
) {

    suspend operator fun invoke(
        keyboardNumber: CharSequence,
        list: List<SudokuModel>
    ): Pair<List<SudokuModel>, Boolean> {
        return withContext(Dispatchers.Default) {
            val number = keyboardNumber.toString().toInt()

            val rowsModels = list.filterIsInstance<SudokuRowsModel>().map { rowsModel ->

                if (rowsModel.isSelected &&
                    sudokuResolverProvider.isSudokuNumberValid(list, rowsModel, number)
                ) {
                    rowsModel.copy(number = number, isSelected = false)
                } else {
                    rowsModel.copy(isSelected = false)
                }
            }

            Pair(
                fillDividersToSudokuUseCase.invoke(rowsModels),
                rowsModels.none { rowsModel -> rowsModel.number == 0 })
        }
    }
}