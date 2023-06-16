package com.domberdev.zeitplan.grades.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.domberdev.zeitplan.grades.model.Grade

class GradeViewModel: ViewModel() {

    private val repo = Repo()

    fun fetchGradeData(subjectID: String): LiveData<MutableList<Grade>>{
        val mutableData = MutableLiveData <MutableList<Grade>>()
        repo.getGradeData(subjectID).observeForever { gradeList ->
            mutableData.value = gradeList
        }
        return mutableData
    }

    fun fetchGradesData(subjectID: String): LiveData<MutableList<Grade>>{
        val mutableData = MutableLiveData <MutableList<Grade>>()
        repo.getGradesData(subjectID).observeForever { gradeList ->
            mutableData.value = gradeList
        }
        return mutableData
    }
}