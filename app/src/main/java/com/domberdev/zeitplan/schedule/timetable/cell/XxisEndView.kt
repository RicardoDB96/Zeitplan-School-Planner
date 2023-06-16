package com.domberdev.zeitplan.schedule.timetable.cell

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.domberdev.zeitplan.R
import kotlinx.android.synthetic.main.x_xis_end.view.*

@SuppressLint("ViewConstructor")
class XxisEndView(
    context: Context,
    height: Int,
    width: Int,
    text: String,
    menuColor: Int
) : LinearLayout(context) {
    init {
        initView(context, height, width, text, menuColor)
    }

    private fun initView(
        context: Context,
        height: Int,
        width: Int,
        text: String,
        menuColor: Int
    ) {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.x_xis_end, this, true)

        topMenuEndItem.layoutParams = LayoutParams(width, height)
        xXisEnd.text = text
        if(menuColor != 0)
            xXisEnd.setBackgroundColor(menuColor)
    }
}