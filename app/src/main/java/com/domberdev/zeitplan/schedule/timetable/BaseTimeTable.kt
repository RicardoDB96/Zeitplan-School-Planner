package com.domberdev.zeitplan.schedule.timetable

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.schedule.timetable.cell.*
import com.domberdev.zeitplan.schedule.timetable.model.ScheduleEntity
import com.domberdev.zeitplan.schedule.timetable.tableinterface.*
import com.domberdev.zeitplan.schedule.timetable.utils.*
import kotlinx.android.synthetic.main.timetable.view.*
import kotlin.math.roundToInt

open class BaseTimeTable : LinearLayout {

    protected var topMenuHeight: Int = 30
    protected var leftMenuWidth: Int = 30
    protected var cellHeight: Int = 70

    protected var isRatio: Boolean = false
    protected var cellRatio: Float = 0f

    protected var tableContext: Context = context

    protected var topMenuHeightPx: Float = 0.0f
    protected var leftMenuWidthPx: Float = 0.0f
    protected var cellHeightPx: Float = 0.0f
    protected var averageWidth: Int = 0
    protected var widthPaddingPx: Float = 0.0f

    protected var tableStartTime: Int = 0
    protected var tableEndTime: Int = 24

    protected var dayList: Array<String> = resources.getStringArray(R.array.day)

    protected var radiusStyle: Int = 0
    protected var isTwentyFourHourClock = true
    protected var cellColor = 0
    protected var menuColor = 0
    protected var lineColor = 0
    protected var menuTextColor = 0
    protected var menuTextSize = 0f

    protected var isFullScreen = false
    protected var widthPadding = 0

    protected var scheduleClickListener: OnScheduleClickListener? = null
    protected var timeCellClickListener: OnTimeCellClickListener? = null
    protected var scheduleLongClickListener: OnScheduleLongClickListener? = null

    protected var border: Boolean = false
    protected var xEndLine: Boolean = false
    protected var yEndLine: Boolean = false

    protected var schedules: MutableList<ScheduleEntity> = mutableListOf()

    constructor(context: Context) : super(context){
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs)
    }



    @SuppressLint("Recycle", "CustomViewStyleable")
    private fun initView(context: Context, attrs: AttributeSet?) {
        tableContext = context
        val inflater = LayoutInflater.from(tableContext)
        val v = inflater.inflate(R.layout.timetable, this, false)
        addView(v)


        if (attrs == null) {
            return
        }

        val array = tableContext.obtainStyledAttributes(attrs, R.styleable.TimeTableView)

        radiusStyle = array.getInt(R.styleable.TimeTableView_radiusOption, 0)
        isTwentyFourHourClock = array.getBoolean(R.styleable.TimeTableView_isTwentyFourHourClock, true)
        cellColor = array.getColor(R.styleable.TimeTableView_cellColor, 0)
        menuColor = array.getColor(R.styleable.TimeTableView_menuColor, 0)
        lineColor = array.getColor(R.styleable.TimeTableView_lineColor,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                resources.getColor(R.color.default_line, null)
            else
                ContextCompat.getColor(context,R.color.default_line))

        border = array.getBoolean(R.styleable.TimeTableView_border, false)
        xEndLine = array.getBoolean(R.styleable.TimeTableView_xEndLine, false)
        yEndLine = array.getBoolean(R.styleable.TimeTableView_yEndLine, false)

        if(lineColor != 0) {
            val mainTable = findViewById<FrameLayout>(R.id.mainTable)
            val topMenu = findViewById<LinearLayout>(R.id.topMenu)
            val leftMenu = findViewById<LinearLayout>(R.id.leftMenu)

            mainTable.setBackgroundColor(lineColor)
            topMenu.setBackgroundColor(lineColor)
            leftMenu.setBackgroundColor(lineColor)
        }

        isFullScreen = array.getBoolean(R.styleable.TimeTableView_isFullWidth, false)
        widthPadding = array.getInteger(R.styleable.TimeTableView_widthPadding, 0)

        menuTextColor = array.getColor(R.styleable.TimeTableView_menuTextColor, 0)
        menuTextSize = array.getFloat(R.styleable.TimeTableView_menuTextSize, 0f)

        array.recycle()
    }

    protected fun calculateTime (schedules: MutableList<ScheduleEntity>) {
        tableStartTime =
            getHour(schedules[0].startTime)
        tableEndTime = 24
        schedules.map {entity ->
            if(getHour(entity.startTime) < tableStartTime)
                tableStartTime =
                    getHour(entity.startTime)
            if(getHour(entity.endTime) >= tableEndTime)
                tableEndTime = getHour(entity.endTime) + 1
        }
        if ((tableEndTime - tableStartTime) < 7) {
            tableEndTime = tableStartTime + 7
        }
    }

    protected fun recycleTimeCell () {
        for(i in 0 until (tableEndTime - tableStartTime)) {
            dayList.forEach { day -> val j: Int = dayList.indexOf(day)
                mainTable.addView(
                    TableCellView(
                        tableContext,
                        cellHeightPx.roundToInt(),
                        averageWidth,
                        (j * averageWidth),
                        (i * cellHeightPx.roundToInt()),
                        cellColor,
                        timeCellClickListener,
                        j,
                        (tableStartTime + i)
                    )
                )
            }
            val hour = if (isTwentyFourHourClock) (tableStartTime + i)
            else {
                if ((tableStartTime + i)!=12) (tableStartTime + i) % 12
                else (tableStartTime + i)
            }
            if (yEndLine) {
                timeCell.addView(
                    YxisView(
                        tableContext,
                        cellHeightPx.roundToInt(),
                        leftMenuWidthPx.roundToInt(),
                        hour.toString(),
                        menuColor,
                        menuTextColor,
                        menuTextSize
                    )
                )
            }
            else {
                if (i == (tableEndTime - tableStartTime) - 1) timeCell.addView(
                    YxisEndView(
                        tableContext,
                        cellHeightPx.roundToInt(),
                        leftMenuWidthPx.roundToInt(),
                        hour.toString(),
                        menuColor
                    )
                )
                else timeCell.addView(
                    YxisView(
                        tableContext,
                        cellHeightPx.roundToInt(),
                        leftMenuWidthPx.roundToInt(),
                        hour.toString(),
                        menuColor,
                        menuTextColor,
                        dpToPx(tableContext, menuTextSize)
                    )
                )
            }

        }
    }

}