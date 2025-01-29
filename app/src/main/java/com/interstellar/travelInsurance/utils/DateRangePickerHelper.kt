package com.interstellar.travelInsurance.utils

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.interstellar.travelInsurance.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateRangePickerHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun showDateRangePicker(
        fragmentManager: FragmentManager,
        onDateRangeSelected: (startDate: String, endDate: String) -> Unit
    ){

        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())

        //region For Selecting by Default date range
//        val now = Calendar.getInstance()
//        val threeMonthsAgo = Calendar.getInstance().apply {
//            add(Calendar.MONTH, -3)
//        }
//        val oneMonthLater = Calendar.getInstance().apply {
//            add(Calendar.MONTH, 1)
//        }
//
//        val constraintsBuilder = CalendarConstraints.Builder().apply {
//            setStart(threeMonthsAgo.timeInMillis)
//            setEnd(oneMonthLater.timeInMillis)
//            setOpenAt(now.timeInMillis)
//        }
//
//        val tomorrow = Calendar.getInstance().apply {
//            add(Calendar.DATE, 1)
//        }
//        val dayAfterTomorrow = Calendar.getInstance().apply {
//            add(Calendar.DATE, 2)
//        }
        //endregion


        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(context.getString(R.string.select_date_range))
            .setTheme(R.style.ThemeMaterial3DateRangePicker)
            .setCalendarConstraints(constraintsBuilder.build())
            .setPositiveButtonText(context.getString(R.string.date_picker_done))
            .setNegativeButtonText(context.getString(R.string.date_picker_reset))
               //region commented for default range added
//            .setSelection(
//                androidx.core.util.Pair(
//                    tomorrow.timeInMillis,
//                    dayAfterTomorrow.timeInMillis
//                )
//            )
            //endregion
            .build()

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = selection.first
            val endDate = selection.second

            if (startDate != null && endDate != null) {
                val formattedStartDate = Utils.convertTimeToDate(startDate)
                val formattedEndDate = Utils.convertTimeToDate(endDate)
                onDateRangeSelected(formattedStartDate, formattedEndDate)
            }
        }

        dateRangePicker.addOnNegativeButtonClickListener {
            dateRangePicker.dismiss()
        }

        dateRangePicker.addOnCancelListener {
            dateRangePicker.dismiss()
        }

        dateRangePicker.show(fragmentManager, "DATE_RANGE_PICKER")
    }

}