package com.domberdev.zeitplan.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.agenda.model.Task
import kotlinx.android.synthetic.main.upcoming_events_item_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class UpcomingEventsAdapter(private val context: Context, private val onItemClickListener: OnUpcomingEventClickListener): RecyclerView.Adapter<UpcomingEventsAdapter.UpcomingEventsViewHolder>() {

    private var dataList = mutableListOf<Task>()

    interface OnUpcomingEventClickListener{
        fun onUpcomingEventClick(taskID: String, subjectID: String)
    }

    fun setListData(data: MutableList<Task>){
        dataList = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingEventsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.upcoming_events_item_row, parent, false)
        return UpcomingEventsViewHolder(view)
    }

    override fun onBindViewHolder(holder: UpcomingEventsViewHolder, position: Int) {
        val events = dataList[position]
        holder.bindView(events)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class UpcomingEventsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        @SuppressLint("SimpleDateFormat", "UseCompatLoadingForDrawables")
        fun bindView(task: Task){
            itemView.upcomingEventTitle.text = task.taskTitle
            val date = SimpleDateFormat("yyyy-MM-dd").parse(task.day)
            val day = SimpleDateFormat("EEEE, MMMM d, yyyy").format(date!!)
            itemView.upcomingEventDate.text = day.capitalizeWords()
            itemView.upcomingEventSubject.text = task.subjectAcronym
            itemView.setOnClickListener { onItemClickListener.onUpcomingEventClick(task.taskID, task.subjectID) }

            val c = Calendar.getInstance()
            val dayF = SimpleDateFormat("yyyy-MM-dd").format(c.time)
            val drawableError = context.resources?.getDrawable(R.drawable.ic_timer_error, context.theme)
            val drawableWarning = context.resources?.getDrawable(R.drawable.ic_timer_warning, context.theme)

            when {
                task.day < dayF -> {
                    itemView.upcomingEventSubject.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableError, null, null, null)
                }
                task.day == dayF -> {
                    itemView.upcomingEventSubject.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableWarning, null, null, null)
                }
                else -> {
                    itemView.upcomingEventSubject.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
                }
            }
        }
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it ->
        it.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
    }
}