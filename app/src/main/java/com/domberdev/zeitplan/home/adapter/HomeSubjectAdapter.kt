package com.domberdev.zeitplan.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.subjects.model.Subject
import kotlinx.android.synthetic.main.subject_home_item_row.view.*
import kotlin.math.roundToInt

class HomeSubjectAdapter(private val context: Context, private val itemClickListener: OnSubjectClickListener): RecyclerView.Adapter<HomeSubjectAdapter.SubjectHomeViewHolder>() {

    private var dataList = mutableListOf<Subject>()

    interface OnSubjectClickListener{
        fun onSubjectClick(subjectID: String)
        fun onSubjectLongClick(subjectID: String, generalGrade: Boolean)
        fun onSettingsClick(subjectID: String, generalGrade: Boolean)
    }

    fun setListData(data: MutableList<Subject>){
        dataList = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectHomeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.subject_home_item_row, parent, false)
        return SubjectHomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectHomeViewHolder, position: Int) {
        val subject = dataList[position]
        holder.bindView(subject)
    }
    override fun getItemCount(): Int {
        return dataList.size
    }

    private val prefs = context.getSharedPreferences("userdata", Context.MODE_PRIVATE).getBoolean("roundGrades", false)

    inner class SubjectHomeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        @SuppressLint("SetTextI18n")
        fun bindView(subject: Subject){
            itemView.subjectHomeTitle.text = subject.subjectTitle
            itemView.subjectHomeTitle.setTextColor(subject.color.toInt())
            itemView.gradeHome.text = subject.grade
            itemView.setOnClickListener { itemClickListener.onSubjectClick(subject.subjectID) }
            itemView.setOnLongClickListener { itemClickListener.onSubjectLongClick(subject.subjectID, subject.generalGrade);true }
            itemView.settingSubjectHomeButton.setOnClickListener { itemClickListener.onSettingsClick(subject.subjectID, subject.generalGrade)}
            itemView.colorHomeLayout.setBackgroundColor(subject.secondaryColor.toInt())

            val circularProgressBar = itemView.circularSubjectHomeProgressBar
            circularProgressBar.apply {
                // Set Progress
                val gradeChart = subject.grade.replace(',', '.')

                val grade = if (prefs){
                    "${gradeChart.toFloat().roundToInt()}/100"
                }else{
                    "${subject.grade}/100"
                }
                itemView.gradeHome.text = grade
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