package com.domberdev.zeitplan.schedule.timetable.schedule

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.schedule.timetable.model.ScheduleEntity
import com.domberdev.zeitplan.schedule.timetable.tableinterface.*
import com.domberdev.zeitplan.schedule.timetable.utils.*
import kotlinx.android.synthetic.main.item_schedule.view.*

@SuppressLint("ViewConstructor")
class ScheduleView(context: Context,
                   entity: ScheduleEntity,
                   height: Int,
                   width: Int,
                   scheduleClickListener: OnScheduleClickListener?,
                   scheduleLongClickListener: OnScheduleLongClickListener?,
                   tableStartTime: Int,
                   radiusStyle: Int
) : LinearLayout(context) {
    init {
        setting(
            context,
            entity,
            height,
            width,
            scheduleClickListener,
            scheduleLongClickListener,
            tableStartTime,
            radiusStyle
        )
    }

    @SuppressLint("RtlHardcoded")
    private fun setting(context: Context,
                        entity: ScheduleEntity,
                        height: Int,
                        width: Int,
                        scheduleClickListener: OnScheduleClickListener?,
                        scheduleLongClickListener: OnScheduleLongClickListener?,
                        tableStartTime: Int,
                        radiusStyle: Int
    ) {

        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.item_schedule, this, true)

        val duration = getTotalMinute(entity.endTime) - getTotalMinute(
            entity.startTime
        )

        val layoutSetting = LayoutParams(width - 6, ((height * duration).toDouble() / 60 - 6).toInt())
        layoutSetting.topMargin = (((height * getTotalMinute(
            entity.startTime
        )).toDouble() / 60) - (height * tableStartTime) + 2).toInt()
        layoutSetting.leftMargin = width * entity.scheduleDay + 2

        tableItem.layoutParams = layoutSetting

        tableItem.setOnClickListener {
            scheduleClickListener?.scheduleClicked(entity)
            entity.mOnClickListener?.onClick(tableItem)
        }

        tableItem.setOnLongClickListener {
            scheduleLongClickListener?.scheduleLongClicked(entity)
            return@setOnLongClickListener true
        }


        val layoutText = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        val cornerRadius =
            dpToPx(context, RADIUS.toFloat())
        val roundRadius =
            dpToPx(context, ROUND.toFloat())

        val border = GradientDrawable()
        border.setColor(entity.backgroundColor)
        border.shape = GradientDrawable.RECTANGLE

        when (radiusStyle) {
            NONE -> {}
            LEFT -> {
                layoutText.leftMargin = (width.toDouble() * 0.15).toInt()
                tableItem.gravity = Gravity.RIGHT
                name.layoutParams = layoutText
                name.gravity = Gravity.RIGHT
                room.gravity = Gravity.RIGHT

                border.cornerRadii = floatArrayOf(cornerRadius, cornerRadius, 0f, 0f, cornerRadius, cornerRadius, 0f, 0f)
            }
            RIGHT -> {
                layoutText.rightMargin = (width.toDouble() * 0.15).toInt()
                name.layoutParams = layoutText

                border.cornerRadii = floatArrayOf(0f, 0f, cornerRadius, cornerRadius, 0f, 0f, cornerRadius, cornerRadius)
            }
            ALL -> {
                border.cornerRadius = roundRadius
            }
        }

        tableItem.background = border

        name.text = entity.scheduleName
        room.text = when(entity.roomInfo.toString()){
            R.string.online.toString() -> context.getString(R.string.online)
            else -> entity.roomInfo.toString()
        }
    }

    companion object {
        const val NONE = 0
        const val LEFT = 1
        const val RIGHT = 2
        const val ALL = 3
        private const val RADIUS = 30
        private const val ROUND = 4
    }
}
