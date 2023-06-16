package com.domberdev.zeitplan.schedule.timetable.tableinterface

interface OnTimeCellClickListener {
    fun timeCellClicked(scheduleDay: Int, time: Int)
}