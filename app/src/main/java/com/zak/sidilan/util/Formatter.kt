package com.zak.sidilan.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.parcelize.RawValue
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

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

    fun addThousandSeparatorTextView(number: Long?): String {
        return if (number != null) {
            val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
            formatter.format(number).replace(",", ".")
        } else ""
    }

    fun convertDateFirebaseToDisplay(inputDate: String?): String? {
        return if (inputDate != null) {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            val date = inputDate.let { inputFormat.parse(it) }
            date?.let { outputFormat.format(it) }
        } else ""
    }

    fun convertDateAPIToDisplay(inputDate: String?): String? {
        return if (inputDate != null) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            val date = inputDate.let { inputFormat.parse(it) }
            date?.let { outputFormat.format(it) }
        } else ""
    }
    fun convertDateAPIToFirebase(inputDate: String?): String? {
        return if (inputDate != null) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputDate.let { inputFormat.parse(it) }
            date?.let { outputFormat.format(it) }
        } else ""
    }

    fun getRawValue(editText: EditText): String {
        return editText.text.toString().replace("[.,]".toRegex(), "")
    }

    fun convertUTCToLocal(timestamp: @RawValue Any?): String {
        return if (timestamp != null) {
            val instant = Instant.ofEpochMilli(timestamp.toString().toLong())
            val zoneId = ZoneId.of("Asia/Jakarta") // Example: "America/New_York"
            val zonedDateTime = instant.atZone(zoneId)
            val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")
            zonedDateTime.format(formatter)
        } else ""
    }

    fun convertToInternationalFormat(phoneNumber: String): String {
        // Assuming the local format has a leading '0' which needs to be removed
        val cleanedPhoneNumber = phoneNumber.removePrefix("0")
        // Adding the country code prefix
        return "62$cleanedPhoneNumber"
    }

}

