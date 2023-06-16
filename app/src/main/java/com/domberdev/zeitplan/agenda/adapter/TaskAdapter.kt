package com.domberdev.zeitplan.agenda.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.agenda.model.Task
import kotlinx.android.synthetic.main.agenda_item_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(private val context: Context, private val onItemClickListener: OnTaskClickListener): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var dataList = mutableListOf<Task>()

    fun setListData(data:MutableList<Task>){
        dataList = data
    }

    interface OnTaskClickListener{
        fun onTaskClick(taskID: String, subjectID: String)
        fun onTaskLongClick(finished: Boolean, taskID: String, subjectID: String)
        fun onSettingsClick(finished: Boolean, taskID: String, subjectID: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.agenda_item_row, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = dataList[position]
        holder.bindView(task)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class TaskViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        @SuppressLint("UseCompatLoadingForDrawables", "SimpleDateFormat", "SetTextI18n")
        fun bindView(task: Task){

            itemView.taskSubjectCircleColor.circleBackgroundColor = task.subjectColor
            if (task.subjectTitle?.length!! >= 22){
                itemView.subjects.text = task.subjectAcronym
            }else{
                itemView.subjects.text = task.subjectTitle
            }

            if (task.finished){
                itemView.taskTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                try {
                    val finishDate = SimpleDateFormat("yyyy-MM-dd").parse(task.finishedOn)!!
                    val finishDay = SimpleDateFormat("EEEE, MMMM d, yyyy").format(finishDate).capitalizeWords()
                    itemView.deadline.text = "${context.getString(R.string.finishOn)} $finishDay"
                }catch (e: Exception){
                    itemView.deadline.text = task.finishedOn
                }
                val drawable = context.resources.getDrawable(R.drawable.ic_check_small, context.theme)
                itemView.deadline.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
            }else{
                itemView.taskTitle.paintFlags = 0
                val date = SimpleDateFormat("yyyy-MM-dd").parse(task.day)
                val day = SimpleDateFormat("EEEE, MMMM d, yyyy").format(date!!)
                itemView.deadline.text = day.capitalizeWords()
                itemView.deadline.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
            }
            itemView.setOnClickListener { onItemClickListener.onTaskClick(task.taskID, task.subjectID) }
            itemView.setOnLongClickListener { onItemClickListener.onTaskLongClick(task.finished, task.taskID, task.subjectID);true }
            itemView.settingsTasksButton.setOnClickListener { onItemClickListener.onSettingsClick(task.finished, task.taskID, task.subjectID) }
            itemView.taskTitle.text = task.taskTitle
            itemView.category.text = when(task.category){
                "0" -> context.getString(R.string.reminder)
                "1" -> context.getString(R.string.Homework)
                "2" -> context.getString(R.string.Teamwork)
                "3" -> context.getString(R.string.Project)
                "4" -> context.getString(R.string.QuickTest)
                "5" -> context.getString(R.string.PartialExam)
                "6" -> context.getString(R.string.Exam)
                else -> task.category
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