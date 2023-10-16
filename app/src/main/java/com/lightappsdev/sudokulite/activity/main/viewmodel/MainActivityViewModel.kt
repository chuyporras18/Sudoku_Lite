package com.lightappsdev.sudokulite.activity.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightappsdev.sudokulite.activity.main.adapter.SudokuAdapter
import com.lightappsdev.sudokulite.activity.main.core.SudokuResolverProvider
import com.lightappsdev.sudokulite.activity.main.enums.SudokuDifficultModes
import com.lightappsdev.sudokulite.activity.main.enums.SudokuModelTypes
import com.lightappsdev.sudokulite.activity.main.model.ResolverResultModel
import com.lightappsdev.sudokulite.activity.main.model.SudokuRowsModel
import com.lightappsdev.sudokulite.activity.main.use_cases.EraseSelectedNumbersUseCase
import com.lightappsdev.sudokulite.activity.main.use_cases.FillDividersToSudokuUseCase
import com.lightappsdev.sudokulite.activity.main.use_cases.GenerateEmptySudokuUseCase
import com.lightappsdev.sudokulite.activity.main.use_cases.KeyboardNumberSelectedUseCase
import com.lightappsdev.sudokulite.activity.main.use_cases.SudokuNumbersSelectedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sudokuResolverProvider: SudokuResolverProvider,
    private val fillDividersToSudokuUseCase: FillDividersToSudokuUseCase,
    private val eraseSelectedNumbersUseCase: EraseSelectedNumbersUseCase,
    private val generateEmptySudokuUseCase: GenerateEmptySudokuUseCase,
    private val keyboardNumberSelectedUseCase: KeyboardNumberSelectedUseCase,
    private val sudokuNumbersSelectedUseCase: SudokuNumbersSelectedUseCase
) :
    ViewModel() {

    private val _sudokuAdapter: MutableLiveData<SudokuAdapter> = MutableLiveData()
    val sudokuAdapter: LiveData<SudokuAdapter> = _sudokuAdapter

    private val _isKeyboardEnabled: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val isKeyboardEnabled: LiveData<Boolean> = _isKeyboardEnabled

    private val _isResolveButtonEnabled: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val isResolveButtonEnabled: LiveData<Boolean> = _isResolveButtonEnabled

    private val _resolverResult: MutableLiveData<ResolverResultModel?> = MutableLiveData(null)
    val resolverResult: LiveData<ResolverResultModel?> = _resolverResult

    private val _isResolving: MutableLiveData<Boolean> = MutableLiveData(false)
    val isResolving: LiveData<Boolean> = _isResolving

    private val _isAutoSelectionChecked: MutableLiveData<Boolean> = MutableLiveData(false)
    val isAutoSelectionChecked: LiveData<Boolean> = _isAutoSelectionChecked

    private val _difficultMode: MutableLiveData<SudokuDifficultModes> =
        MutableLiveData<SudokuDifficultModes>(SudokuDifficultModes.EASY)
    val difficultMode: LiveData<SudokuDifficultModes> = _difficultMode

    private var resolverJob: Job? = null
    private var syncJob: Job? = null

    var isInitialized: Boolean = false

    fun syncSudoku() {
        _sudokuAdapter.value?.let { sudokuAdapter ->
            _isResolving.value = true
            syncJob?.cancel()

            syncJob = viewModelScope.launch {
                val sudokuResolved = fillDividersToSudokuUseCase.invoke(
                    sudokuResolverProvider.generateSudoku(difficultMode())
                )

                _resolverResult.value = null

                sudokuAdapter.list = sudokuResolved

                isResolveButtonEnabled()

                isInitialized = true

                _isResolving.value = false
            }
        }
    }

    fun resolveSudoku() {
        _sudokuAdapter.value?.let { sudokuAdapter ->
            _resolverResult.value = null
            _isResolveButtonEnabled.value = false
            resolverJob?.cancel()

            resolverJob = viewModelScope.launch {
                _isResolving.value = true

                val (sudokuResolved, resolutionTime) = sudokuResolverProvider.sudokuResolver(
                    sudokuAdapter.list.filterIsInstance(SudokuRowsModel::class.java)
                )

                if (sudokuResolved.isEmpty()) {
                    _resolverResult.value = ResolverResultModel.Error()
                } else {
                    _resolverResult.value = ResolverResultModel.Solved(resolutionTime)

                    sudokuAdapter.list = fillDividersToSudokuUseCase.invoke(sudokuResolved)
                }

                isKeyboardEnabled()

                _isResolving.value = false
            }
        }
    }

    fun onSudokuNumberSelected(position: Int) {
        if (!isSudokuNumberEnabled()) return

        viewModelScope.launch {
            _sudokuAdapter.value?.let { sudokuAdapter ->
                val currentRowsModel = sudokuAdapter.list[position] as SudokuRowsModel

                currentRowsModel.isSelected = !currentRowsModel.isSelected
                sudokuAdapter.notifyItemChanged(position)

                isKeyboardEnabled()

                if (currentRowsModel.isSelected && currentRowsModel.number != 0 && _isAutoSelectionChecked.value == true) {
                    sudokuAdapter.list = sudokuNumbersSelectedUseCase.invoke(
                        sudokuAdapter.list,
                        currentRowsModel.number
                    )
                }
            }
        }
    }

    fun onKeyboardNumberSelected(keyboardNumber: CharSequence) {
        viewModelScope.launch {
            _sudokuAdapter.value?.let { sudokuAdapter ->
                val (list, isComplete) =
                    keyboardNumberSelectedUseCase.invoke(keyboardNumber, sudokuAdapter.list)

                sudokuAdapter.list = list

                isKeyboardEnabled()

                if (isComplete) {
                    resolveSudoku()
                }
            }
        }
    }

    fun clearSudoku() {
        viewModelScope.launch {
            _sudokuAdapter.value?.let { sudokuAdapter ->
                _resolverResult.value = null

                sudokuAdapter.list = generateEmptySudokuUseCase.invoke()

                isKeyboardEnabled()
                isResolveButtonEnabled()
            }
        }
    }

    fun eraseSudokuNumberSelected() {
        viewModelScope.launch {
            _sudokuAdapter.value?.let { sudokuAdapter ->
                sudokuAdapter.list = eraseSelectedNumbersUseCase.invoke(sudokuAdapter.list)
            }
        }
    }

    private fun isKeyboardEnabled() {
        _sudokuAdapter.value?.let { sudokuAdapter ->
            _isKeyboardEnabled.value = sudokuAdapter.list.filterIsInstance<SudokuRowsModel>()
                .any { sudokuModel -> sudokuModel.isSelected }
        }
    }

    private fun isResolveButtonEnabled() {
        _sudokuAdapter.value?.let { sudokuAdapter ->
            _isResolveButtonEnabled.value =
                sudokuAdapter.list.any { sudokuModel ->
                    SudokuModelTypes.values()[sudokuModel.viewType] == SudokuModelTypes.ROW && (sudokuModel as SudokuRowsModel).number != 0
                }
        }
    }

    private fun isSudokuNumberEnabled(): Boolean {
        return _isResolveButtonEnabled.value == true
    }

    private fun difficultMode(): SudokuDifficultModes {
        return _difficultMode.value ?: SudokuDifficultModes.EASY
    }

    fun isCurrentDifficult(difficultMode: SudokuDifficultModes): Boolean {
        return _difficultMode.value == difficultMode
    }

    fun toggleAutoSelectionChecked() {
        _isAutoSelectionChecked.value = !(_isAutoSelectionChecked.value ?: false)
    }

    fun sudokuAdapter(sudokuAdapter: SudokuAdapter) {
        _sudokuAdapter.value = sudokuAdapter
    }

    fun difficultMode(difficultMode: SudokuDifficultModes) {
        if (difficultMode() == difficultMode) return

        _difficultMode.value = difficultMode
    }
}