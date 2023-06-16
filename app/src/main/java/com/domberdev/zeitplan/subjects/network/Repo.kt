package com.domberdev.zeitplan.subjects.network

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.domberdev.zeitplan.subjects.model.Subject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source

@Suppress("NAME_SHADOWING")
class Repo {

    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email!!

    @SuppressLint("ResourceType")
    fun getSubjectData(): LiveData<MutableList<Subject>> {
        val mutableData = MutableLiveData<MutableList<Subject>>()
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").orderBy("subjectTitle", Query.Direction.ASCENDING).get(Source.CACHE).addOnSuccessListener { result ->
                val listData = mutableListOf<Subject>()
                for (document in result){
                    val subjectTitle = document.getString("subjectTitle")
                    val subjectID = document.getString("subjectID")
                    val teacher = document.getString("teacher")
                    val place = document.getString("place")
                    val color = document.getString("color")
                    val secondaryColor = document.getString("secondaryColor")
                    val grade = document.getString("grade")
                    val goal = document.getString("goal")
                    val generalGrade = document.getBoolean("generalGrade")
                    val subject = Subject(subjectTitle!!, teacher = teacher!!,place = place!!, subjectID = subjectID!!, color = color!!,secondaryColor = secondaryColor!!, grade = grade!!, goal = goal!!, generalGrade = generalGrade!!)
                    listData.add(subject)
                }
                mutableData.value = listData
            }
        }
        return mutableData
    }

    fun getSubjectHomeData(context: Context): LiveData<MutableList<Subject>>{
        val mutableData = MutableLiveData<MutableList<Subject>>()
        val prefs = context.getSharedPreferences("userdata", Context.MODE_PRIVATE)?.getBoolean("getAllData", false)
        if (prefs == true){
            db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                val phase = user.getString("Phase")!!
                val period = user.getString("Period")!!
                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").orderBy("subjectTitle", Query.Direction.ASCENDING).get().addOnSuccessListener { result ->
                    val listData = mutableListOf<Subject>()
                    for (document in result){
                        val subjectTitle = document.getString("subjectTitle")
                        val subjectAcronym = document.getString("subjectAcronym")
                        val teacher = document.getString("teacher")
                        val place = document.getString("place")
                        val subjectID = document.getString("subjectID")
                        val color = document.getString("color")
                        val secondaryColor = document.getString("secondaryColor")
                        val grade = document.getString("grade")
                        val goal = document.getString("goal")
                        val generalGrade = document.getBoolean("generalGrade")
                        val subject = Subject(subjectTitle!!, subjectAcronym!!, teacher!!, place!!, subjectID!!, color!!, secondaryColor!!, grade!!, goal!!, generalGrade!!)
                        listData.add(subject)
                    }
                    mutableData.value = listData
                }
            }
        }else{
            db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                val phase = user.getString("Phase")!!
                val period = user.getString("Period")!!
                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").orderBy("subjectTitle", Query.Direction.ASCENDING).get(Source.CACHE).addOnSuccessListener { result ->
                    val listData = mutableListOf<Subject>()
                    for (document in result){
                        val subjectTitle = document.getString("subjectTitle")
                        val subjectID = document.getString("subjectID")
                        val color = document.getString("color")
                        val secondaryColor = document.getString("secondaryColor")
                        val grade = document.getString("grade")
                        val generalGrade = document.getBoolean("generalGrade")
                        val subject = Subject(subjectTitle!!, subjectID = subjectID!!, color = color!!,secondaryColor = secondaryColor!!, grade = grade!!, generalGrade = generalGrade!!)
                        listData.add(subject)
                    }
                    mutableData.value = listData
                }
            }
        }
        return mutableData
    }

    fun getSubjectGradeData(): LiveData<MutableList<Subject>>{
        val mutableData = MutableLiveData<MutableList<Subject>>()
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").get(Source.CACHE).addOnSuccessListener {
                for (document in it){
                    db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").whereEqualTo("generalGrade", true).orderBy("subjectTitle", Query.Direction.ASCENDING).get(Source.CACHE).addOnSuccessListener { result ->
                        val listData = mutableListOf<Subject>()
                        for (document in result){
                            val subjectTitle = document.getString("subjectTitle")
                            val subjectAcronym = document.getString("subjectAcronym")
                            val color = document.getString("color")
                            val grade = document.getString("grade")
                            val generalGrade = document.getBoolean("generalGrade")
                            val subject = Subject(subjectTitle!!, subjectAcronym!!, color = color!!, grade = grade!!, generalGrade = generalGrade!!)
                            listData.add(subject)
                        }
                        mutableData.value = listData
                    }
                }
            }
        }
        return mutableData
    }

    fun fetchSubjectSelectionData(): LiveData<MutableList<Subject>>{
        val mutableData = MutableLiveData<MutableList<Subject>>()
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").get(Source.CACHE).addOnSuccessListener {
                for (document in it){
                    db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").whereEqualTo("generalGrade", true).orderBy("subjectTitle", Query.Direction.ASCENDING).get(Source.CACHE).addOnSuccessListener { result ->
                        val listData = mutableListOf<Subject>()
                        for (document in result){
                            val subjectTitle = document.getString("subjectTitle")!!
                            val subjectColor = document.getString("color")!!
                            val subjectData = Subject(subjectTitle = subjectTitle, color = subjectColor)
                            listData.add(subjectData)
                        }
                        mutableData.value = listData
                    }
                }
            }
        }
        return mutableData
    }
}