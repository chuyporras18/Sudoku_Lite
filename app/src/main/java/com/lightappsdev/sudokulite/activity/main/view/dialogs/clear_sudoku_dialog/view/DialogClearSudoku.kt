package com.lightappsdev.sudokulite.activity.main.view.dialogs.clear_sudoku_dialog.view

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.lightappsdev.sudokulite.activity.main.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DialogClearSudoku : DialogFragment() {

    companion object {
        const val TAG: String = "DialogClearSudoku"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).setTitle("Clear Sudoku Board")
            .setMessage("The Sudoku Board will be cleared. Are you sure?")
            .setPositiveButton("Yes") { dialog, _ ->
                ViewModelProvider(requireActivity())[MainActivityViewModel::class.java].clearSudoku()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .create()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(MATCH_PARENT, WRAP_CONTENT)
    }
}