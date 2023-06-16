package com.domberdev.zeitplan.subjects.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.subjects.model.Subject
import kotlinx.android.synthetic.main.subject_item_row.view.*
import kotlin.math.roundToInt

class SubjectAdapter(private val context: Context, private val itemClickListener: OnSubjectClickListener): RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    private var dataList = mutableListOf<Subject>()

    interface OnSubjectClickListener{
        fun onSubjectClick(subjectID: String)
        fun onSubjectLongClick(subjectID: String, generalGrade: Boolean)
        fun onSettingsClick(subjectID: String, generalGrade: Boolean)
    }

    fun setListData(data: MutableList<Subject>){
        dataList = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.subject_item_row, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = dataList[position]
        holder.bindView(subject)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    private val prefs = context.getSharedPreferences("userdata", Context.MODE_PRIVATE).getBoolean("roundGrades", false)

    inner class SubjectViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindView(subject: Subject){
            itemView.setOnClickListener { itemClickListener.onSubjectClick(subject.subjectID) }
            itemView.setOnLongClickListener { itemClickListener.onSubjectLongClick(subject.subjectID, subject.generalGrade); true }
            itemView.settingButton.setOnClickListener { itemClickListener.onSettingsClick(subject.subjectID, subject.generalGrade)}
            itemView.subjectTitle.text = subject.subjectTitle
            itemView.subjectTitle.setTextColor(subject.color.toInt())
            itemView.teacher.text = subject.teacher
            itemView.place.text = when(subject.place){
                "6593632304" -> context.getString(R.string.online)
                else -> subject.place.toString()
            }
            itemView.objective.text = subject.goal
            itemView.colorLayout.setBackgroundColor(subject.secondaryColor.toInt())

            val circularProgressBar = itemView.gradeCircularBar
            circularProgressBar.apply {
                // Set Progress
                val gradeChart = subject.grade.replace(',', '.')

                val grade = if (prefs){
                    "${gradeChart.toFloat().roundToInt()}/100"
                }else{
                    "${subject.grade}/100"
                }
                itemView.grade.text = grade
                // or with animation
                val gradeAnim = if (prefs){
                    gradeChart.toFloat().roundToInt().toFloat()
                }else{
                    gradeChart.toFloat()
                }
                setProgressWithAnimation(gradeAnim, 1000) // =1s
                // Set Progress Max
                progressMax = 100f
                // Set ProgressBar Color
                progressBarColor = subject.color.toInt()
                // Set Width
                progressBarWidth = 10f // in DP
                backgroundProgressBarWidth = 5f // in DP
            }
        }
    }
}