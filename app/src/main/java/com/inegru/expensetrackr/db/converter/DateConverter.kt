package com.inegru.expensetrackr.db.converter

import androidx.room.TypeConverter
import com.inegru.expensetrackr.common.utils.DateUtils
import java.time.LocalDate

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { DateUtils.parseDate(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.let { DateUtils.formatDate(it) }
    }
}