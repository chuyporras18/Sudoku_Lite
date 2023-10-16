package com.lightappsdev.sudokulite.activity.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.recyclerview.widget.GridLayoutManager
import com.lightappsdev.sudokulite.activity.main.adapter.SudokuAdapter
import com.lightappsdev.sudokulite.activity.main.enums.SudokuDifficultModes
import com.lightappsdev.sudokulite.activity.main.listener.SudokuNumberListener
import com.lightappsdev.sudokulite.activity.main.view.dialogs.clear_sudoku_dialog.view.DialogClearSudoku
import com.lightappsdev.sudokulite.activity.main.viewmodel.MainActivityViewModel
import com.lightappsdev.sudokulite.addForegroundRipple
import com.lightappsdev.sudokulite.databinding.ActivityMainBinding
import com.lightappsdev.sudokulite.databinding.DifficultyButtonLayoutBinding
import com.lightappsdev.sudokulite.databinding.KeyboardButtonLayoutBinding
import com.lightappsdev.sudokulite.restoreState
import com.lightappsdev.sudokulite.saveState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SudokuNumberListener {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.recyclerView.apply {
            val sudokuNumberListener = this@MainActivity
            adapter = viewModel.sudokuAdapter.value
                ?: SudokuAdapter(sudokuNumberListener).also { sudokuAdapter ->
                    viewModel.sudokuAdapter(sudokuAdapter)
                }
            layoutManager = GridLayoutManager(context, SudokuAdapter.MAX_ITEMS_ROW)
            restoreState(savedInstanceState)
            itemAnimator = null
        }

        binding.resolveBtn.apply {
            addForegroundRipple()
            setOnClickListener { viewModel.resolveSudoku() }
        }

        binding.autoSelectionBtn.apply {
            addForegroundRipple()
            setOnClickListener { viewModel.toggleAutoSelectionChecked() }
        }

        binding.syncBtn.apply {
            addForegroundRipple()
            setOnClickListener { viewModel.syncSudoku() }
        }

        binding.clearBtn.apply {
            addForegroundRipple()
            setOnClickListener {
                DialogClearSudoku().show(supportFragmentManager, DialogClearSudoku.TAG)
            }
        }

        binding.eraseBtn.apply {
            addForegroundRipple()
            setOnClickListener { viewModel.eraseSudokuNumberSelected() }
        }

        bindNumberButtons()

        bindSudokuDifficult()

        viewModel.isKeyboardEnabled.observe(this) { isEnabled ->
            getKeyboardNumbersTextView().forEach { keyboardButton ->
                keyboardButton.cardview.isEnabled = isEnabled
            }
            binding.eraseBtn.isEnabled = isEnabled
            binding.eraseTV.isEnabled = isEnabled
        }

        viewModel.isResolveButtonEnabled.observe(this) { isEnabled ->
            binding.resolveBtn.isEnabled = isEnabled
            binding.autoSelectionBtn.isEnabled = isEnabled
            binding.autoSelectionTV.isEnabled = isEnabled
        }

        viewModel.resolverResult.observe(this) { resolverResult ->
            binding.recyclerView.adapter = binding.recyclerView.adapter

            if (resolverResult == null) {
                binding.resultIV.setImageDrawable(null)
                binding.resultTV.text = "Resolve Sudoku"
                return@observe
            }

            binding.resultIV.apply {
                val drawableIcon =
                    ContextCompat.getDrawable(context, resolverResult.getResultIcon())
                setImageDrawable(drawableIcon)
            }
            binding.resultTV.text = resolverResult.getResultMessage()
        }

        viewModel.isResolving.observe(this) { isResolving ->
            binding.progressBar.visibility = if (isResolving) View.VISIBLE else View.INVISIBLE
        }

        viewModel.isAutoSelectionChecked.observe(this) { isChecked ->
            binding.autoSelectionBtn.background?.alpha = if (isChecked) (255 * 0.8).toInt() else 255
        }

        viewModel.difficultMode.observe(this) { difficultMode ->
            bindSudokuDifficult()
        }

        setContentView(binding.root)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (!viewModel.isInitialized) {
            viewModel.syncSudoku()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.recyclerView.saveState(outState)
    }

    override fun onSudokuNumberSelected(position: Int) {
        viewModel.onSudokuNumberSelected(position)
    }

    private fun getKeyboardNumbersTextView(): List<KeyboardButtonLayoutBinding> {
        return listOf(
            binding.numberOneTV,
            binding.numberTwoTV,
            binding.numberThreeTV,
            binding.numberFourTV,
            binding.numberFiveTV,
            binding.numberSixTV,
            binding.numberSevenTV,
            binding.numberEightTV,
            binding.numberNineTV
        )
    }

    private fun bindNumberButtons() {
        getKeyboardNumbersTextView().forEachIndexed { index, keyboardButton ->
            keyboardButton.cardview.apply {
                addForegroundRipple()
                setOnClickListener { viewModel.onKeyboardNumberSelected(keyboardButton.textView.text) }
            }

            keyboardButton.textView.text = (index + 1).toString()
        }
    }

    private fun bindSudokuDifficult() {
        binding.difficultsLayout.removeAllViews()

        SudokuDifficultModes.values().forEach { difficultModes ->
            val inflater = LayoutInflater.from(this)
            val button = DifficultyButtonLayoutBinding.inflate(inflater, null, false).apply {
                root.apply {
                    layoutParams =
                        LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).also { params ->
                            params.weight = 1F
                            params.setMargins(8)
                        }
                }

                cardview.apply {
                    addForegroundRipple()
                    setOnClickListener {
                        viewModel.difficultMode(difficultModes)
                        viewModel.syncSudoku()
                    }
                }

                textView.apply {
                    text = difficultModes.name
                }

                view.apply {
                    isVisible = viewModel.isCurrentDifficult(difficultModes)
                }
            }
            binding.difficultsLayout.addView(button.root)
        }
    }
}