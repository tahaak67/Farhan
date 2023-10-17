package ly.com.tahaben.usage_overview_data.local.entity

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 11,Mar,2023
 */


class LocalDateTypeConverter {

    @TypeConverter
    fun fromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
    }

    @TypeConverter
    fun toDate(dateString: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse(dateString, formatter)
    }
}