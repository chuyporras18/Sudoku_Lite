package com.lightappsdev.sudokulite.activity.main.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lightappsdev.sudokulite.R
import com.lightappsdev.sudokulite.activity.main.enums.SudokuModelTypes
import com.lightappsdev.sudokulite.activity.main.listener.SudokuNumberListener
import com.lightappsdev.sudokulite.activity.main.model.SudokuModel
import com.lightappsdev.sudokulite.activity.main.model.SudokuRowsModel
import com.lightappsdev.sudokulite.addForegroundRipple
import com.lightappsdev.sudokulite.basicDiffUtil
import com.lightappsdev.sudokulite.databinding.DividerCrossLayoutBinding
import com.lightappsdev.sudokulite.databinding.DividerHorizontalLayoutBinding
import com.lightappsdev.sudokulite.databinding.DividerVerticalLayoutBinding
import com.lightappsdev.sudokulite.databinding.ItemNumberPlaceholderBinding
import kotlin.math.roundToInt

class SudokuAdapter(var sudokuNumberListener: SudokuNumberListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val MAX_ITEMS_ROW: Int = 11
    }

    var list: List<SudokuModel> by basicDiffUtil(
        areItemsTheSame = { old, new ->
            when {
                old is SudokuRowsModel && new is SudokuRowsModel -> old.position == new.position
                else -> false
            }
        }
    )

    inner class ViewHolder(private val binding: ItemNumberPlaceholderBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.cardView.setOnClickListener(this)
        }

        fun bind(rowsModel: SudokuRowsModel) {
            if (!rowsModel.isLocked) {
                binding.cardView.addForegroundRipple()

                val color = ContextCompat.getColor(binding.root.context, R.color.black_white)
                binding.numberTV.setTextColor(color)
            } else {
                binding.cardView.setCardBackgroundColor(Color.LTGRAY)
                binding.numberTV.setTextColor(Color.BLACK)
            }

            binding.numberTV.text = rowsModel.number.takeIf { it != 0 }?.toString() ?: ""

            binding.cardView.background?.alpha = if (rowsModel.isSelected) 128 else 255
        }

        override fun onClick(v: View) {
            if (adapterPosition == RecyclerView.NO_POSITION || (list[adapterPosition] as SudokuRowsModel).isLocked) return

            when (v) {
                binding.cardView -> sudokuNumberListener.onSudokuNumberSelected(adapterPosition)
            }
        }
    }

    inner class ViewHolderDividerVertical(binding: DividerVerticalLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ViewHolderDividerHorizontal(binding: DividerHorizontalLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ViewHolderDividerCross(binding: DividerCrossLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.isVisible = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (SudokuModelTypes.values()[viewType]) {
            SudokuModelTypes.VERTICAL -> {
                val binding =
                    DividerVerticalLayoutBinding.inflate(layoutInflater, parent, false).apply {
                        val height = (parent.height.toFloat() / MAX_ITEMS_ROW).roundToInt()

                        root.layoutParams.width = height
                    }
                ViewHolderDividerVertical(binding)
            }

            SudokuModelTypes.HORIZONTAL -> {
                val binding =
                    DividerHorizontalLayoutBinding.inflate(layoutInflater, parent, false).apply {
                        val height = (parent.height.toFloat() / MAX_ITEMS_ROW).roundToInt()

                        root.layoutParams.height = height
                    }
                ViewHolderDividerHorizontal(binding)
            }

            SudokuModelTypes.CROSS -> {
                val binding =
                    DividerCrossLayoutBinding.inflate(layoutInflater, parent, false).apply {
                        adjustViews(parent, root)
                    }
                ViewHolderDividerCross(binding)
            }

            SudokuModelTypes.ROW -> {
                val binding =
                    ItemNumberPlaceholderBinding.inflate(layoutInflater, parent, false).apply {
                        adjustViews(parent, root)
                    }
                ViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]

        when (holder) {
            is ViewHolderDividerHorizontal -> {}
            is ViewHolderDividerVertical -> {}
            is ViewHolder -> holder.bind(item as SudokuRowsModel)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    private fun adjustViews(parent: ViewGroup, root: View) {
        val height = (parent.height.toFloat() / MAX_ITEMS_ROW).roundToInt()

        root.layoutParams.width = height
        root.layoutParams.height = height
    }
}