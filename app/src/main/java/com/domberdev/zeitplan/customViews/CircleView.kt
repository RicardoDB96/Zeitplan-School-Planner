package com.domberdev.zeitplan.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.domberdev.zeitplan.R
import de.hdodenhof.circleimageview.CircleImageView

class CircleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val circleView: CircleImageView
    private val textColorView: TextView

    init {

        val view = LayoutInflater.from(context).inflate(R.layout.circle_view, this, true)

        circleView = view.findViewById(R.id.circleColor)
        textColorView = view.findViewById(R.id.circleText)
    }

    fun setCircle(circleText: CircleText){
        textColorView.text = circleText.text
        circleView.circleBackgroundColor = circleText.color
        textColorView.setTextColor(circleText.textColor)
    }
}