package com.domberdev.zeitplan.schedule.timetable.tableinterface

import com.domberdev.zeitplan.schedule.timetable.model.ScheduleEntity

interface OnScheduleClickListener {
    fun scheduleClicked(entity: ScheduleEntity)
}