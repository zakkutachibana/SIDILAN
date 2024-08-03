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
import com.itextpdf.kernel.colors.DeviceRgb
import java.util.Calendar
import java.util.Date

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
        return if (inputDate != null && (inputDate.matches(Regex("\\d{2}/\\d{2}/\\d{4}")))) {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            val date = inputDate.let { inputFormat.parse(it) }
            date?.let { outputFormat.format(it) }
        } else inputDate
    }

    fun convertDateAPIToDisplay(inputDate: String?): String? {
        return if (inputDate != null && inputDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                val date = inputFormat.parse(inputDate)
                date?.let { outputFormat.format(it) }
            } catch (e: ParseException) {
                inputDate
            }
        } else {
            inputDate
        }
    }

    fun convertDateMonthToDisplay(inputDate: String?): String? {
        return if (inputDate != null && inputDate.matches(Regex("\\d{4}/\\d{2}"))) {
            try {
                val inputFormat = SimpleDateFormat("yyyy/MM", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
                val date = inputFormat.parse(inputDate)
                date?.let { outputFormat.format(it) }
            } catch (e: ParseException) {
                inputDate
            }
        } else {
            inputDate
        }
    }

    fun convertDateAPIToFirebase(inputDate: String?): String? {
        return if (inputDate != null && inputDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = inputFormat.parse(inputDate)
                date?.let { outputFormat.format(it) }
            } catch (e: ParseException) {
                inputDate
            }
        } else {
            inputDate
        }
    }

    fun getRawValue(editText: EditText): String {
        return editText.text.toString().replace("[.,]".toRegex(), "")
    }

    fun convertEpochToLocal(timestamp: @RawValue Any?): String {
        return if (timestamp != null) {
            val instant = Instant.ofEpochMilli(timestamp.toString().toLong())
            val zoneId = ZoneId.of("Asia/Jakarta")
            val zonedDateTime = instant.atZone(zoneId)
            val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy (HH:mm)", Locale("id", "ID"))
            zonedDateTime.format(formatter)
        } else ""
    }

    fun String.toDate(format: String = "dd/MM/yyyy"): Date? {
        return try {
            SimpleDateFormat(format, Locale.getDefault()).parse(this)
        } catch (e: Exception) {
            null
        }
    }

    fun convertToInternationalFormat(phoneNumber: String): String {
        // Assuming the local format has a leading '0' which needs to be removed
        val cleanedPhoneNumber = phoneNumber.removePrefix("0")
        // Adding the country code prefix
        return "62$cleanedPhoneNumber"
    }

    fun hexToRgb(hex: String): DeviceRgb {
        val color = Integer.parseInt(hex.substring(1), 16)
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF
        return DeviceRgb(r, g, b)
    }
    fun convertNumberToWords(nominal: Long): String {
        val satuBelasDuaPuluh = arrayOf("", "satu", "dua", "tiga", "empat", "lima", "enam", "tujuh", "delapan", "sembilan", "sepuluh", "sebelas")

        if (nominal < 12) {
            return satuBelasDuaPuluh[nominal.toInt()]
        }

        if (nominal < 20) {
            return satuBelasDuaPuluh[(nominal - 10).toInt()] + " belas"
        }

        if (nominal < 100) {
            return satuBelasDuaPuluh[(nominal / 10).toInt()] + " puluh " + satuBelasDuaPuluh[(nominal % 10).toInt()]
        }

        if (nominal < 200) {
            return "seratus " + convertNumberToWords(nominal - 100)
        }

        if (nominal < 1000) {
            return satuBelasDuaPuluh[(nominal / 100).toInt()] + " ratus " + convertNumberToWords(nominal % 100)
        }

        if (nominal < 2000) {
            return "seribu " + convertNumberToWords(nominal - 1000)
        }

        if (nominal < 1000000) {
            return convertNumberToWords(nominal / 1000) + " ribu " + convertNumberToWords(nominal % 1000)
        }

        if (nominal < 1000000000) {
            return convertNumberToWords(nominal / 1000000) + " juta " + convertNumberToWords(nominal % 1000000)
        }

        if (nominal < 1000000000000) {
            return convertNumberToWords(nominal / 1000000000) + " miliar " + convertNumberToWords(nominal % 1000000000)
        }

        return "Nominal terlalu besar"
    }

    private fun getRomanNumeral(month: Int): String {
        val romanNumerals = arrayOf(
            "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"
        )
        return romanNumerals[month - 1]
    }

    private fun getYearAndMonth(dateString: String): Pair<Int, Int>? {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            val date = dateFormat.parse(dateString)
            if (date != null) {
                val calendar = Calendar.getInstance()
                calendar.time = date
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is zero-based
                Pair(year, month)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun generateInvoiceNumber(invoiceNumber: Int, date: String): String {
        val yearMonth = getYearAndMonth(date)
        val formattedInvoiceNumber = String.format("%03d", invoiceNumber)
        val romanMonth = getRomanNumeral(yearMonth?.second!!)
        val year = yearMonth.first

        return "$formattedInvoiceNumber/PP/$romanMonth/$year"
    }

}

