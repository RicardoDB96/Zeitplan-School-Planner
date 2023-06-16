package com.domberdev.zeitplan.grades.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.grades.model.Grade
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.grade_item_row.view.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class SubjectInfoGradeAdapter(private val context: Context, private val onItemClickListener: OnGradeClickListener): RecyclerView.Adapter<SubjectInfoGradeAdapter.GradeViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email!!

    private var dataList = mutableListOf<Grade>()

    fun setListData(data:MutableList<Grade>){
        dataList = data
    }

    interface OnGradeClickListener{
        fun onGradeClick(gradeID: String, subjectID: String)
        fun onGradeLongClick(gradeID: String, subjectID: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.grade_item_row, parent, false)
        return GradeViewHolder(view)
    }

    override fun onBindViewHolder(holder: GradeViewHolder, position: Int) {
        val grade = dataList[position]
        holder.bindView(grade)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class GradeViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        @SuppressLint("SimpleDateFormat")
        fun bindView(grade: Grade){
            val gradeV = grade.grade / 100f

            val orange = 0xFFf57c00.toInt()

            val gradeF = DecimalFormat("#.##").format(grade.grade)

            itemView.gradeCircleColor.circleBackgroundColor = when{
                grade.grade >= 66f -> ColorUtils.blendARGB(Color.YELLOW, 0xFF2e7d32.toInt(), gradeV)
                grade.grade <= 33f -> ColorUtils.blendARGB(orange, 0xFFCCB100.toInt(), gradeV)
                else -> ColorUtils.blendARGB(0xFFCCB100.toInt(), Color.YELLOW, gradeV)
            }
            itemView.gradeValue.text = gradeF
            itemView.gradeTitle.text = grade.gradeTitle
            db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                val phase = user.getString("Phase")!!
                val period = user.getString("Period")!!
                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(grade.subjectID).get(Source.CACHE).addOnSuccessListener {
                    itemView.gradeSubject.text = it.getString("subjectTitle")
                }
            }
            val date = SimpleDateFormat("yyyy-MM-dd").parse(grade.gradeDay)
            val day = SimpleDateFormat("d MMM").format(date!!)
            itemView.gradeDay.text = day
            itemView.setOnClickListener { onItemClickListener.onGradeClick(grade.gradeID, grade.subjectID) }
            itemView.setOnLongClickListener { onItemClickListener.onGradeLongClick(grade.gradeID, grade.subjectID);true }
        }
    }
}