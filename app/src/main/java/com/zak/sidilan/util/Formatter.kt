package com.zak.sidilan.util

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.annotation.RequiresApi
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object Formatter {

    private val decimalFormat = DecimalFormat("#,###")

    fun addThousandSeparatorEditText(editText: EditText) {
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

    fun addThousandSeparatorTextView(number: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
        return formatter.format(number).replace(",", ".")
    }

    fun getRawValue(editText: EditText): String {
        return editText.text.toString().replace("[.,]".toRegex(), "")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertUTCToLocal(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val zoneId = ZoneId.of("Asia/Jakarta") // Example: "America/New_York"
        val zonedDateTime = instant.atZone(zoneId)
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")
        return zonedDateTime.format(formatter)
    }

}
