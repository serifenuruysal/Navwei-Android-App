package com.androidapp.navweiandroidv2.util.helper

import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.androidapp.entity.models.Schedule
import com.androidapp.navweiandroidv2.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by S.Nur Uysal on 2020-01-07.
 */

class ScheduleDateHelper(val schedule: Schedule?, val context: Context) {
    private val parser = SimpleDateFormat("HH:mm")
    private val parserStatuTitle = SimpleDateFormat("hh a")
    private val calendar = Calendar.getInstance()
    private var scheduleData: String? = null
    private var currentTimeDate: Date = calendar.time
    private var closeTimeDate: Date = Date()
    private var openTimeDate: Date = Date()
    private var nextOpenTime: String? = null

     var mallTimeStatu: MallTimeStatu = MallTimeStatu.close

    init {
        if (schedule?.monday != null && schedule.thu_fri != null && schedule.weekends != null) {
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            when (dayOfWeek) {

                Calendar.MONDAY -> {
                    scheduleData = schedule.monday
                    setNextOpenTime(schedule.thu_fri!!, context.getString(R.string.Tuesday))
                }
                Calendar.TUESDAY -> {
                    scheduleData = schedule.thu_fri
                    setNextOpenTime(schedule.thu_fri!!, context.getString(R.string.Wednesday))
                }
                Calendar.WEDNESDAY -> {
                    scheduleData = schedule.thu_fri
                    setNextOpenTime(schedule.thu_fri!!, context.getString(R.string.Thursday))
                }
                Calendar.THURSDAY -> {
                    scheduleData = schedule.thu_fri
                    setNextOpenTime(schedule.thu_fri!!, context.getString(R.string.Friday))
                }
                Calendar.FRIDAY -> {
                    scheduleData = schedule.thu_fri
                    setNextOpenTime(schedule.weekends!!, context.getString(R.string.Saturday))
                }
                Calendar.SATURDAY -> {
                    scheduleData = schedule.weekends
                    setNextOpenTime(schedule.weekends!!, context.getString(R.string.Sunday))
                }
                Calendar.SUNDAY -> {
                    scheduleData = schedule.weekends
                    setNextOpenTime(schedule.monday!!, context.getString(R.string.Monday))
                }
            }
        }
    }

    private fun setNextOpenTime(schedule: String, dayOfWeek: String) {
        val dateArray = schedule.split("-")

        if (dateArray.isNotEmpty()) {
            nextOpenTime = dateArray[0] + " " + dayOfWeek
        }
    }


    private fun parseCurrentDateInfo() {
        var openTime: String? = null
        var closeTime: String? = null

        val dateArray = scheduleData?.split("-")

        if (dateArray != null) {
            if (dateArray.isNotEmpty()) {
                openTime = dateArray[0]
                closeTime = dateArray[dateArray.size - 1]
            }

            closeTimeDate.time = parser.parse(closeTime).time
            openTimeDate.time = parser.parse(openTime).time


            mallTimeStatu = when {
                currentTimeDate.after(closeTimeDate) -> MallTimeStatu.open
                willBeCloseLessThanHour() -> MallTimeStatu.cloosing_soon
                else -> MallTimeStatu.close
            }

        }

    }

    fun getOpenCloseTimeInfo(): String {
        if (scheduleData == null || schedule == null ) {
            return ""
        }
        parseCurrentDateInfo()

        when (mallTimeStatu) {
            MallTimeStatu.open -> {
                val closeTime = parserStatuTitle.format(closeTimeDate).toString()
                return context.getString(R.string.open_time, closeTime)
            }
            MallTimeStatu.close -> {
                return context.getString(R.string.close_time, nextOpenTime)
            }
            MallTimeStatu.cloosing_soon -> {
                val closeTime = parserStatuTitle.format(closeTimeDate).toString()
                return context.getString(R.string.closing_soon_time, closeTime)
            }
        }

    }
    fun setOpenCloseTimeStatu(textView: TextView) {
        if (scheduleData == null || schedule == null ) {
            return
        }

        when (mallTimeStatu) {
            MallTimeStatu.open -> {
                textView.text = context.getString(R.string.open)
                textView.setTextColor( ContextCompat.getColor(context, R.color.store_open_color))
                textView.setBackground(ContextCompat.getDrawable(context,R.drawable.shape_open_store_backround))
            }
            MallTimeStatu.close -> {
                textView.text = context.getString(R.string.close)
                textView.setTextColor( ContextCompat.getColor(context, R.color.store_close_color))
                textView.setBackground(ContextCompat.getDrawable(context,R.drawable.shape_close_store_backround))
            }
            MallTimeStatu.cloosing_soon -> {
                textView.text = context.getString(R.string.closing_soon)
                textView.setTextColor( ContextCompat.getColor(context, R.color.store_close_soon_color))
                textView.setBackground(ContextCompat.getDrawable(context,R.drawable.shape_close_soon_store_backround))
            }
        }

    }

    private fun willBeCloseLessThanHour(): Boolean {
        if ((closeTimeDate.time - currentTimeDate.time) < 3600000) {
            return true
        }
        return false
    }

}

enum class MallTimeStatu {
    close,
    open,
    cloosing_soon
}
