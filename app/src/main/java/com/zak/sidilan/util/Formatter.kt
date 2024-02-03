package com.zak.sidilan.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.DecimalFormat
import java.text.ParseException

object Formatter {

    private val decimalFormat = DecimalFormat("#,###")

    fun addThousandSeparator(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                editText.removeTextChangedListener(this)

                try {
                    val originalString = editable.toString()

                    if (originalString.isNotEmpty()) {
                        val longval = originalString.replace(".", "").replace(",", "").toLong()
                        val formattedString = decimalFormat.format(longval)
                            .replace(",", ".")
                        editText.setText(formattedString)
                        editText.setSelection(editText.text.length)
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                editText.addTextChangedListener(this)
            }
        })
    }
    fun getRawValue(editText: EditText): String {
        return editText.text.toString().replace("[.,]".toRegex(), "")
    }
}
