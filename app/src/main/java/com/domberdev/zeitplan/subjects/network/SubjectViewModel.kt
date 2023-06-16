package com.domberdev.zeitplan.subjects.network

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.domberdev.zeitplan.subjects.model.Subject

class SubjectViewModel: ViewModel() {

    private val repo = Repo()
    fun fetchSubjectData(): LiveData<MutableList<Subject>> {
        val mutableData = MutableLiveData<MutableList<Subject>>()
        repo.getSubjectData().observeForever{ subjectList ->
            mutableData.value = subjectList
        }
        return mutableData
    }

    fun fetchSubjectHomeData(context: Context): LiveData<MutableList<Subject>> {
        val mutableData = MutableLiveData<MutableList<Subject>>()
        repo.getSubjectHomeData(context).observeForever{ subjectList ->
            mutableData.value = subjectList
        }
        return mutableData
    }

    fun fetchSubjectGradeData(): LiveData<MutableList<Subject>> {
        val mutableData = MutableLiveData<MutableList<Subject>>()
        repo.getSubjectGradeData().observeForever{ subjectList ->
            mutableData.value = subjectList
        }
        return mutableData
    }

    fun fetchSubjectSelectionData(): LiveData<MutableList<Subject>> {
        val mutableData = MutableLiveData<MutableList<Subject>>()
        repo.fetchSubjectSelectionData().observeForever{ subjectList ->
            mutableData.value = subjectList
        }
        return mutableData
    }
}