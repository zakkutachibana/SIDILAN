package com.zak.sidilan.util

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import com.google.android.material.checkbox.MaterialCheckBox
import androidx.core.content.ContextCompat
import com.zak.sidilan.R

class CustomTriStateCheckBox(context: Context, attrs: AttributeSet) : MaterialCheckBox(context, attrs) {

    private var state = CheckBoxState.UNCHECKED
    private var stateChangeListener: ((CheckBoxState) -> Unit)? = null

    init {
        updateVisualState()
    }

    private fun updateVisualState() {
        when (state) {
            CheckBoxState.CHECKED -> {
                checkedState = STATE_CHECKED
                buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.safe_green))
            }
            CheckBoxState.UNCHECKED -> {
                checkedState = STATE_UNCHECKED
                buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.safe_red))
            }
            CheckBoxState.INDETERMINATE -> {
                checkedState = STATE_INDETERMINATE
                buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.safe_yellow))
            }
        }
    }

    fun getState(): CheckBoxState {
        return state
    }

    fun setState(newState: CheckBoxState) {
        if (state != newState) {
            state = newState
            updateVisualState()
            stateChangeListener?.invoke(state)
        }
    }

    fun setOnStateChangeListener(listener: (CheckBoxState) -> Unit) {
        stateChangeListener = listener
    }
}

