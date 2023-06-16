package com.domberdev.zeitplan.agenda.network

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.domberdev.zeitplan.agenda.model.Task
import com.google.firebase.firestore.Source

open class TaskViewModel: ViewModel() {

    private val repo = Repo()
    fun fetchTaskData(context: Context):LiveData<MutableList<Task>>{
        val mutableData = MutableLiveData<MutableList<Task>>()
        repo.getTaskData(context).observeForever{taskList ->
            mutableData.value = taskList
        }
        return mutableData
    }

    fun fetchUpcomingEvents(source: Source):LiveData<MutableList<Task>>{
        val mutableData = MutableLiveData<MutableList<Task>>()
        repo.getUpcomingEvents(source).observeForever{eventsList ->
            mutableData.value = eventsList
        }
        return mutableData
    }

    fun fetchUpcomingSubjectInfoTasks(subject: String):LiveData<MutableList<Task>>{
        val mutableData = MutableLiveData<MutableList<Task>>()
        repo.getUpcomingSubjectInfoTasks(subject).observeForever{subjectInfoTasks ->
            mutableData.value = subjectInfoTasks
        }
        return mutableData
    }
}