package com.domberdev.zeitplan.agenda.network

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.domberdev.zeitplan.agenda.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import java.text.SimpleDateFormat
import java.util.*

class Repo {

    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email

    private val userID = FirebaseAuth.getInstance().uid!!

    var subjectID = String()

    fun getTaskData(context: Context):LiveData<MutableList<Task>>{
        val prefs = context.getSharedPreferences("userdata", Context.MODE_PRIVATE)?.getBoolean("showTasks", false)
        val mutableData = MutableLiveData<MutableList<Task>>()
        val listData = mutableListOf<Task>()
        db.waitForPendingWrites().addOnSuccessListener {
            db.collection("Users").document(userEmail!!).get(Source.CACHE).addOnSuccessListener { user ->
                val phase = user.getString("Phase")!!
                val period = user.getString("Period")!!
                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").get(Source.CACHE).addOnSuccessListener { result ->
                    for (document in result){
                        subjectID = document.getString("subjectID")!!
                        val subjectTitle = document.getString("subjectTitle")!!
                        val subjectAcronym = document.getString("subjectAcronym")!!
                        val subjectColor = document.getString("color")!!.toInt()
                        val subjectSecondaryColor = document.getString("secondaryColor")!!.toInt()
                        if (prefs == false){
                            db.collection("Tasks/$subjectID/tasks").whereEqualTo(userID, false).get(Source.CACHE).addOnSuccessListener { result1 ->
                                for (document1 in result1){
                                    val taskTitle = document1.getString("taskTitle")
                                    val timeLimit = document1.getString("timeLimit")
                                    val finishedOn = document1.getString("${userID}finishedOn")
                                    val day = document1.getString("day")
                                    val category = document1.getString("category")
                                    val taskID = document1.getString("taskID")
                                    val subjectID = document1.getString("subjectID")
                                    val description = document1.getString("description")
                                    val finished = document1.getBoolean(userID)
                                    val task = Task(taskTitle!!, timeLimit!!, finishedOn!!, day!!,category!!, taskID!!, subjectID!!, description!!, finished!!, subjectTitle, subjectAcronym, subjectColor, subjectSecondaryColor)
                                    listData.add(task)
                                }
                                listData.sortBy { it.day }
                                mutableData.value = listData
                            }
                        }else if(prefs == true){
                            db.collection("Tasks/$subjectID/tasks").get(Source.CACHE).addOnSuccessListener { result1 ->
                                for (document1 in result1){
                                    val taskTitle = document1.getString("taskTitle")
                                    val timeLimit = document1.getString("timeLimit")
                                    val finishedOn = document1.getString("${userID}finishedOn")
                                    val day = document1.getString("day")
                                    val category = document1.getString("category")
                                    val taskID = document1.getString("taskID")
                                    val subjectID = document1.getString("subjectID")
                                    val description = document1.getString("description")
                                    val finished = document1.getBoolean(userID)
                                    val task = Task(taskTitle!!, timeLimit!!, finishedOn!!, day!!, category!!, taskID!!, subjectID!!, description!!, finished!!, subjectTitle, subjectAcronym, subjectColor, subjectSecondaryColor)
                                    listData.add(task)
                                }
                                listData.sortBy { it.day }
                                mutableData.value = listData
                            }
                        }
                    }
                }
            }
        }
        return mutableData
    }

    @SuppressLint("SimpleDateFormat")
    fun getUpcomingEvents(source: Source):LiveData<MutableList<Task>>{
        val c = Calendar.getInstance()
        c.add(Calendar.DATE, 7)
        val dayF = SimpleDateFormat("yyyy-MM-dd").format(c.time)

        val mutableData = MutableLiveData<MutableList<Task>>()
        val listData = mutableListOf<Task>()
        if (source == Source.SERVER){
            db.collection("Users").document(userEmail!!).get(Source.CACHE).addOnSuccessListener { user ->
                val phase = user.getString("Phase")!!
                val period = user.getString("Period")!!
                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").get(Source.SERVER).addOnSuccessListener { result ->
                    for (document in result){
                        subjectID = document.getString("subjectID")!!
                        val subjectAcronym = document.getString("subjectAcronym")!!
                        db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects/$subjectID/Grades").get(Source.SERVER).addOnSuccessListener { grades ->
                            for (grade in grades){
                                grade.getString("grade")
                                grade.getString("gradeTitle")
                                grade.getString("gradeDay")
                                grade.getString("gradeID")
                                grade.getString("gradeWeight")
                                grade.getString("subjectID")
                            }
                        }
                        db.collection("Tasks/$subjectID/tasks").get(Source.SERVER).addOnSuccessListener { result1 ->
                            for (document1 in result1){
                                val taskTitle = document1.getString("taskTitle")
                                document1.getString("timeLimit")
                                document1.getString("${userID}finishedOn")
                                val day = document1.getString("day")
                                document1.getString("category")
                                val taskID = document1.getString("taskID")
                                val subjectID = document1.getString("subjectID")
                                document1.getString("description")
                                val finished = document1.getBoolean(userID)
                                val task = Task(taskTitle = taskTitle!!, day = day!!, taskID = taskID!!, subjectID = subjectID!!, finished = finished!!, subjectAcronym = subjectAcronym)
                                if (!task.finished){
                                    listData.add(task)
                                }
                            }
                            listData.sortBy { it.day }
                            mutableData.value = listData
                        }
                    }
                }
            }
        }else{
            db.collection("Users").document(userEmail!!).get(Source.CACHE).addOnSuccessListener { user ->
                val phase = user.getString("Phase")!!
                val period = user.getString("Period")!!
                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").get(Source.CACHE).addOnSuccessListener { result ->
                    for (document in result){
                        subjectID = document.getString("subjectID")!!
                        val subjectAcronym = document.getString("subjectAcronym")!!
                        db.collection("Tasks/$subjectID/tasks").whereLessThanOrEqualTo("day", dayF).get(Source.CACHE).addOnSuccessListener { result1 ->
                            for (document1 in result1){
                                val taskTitle = document1.getString("taskTitle")
                                val day = document1.getString("day")
                                val taskID = document1.getString("taskID")
                                val subjectID = document1.getString("subjectID")
                                val finished = document1.getBoolean(userID)
                                val task = Task(taskTitle = taskTitle!!, day = day!!, taskID = taskID!!, subjectID = subjectID!!, finished = finished!!, subjectAcronym = subjectAcronym)
                                if (!task.finished){
                                    listData.add(task)
                                }
                            }
                            listData.sortBy { it.day }
                            mutableData.value = listData
                        }
                    }
                }
            }
        }
        return mutableData
    }

    @SuppressLint("SimpleDateFormat")
    fun getUpcomingSubjectInfoTasks(subject: String):LiveData<MutableList<Task>>{
        val c = Calendar.getInstance()
        val dayF = SimpleDateFormat("yyyy-MM-dd").format(c.time)

        val mutableData = MutableLiveData<MutableList<Task>>()
        val listData = mutableListOf<Task>()
        db.collection("Users").document(userEmail!!).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subject).get(Source.CACHE).addOnSuccessListener { result ->
                val color = result.getString("color")!!.toInt()
                db.collection("Tasks/$subject/tasks").whereGreaterThanOrEqualTo("day", dayF).get(Source.CACHE).addOnSuccessListener { subjectTasks ->
                    for (tasks in subjectTasks){
                        val taskTitle = tasks.getString("taskTitle")
                        val category = tasks.getString("category")
                        val taskID = tasks.getString("taskID")
                        val subjectID = tasks.getString("subjectID")
                        val finished = tasks.getBoolean(userID)
                        val task = Task(taskTitle!!, category = category!!, taskID = taskID!!, subjectID = subjectID!!, finished = finished!!, subjectColor = color)
                        listData.add(task)
                    }
                    listData.sortBy { it.day }
                    mutableData.value = listData
                }
            }
        }
        return mutableData
    }
}