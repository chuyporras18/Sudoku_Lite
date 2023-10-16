package com.lightappsdev.sudokulite.activity.main.core

import com.lightappsdev.sudokulite.activity.main.enums.SudokuDifficultModes
import com.lightappsdev.sudokulite.activity.main.model.SudokuModel
import com.lightappsdev.sudokulite.activity.main.model.SudokuRowsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SudokuResolverProvider @Inject constructor() {

    suspend fun generateSudoku(difficult: SudokuDifficultModes = SudokuDifficultModes.EASY): List<SudokuRowsModel> {
        return withContext(Dispatchers.Default) {
            val indices = (0 until 81).toList().shuffled().take(81 - difficult.keepCount)

            val (sudoku, _) = sudokuResolver(
                List(9) { index ->
                    SudokuRowsModel(number = index + 1, position = index)
                }.shuffled() +
                        List(72) { index -> SudokuRowsModel(number = 0, position = index + 9) }
            )

            if (sudoku.isEmpty()) {
                generateSudoku(difficult)
                return@withContext emptyList()
            }

            sudoku.mapIndexed { index, rowsModel ->
                if (index in indices) {
                    rowsModel.copy(number = 0)
                } else {
                    rowsModel.copy(isLocked = true)
                }
            }
        }
    }

    suspend fun sudokuResolver(unformattedSudoku: List<SudokuRowsModel>): Pair<List<SudokuRowsModel>, Float> {
        return withContext(Dispatchers.Default) {
            val start = System.currentTimeMillis()

            val lockedPositions = unformattedSudoku.filter { rowsModel -> rowsModel.isLocked }
                .map { rowsModel -> rowsModel.position }

            val sudoku = unformattedSudoku.chunked(9)
                .map { it.map { rowsModel -> rowsModel.number }.toMutableList() }

            if (!isInitialSudokuValid(sudoku)) return@withContext Pair(emptyList(), 0F)

            fun resolve(): List<List<Int>>? {
                if (isSudokuSolved(sudoku)) {
                    return sudoku
                }

                val (i, j) = leastOptionsCells(sudoku)

                if (i == -1 || j == -1) {
                    return sudoku
                }

                for (n in (1..9)) {
                    if (sudoku[i][j] != 0) continue

                    if (isSudokuNumberValid(sudoku, sudokuToQuadrants(sudoku), i, j, n)) {
                        putSudokuNumber(sudoku, i, j, n)

                        if (resolve() != null) {
                            return sudoku
                        }

                        putSudokuNumber(sudoku, i, j, 0)
                    }
                }

                return null
            }

            val list = resolve().orEmpty()
                .flatMap { list -> list.map { number -> SudokuRowsModel(number) } }
                .onEachIndexed { index, sudokuRowsModel ->
                    sudokuRowsModel.position = index
                    sudokuRowsModel.isLocked = index in lockedPositions
                }

            Pair(list, (System.currentTimeMillis() - start) / 1000F)
        }
    }

    private fun isInitialSudokuValid(sudoku: List<MutableList<Int>>): Boolean {
        sudoku.forEachIndexed list@{ i, list ->
            list.forEachIndexed int@{ j, int ->
                if (int == 0) return@int

                sudoku[i][j] = 0

                if (!isSudokuNumberValid(sudoku, sudokuToQuadrants(sudoku), i, j, int)) {
                    return false
                }

                sudoku[i][j] = int
            }
        }
        return true
    }

    private fun isSudokuSolved(sudoku: List<List<Int>>): Boolean {
        return !sudoku.any { it.contains(0) }
    }

    fun isSudokuNumberValid(list: List<SudokuModel>, rowsModel: SudokuRowsModel, n: Int): Boolean {
        val sudoku = list.filterIsInstance<SudokuRowsModel>()
            .map { sudokuRowsModel -> sudokuRowsModel.number }.chunked(9)

        val i = rowsModel.position / 9
        val j = rowsModel.position % 9

        return isSudokuNumberValid(sudoku, sudokuToQuadrants(sudoku), i, j, n)
    }

    private fun isSudokuNumberValid(
        sudoku: List<List<Int>>,
        quadrants: List<List<Int>>,
        i: Int,
        j: Int,
        n: Int
    ): Boolean {
        val board = getSudokuQuadrants(quadrants, i, j)

        val square = !board.contains(n)

        val horizontal = !sudoku[i].contains(n)

        val vertical = !sudoku.map { it[j] }.contains(n)

        return square && horizontal && vertical
    }

    private fun sudokuToQuadrants(sudoku: List<List<Int>>): List<List<Int>> {
        return sudoku.chunked(3).flatMap { quadrant ->
            val chunked = quadrant.flatMap { list ->
                list.chunked(3)
            }

            val modules =
                (0..chunked.size).associateBy({ it % 3 }, { emptyList<Int>() }).toMutableMap()

            chunked.forEachIndexed { index, ints ->
                modules[index % 3] = modules[index % 3].orEmpty() + ints
            }

            modules.values
        }
    }

    private fun leastOptionsCells(sudoku: List<List<Int>>): Pair<Int, Int> {
        val row = sudoku.withIndex().filter { it.value.contains(0) }
            .minBy { (_, int) -> int.count { it == 0 } }.index
        val colum = sudoku[row].indexOf(0)
        return Pair(row, colum)
    }

    private fun getSudokuQuadrants(board: List<List<Int>>, i: Int = 0, j: Int = 6): List<Int> {
        val index = (i / 3) * 3 + j / 3
        return board[index]
    }

    private fun putSudokuNumber(sudoku: List<MutableList<Int>>, i: Int, j: Int, n: Int) {
        sudoku[i][j] = n
    }
}