package com.lightappsdev.sudokulite

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.gson.Gson
import kotlin.properties.Delegates

val application: App by lazy { App.instance }

val gson: Gson = Gson()
var toast: Toast? = null

fun <VH : RecyclerView.ViewHolder, T> RecyclerView.Adapter<VH>.basicDiffUtil(
    initialList: List<T> = emptyList(),
    areItemsTheSame: (T, T) -> Boolean = { old, new -> old == new },
    areContentsTheSame: (T, T) -> Boolean = { old, new -> old == new }
) =
    Delegates.observable(initialList) { _, old, new ->
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = old.size

            override fun getNewListSize(): Int = new.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                areItemsTheSame(old[oldItemPosition], new[newItemPosition])

            override fun areContentsTheSame(
                oldItemPosition: Int,
                newItemPosition: Int
            ): Boolean =
                areContentsTheSame(old[oldItemPosition], new[newItemPosition])

        }).dispatchUpdatesTo(this)
    }

fun View.addBackgroundRipple() {
    with(TypedValue()) {
        isClickable = true
        isFocusable = true
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
        setBackgroundResource(resourceId)
    }
}

fun View.addForegroundRipple() {
    with(TypedValue()) {
        isClickable = true
        isFocusable = true
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            foreground = ContextCompat.getDrawable(context, resourceId)
        }
    }
}

inline fun <reified T> String.fromJson(): T {
    return gson.fromJson(this, T::class.java)
}

fun RecyclerView.saveState(outState: Bundle?) {
    outState?.putParcelable("rvState_${this.id}", this.layoutManager?.onSaveInstanceState())
}

@Suppress("DEPRECATION")
fun RecyclerView.restoreState(savedInstanceState: Bundle?) {
    val state = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        savedInstanceState?.getParcelable("rvState_${this.id}", Parcelable::class.java)
    } else {
        savedInstanceState?.getParcelable("rvState_${this.id}")
    }
    layoutManager?.onRestoreInstanceState(state)
}

fun RecyclerView.disableAnimations() {
    (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
}

fun toast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    Handler(Looper.getMainLooper()).post {
        toast?.cancel()
        toast = Toast.makeText(application.applicationContext, message, duration)
        toast?.show()
    }
}

fun adjustRecyclerViewItems(
    root: View,
    view: View,
    parent: ViewGroup,
    hCount: Int,
    vCount: Int = hCount
) {
    val maxWidthItem = (parent.width * .95f) / hCount
    val maxHeightItem = (parent.height * .95f) / vCount

    val ratio = 1f

    var widthItem = maxHeightItem / ratio
    val heightItem: Float

    if (widthItem > maxWidthItem) {
        widthItem = maxWidthItem
        heightItem = widthItem * ratio
    } else {
        heightItem = maxHeightItem
    }

    view.layoutParams.width = widthItem.toInt()
    view.layoutParams.height = heightItem.toInt()

    root.layoutParams.width = maxWidthItem.toInt()
    root.layoutParams.height = maxHeightItem.toInt()
}