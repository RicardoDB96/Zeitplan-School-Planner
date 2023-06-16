package com.domberdev.zeitplan.grades.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.domberdev.zeitplan.grades.model.Grade
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source

class Repo {

    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email!!

    fun getGradeData(subjectID: String): LiveData<MutableList<Grade>>{
        val mutableData = MutableLiveData<MutableList<Grade>>()
        val listData = mutableListOf<Grade>()

        db.disableNetwork().addOnCompleteListener {
            FirebaseFirestore.getInstance().collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                val phase = user.getString("Phase")!!
                val period = user.getString("Period")!!
                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects/$subjectID/Grades").orderBy("gradeDay", Query.Direction.DESCENDING).limit(5).get(Source.CACHE).addOnSuccessListener { subjectGrade ->
                    for (grade in subjectGrade){
                        val gradeTitle = grade.getString("gradeTitle")!!
                        val gradeV = grade.getString("grade")!!.toFloat()
                        val gradeDay = grade.getString("gradeDay")!!
                        val gradeWeight = grade.getString("gradeWeight")!!.toInt()
                        val gradeID = grade.getString("gradeID")!!
                        val subjectIDV = grade.getString("subjectID")!!
                        val gradeData = Grade(gradeTitle, gradeV, gradeDay, gradeWeight, gradeID, subjectIDV)
                        listData.add(gradeData)
                    }
                    mutableData.value = listData
                }
            }
        }
        return mutableData
    }

    fun getGradesData(subjectID: String): LiveData<MutableList<Grade>>{
        val mutableData = MutableLiveData<MutableList<Grade>>()
        val listData = mutableListOf<Grade>()
        db.disableNetwork().addOnCompleteListener {
            FirebaseFirestore.getInstance().collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                val phase = user.getString("Phase")!!
                val period = user.getString("Period")!!
                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects/$subjectID/Grades").orderBy("gradeDay", Query.Direction.DESCENDING).get(Source.CACHE).addOnSuccessListener { subjectGrade ->
                    for (grade in subjectGrade){
                        val gradeTitle = grade.getString("gradeTitle")!!
                        val gradeV = grade.getString("grade")!!.toFloat()
                        val gradeDay = grade.getString("gradeDay")!!
                        val gradeWeight = grade.getString("gradeWeight")!!.toInt()
                        val gradeID = grade.getString("gradeID")!!
                        val subjectIDV = grade.getString("subjectID")!!
                        val gradeData = Grade(gradeTitle, gradeV, gradeDay, gradeWeight, gradeID, subjectIDV)
                        listData.add(gradeData)
                    }
                    mutableData.value = listData
                }
            }
        }
        return mutableData
    }
}