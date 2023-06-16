package com.domberdev.zeitplan.schedule.timetable.model

import android.view.View

data class ScheduleEntity(
    var scheduleID: String,
    var scheduleName: String,
    var roomInfo: Any,
    var scheduleDay: Int,
    var startTime: String,
    var endTime: String,
    var backgroundColor: Int = 0xFFDDDDDD.toInt(),
    var subjectID: String,
    var startTime12: String,
    var endTime12: String,
) {

    var mOnClickListener: View.OnClickListener? = null

    fun setOnClickListener(listener: View.OnClickListener) {
        mOnClickListener = listener
    }

}
