package com.metalpizzacat.shoppinglist.data

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

/**
 * Class that represents the key to the grouping by purchase date as a full dd-mm-yyyy date
 */
data class PurchaseDay(
    val day: Int?,
    val month: Int?,
    val year: Int?
) {

    /**
     * Get representation of the purchase day as a local date object
     */
    val localDate: LocalDate?
        get() = if (day == null || month == null || year == null) {
            null
        } else {
            LocalDate.of(year, month, day)
        }

    /**
     * Get representation of the purchase day as a java date object
     */
    val date: Date?
        get() = localDate?.atStartOfDay(
            ZoneId.systemDefault()
        )?.toInstant().let { Date.from(it) }

    /**
     * Return purchase day object as a formatted string or null if any field is null
     * @param format SimpleDateFormat pattern value
     */
    fun toFormatedString(format: String = "EEE dd-MM-yyyy"): String? =
        if (day == null || month == null || year == null) {
            null
        } else {
            SimpleDateFormat(
                format,
                Locale.getDefault()
            ).format(
                Date.from(
                    LocalDate.of(year, month, day)
                        .atStartOfDay(
                            ZoneId.systemDefault()
                        ).toInstant()
                )
            )
        }
}
