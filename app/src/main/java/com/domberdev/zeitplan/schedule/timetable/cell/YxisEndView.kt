package com.domberdev.zeitplan.schedule.timetable.cell

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.domberdev.zeitplan.R
import kotlinx.android.synthetic.main.y_xis_end.view.*

@SuppressLint("ViewConstructor")
class YxisEndView(
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
    ){
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.y_xis_end, this, true)

        leftMenuEndItem.layoutParams = LayoutParams(width, height)
        yXisEnd.text = text
        if(menuColor != 0)
            yXisEnd.setBackgroundColor(menuColor)
    }
}