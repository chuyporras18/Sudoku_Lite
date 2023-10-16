package com.lightappsdev.sudokulite.activity.main.use_cases

import com.lightappsdev.sudokulite.activity.main.model.SudokuModel
import com.lightappsdev.sudokulite.activity.main.model.SudokuRowsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GenerateEmptySudokuUseCase @Inject constructor(private val fillDividersToSudokuUseCase: FillDividersToSudokuUseCase) {

    suspend operator fun invoke(): List<SudokuModel> {
        return withContext(Dispatchers.Default) {
            fillDividersToSudokuUseCase.invoke(
                List(81) { index ->
                    SudokuRowsModel(number = 0, position = index, isLocked = false)
                }
            )
        }
    }
}