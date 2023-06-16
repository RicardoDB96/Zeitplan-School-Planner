package com.domberdev.zeitplan.subjects.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.agenda.model.Task
import kotlinx.android.synthetic.main.subject_info_task_item_row.view.*

class SubjectInfoTasksAdapter(private val context: Context, private val onItemClickListener: OnSubjectInfoTaskListener): RecyclerView.Adapter<SubjectInfoTasksAdapter.SubjectInfoTasksViewModel>() {

    private var dataList = mutableListOf<Task>()

    interface OnSubjectInfoTaskListener{
        fun onSubjectInfoTaskClick(taskID: String, subjectID: String)
    }

    fun setListData(data: MutableList<Task>){
        dataList = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectInfoTasksViewModel {
        val view = LayoutInflater.from(context).inflate(R.layout.subject_info_task_item_row, parent, false)
        return SubjectInfoTasksViewModel(view)
    }

    override fun onBindViewHolder(holder: SubjectInfoTasksViewModel, position: Int) {
        val tasks = dataList[position]
        holder.bindView(tasks)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class SubjectInfoTasksViewModel(itemView: View): RecyclerView.ViewHolder(itemView){
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bindView(task: Task){
            itemView.subjectInfoTaskTitle.text = task.taskTitle
            itemView.subjectInfoTaskCategory.text = when(task.category){
                "0" -> context.getString(R.string.reminder)
                "1" -> context.getString(R.string.Homework)
                "2" -> context.getString(R.string.Teamwork)
                "3" -> context.getString(R.string.Project)
                "4" -> context.getString(R.string.QuickTest)
                "5" -> context.getString(R.string.PartialExam)
                "6" -> context.getString(R.string.Exam)
                else -> task.category
            }

            val circleCheck = context.resources.getDrawable(R.drawable.ic_circle_check, context.theme)
            val circleUnchecked = context.resources.getDrawable(R.drawable.ic_circle_unchecked, context.theme)

            circleCheck.setTint(task.subjectColor)
            if (task.finished){
                itemView.subjectInfoTaskTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                itemView.subjectInfoFinishedCheck.setImageDrawable(circleCheck)
            }else{
                itemView.subjectInfoTaskTitle.paintFlags = 0
                itemView.subjectInfoFinishedCheck.setImageDrawable(circleUnchecked)
            }
            itemView.setOnClickListener { onItemClickListener.onSubjectInfoTaskClick(task.taskID, task.subjectID) }
        }
    }
}