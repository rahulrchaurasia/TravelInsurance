package com.interstellar.travelInsurance.utils

import android.util.Log
import java.util.Calendar


enum class DateRangeType {
    TODAY, YESTERDAY, THIS_WEEK, LAST_WEEK, THIS_MONTH, LAST_MONTH
}
//enum class DateRangeLabel {
//    Today, Yesterday, This Week ,Last Week,  This Month, Last Month
//}
data class DateRange(val startDate: Calendar, val endDate: Calendar, val label: String) {
    val formattedRange: String

    get() =  "${startDate.formatDate()} To ${endDate.formatDate()}"

    companion object {
        fun create(type: DateRangeType): DateRange {
            val calendar = Calendar.getInstance()
            return when (type) {
                DateRangeType.TODAY -> {
                    val today = calendar.clone() as Calendar
                    DateRange(today, today,  Constant.today)
                }
                DateRangeType.YESTERDAY -> {

                    val yesterday = (calendar.clone() as Calendar).apply { add(Calendar.DATE, -1) }
                    val today = calendar.clone() as Calendar
                    DateRange(yesterday, today, Constant.yesterday)
                }
                DateRangeType.THIS_WEEK -> {
                    val start = calendar.apply {
                        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                        firstDayOfWeek = Calendar.MONDAY
                    }
                    val end = start.clone() as Calendar
                    end.add(Calendar.DAY_OF_WEEK, 6)
                    DateRange(start, end, Constant.thisWeek)
                }
                DateRangeType.LAST_WEEK -> {
                    val start = calendar.apply {
                        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                        firstDayOfWeek = Calendar.MONDAY
                        add(Calendar.WEEK_OF_YEAR, -1)
                    }
                    val end = start.clone() as Calendar
                    end.add(Calendar.DAY_OF_WEEK, 6)
                    DateRange(start, end, Constant.lastWeek)
                }
                DateRangeType.THIS_MONTH -> {
                    val start = calendar.apply { set(Calendar.DAY_OF_MONTH, 1) }
                    val end = start.clone() as Calendar
                    end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH))
                    DateRange(start, end, Constant.thisMonth)
                }
                DateRangeType.LAST_MONTH -> {
                    val start = calendar.apply {
                        add(Calendar.MONTH, -1)
                        set(Calendar.DAY_OF_MONTH, 1)
                    }
                    val end = start.clone() as Calendar
                    end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH))
                    DateRange(start, end, Constant.lastMonth)
                }


            }
        }
    }
}