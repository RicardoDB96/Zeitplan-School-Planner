package com.domberdev.zeitplan.profile.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.subjects.model.Subject
import kotlinx.android.synthetic.main.grade_subject_item_row.view.*
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

class SubjectsWithGradesAdapter(private val context: Context): RecyclerView.Adapter<SubjectsWithGradesAdapter.SubjectWithGradesViewHolder>() {

    private var dataList = mutableListOf<Subject>()
    val mutableList = mutableListOf<Float>()

    fun setListData(data: MutableList<Subject>){
        dataList = data
    }

    private val prefs = context.getSharedPreferences("userdata", Context.MODE_PRIVATE).getBoolean("roundGrades", false)

    fun setCurrentGrade(): String{
        for (grade in dataList){
            val gradeChart = grade.grade.replace(',', '.')
            if (prefs){
                mutableList.add(gradeChart.toFloat().roundToInt().toFloat())
            }else{
                mutableList.add(gradeChart.toFloat())
            }
        }
        val gradeComplete = mutableList.average()
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(gradeComplete)
    }

    fun setCurrentGradePure(): String{
        val mutableList = mutableListOf<Float>()
        for (grade in dataList){
            val gradeChart = grade.grade.replace(',', '.')
            mutableList.add(gradeChart.toFloat())
        }
        val gradeComplete = mutableList.average()
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(gradeComplete)
    }

    fun setCurrentGradeRound(): String{
        val mutableList = mutableListOf<Float>()
        for (grade in dataList){
            val gradeChart = grade.grade.replace(',', '.')
            mutableList.add(gradeChart.toFloat().roundToInt().toFloat())
        }
        val gradeComplete = mutableList.average()
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(gradeComplete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectWithGradesViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.grade_subject_item_row, parent, false)
        return SubjectWithGradesViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectWithGradesViewHolder, position: Int) {
        val subject = dataList[position]
        holder.bindView(subject)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class SubjectWithGradesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        @SuppressLint("SetTextI18n")
        fun bindView(subject: Subject){
            itemView.subjectCircleColor.circleBackgroundColor = subject.color.toInt()
            if (subject.subjectTitle.length >= 20){
                itemView.subjectTitleProfile.text = subject.subjectAcronym
            }else{
                itemView.subjectTitleProfile.text = subject.subjectTitle
            }
            val gradeChart = subject.grade.replace(',', '.')
            if (prefs){
                itemView.gradeRVProfile.text = "(${gradeChart.toFloat().roundToInt()})"
            }else{
                itemView.gradeRVProfile.text = "(${subject.grade})"
            }
        }
    }
}