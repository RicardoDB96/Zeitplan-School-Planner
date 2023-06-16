package com.domberdev.zeitplan.schedule.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.schedule.bottomsheet.ScheduleInfoFragment
import com.domberdev.zeitplan.schedule.timetable.model.ScheduleEntity
import com.domberdev.zeitplan.schedule.timetable.tableinterface.OnScheduleClickListener
import com.domberdev.zeitplan.schedule.timetable.tableinterface.OnScheduleLongClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.fragment_schedule.*

class ScheduleFragment : Fragment(), ScheduleInfoFragment.OnScheduleInfoButton, AddScheduleFragment.UpdateScheduleData, EditScheduleFragment.UpdateScheduleData{

    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email

    private val subjectList = mutableListOf<String>()
    private val mutableSubjectData = MutableLiveData<MutableList<String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseFirestore.getInstance().collection("Users").document(userEmail!!).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").get(Source.CACHE).addOnSuccessListener { subjects ->
                for (id in subjects) {
                    val subjectID = id.getString("subjectID")!!
                    subjectList.add(subjectID)
                }
                mutableSubjectData.value = subjectList
            }
        }
        addSubjectToScheduleButton.setOnClickListener { newSchedule() }
        timetable.isTwentyFourHourClock(false)
        timetable.initTable()
        getSubjectSchedule().observe(viewLifecycleOwner) {
            timetable.updateSchedules(it)
            scheduleProgressBar.visibility = View.GONE
        }
        scheduleClick()
    }

    private fun getSubjectSchedule(): MutableLiveData<MutableList<ScheduleEntity>> {
        val mutableData = MutableLiveData<MutableList<ScheduleEntity>>()
        val scheduleList = mutableListOf<ScheduleEntity>()
        db.collection("Users").document(userEmail!!).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Schedules").get(Source.CACHE).addOnSuccessListener { schedules ->
                for (schedule in schedules){
                    val scheduleID = schedule.getString("scheduleID")!!
                    val subjectAcronym = schedule.getString("subjectAcronym")!!
                    val subjectPlace = when(schedule.getString("place")!!){
                        "6593632304" -> getString(R.string.online)
                        else -> schedule.getString("place")!!
                    }
                    val scheduleDay = schedule.getString("scheduleDay")!!.toInt()
                    val scheduleStartTime = schedule.getString("scheduleStartTime")!!
                    val scheduleEndTime = schedule.getString("scheduleEndTime")!!
                    val subjectBackground = schedule.getString("color")!!.toInt()
                    val subjectID = schedule.getString("subjectID")!!
                    val startTime12 = schedule.getString("startTime12")!!
                    val endTime12 = schedule.getString("endTime12")!!
                    val subjectSchedule = ScheduleEntity(scheduleID, subjectAcronym, subjectPlace, scheduleDay, scheduleStartTime, scheduleEndTime, subjectBackground, subjectID, startTime12, endTime12)
                    if (subjectList.contains(subjectID)){
                        scheduleList.add(subjectSchedule)
                    }else{
                        println("Deleting $subjectID with id: $scheduleID")
                        db.collection("Users/$userEmail/Phase$phase/Period$period/Schedules").document(scheduleID).delete()
                    }
                }
                mutableData.value = scheduleList
            }
        }
        return mutableData
    }

    private fun scheduleClick(){
        timetable.setOnScheduleClickListener(
            object: OnScheduleClickListener{
                override fun scheduleClicked(entity: ScheduleEntity) {
                    val bottomSheetFragment = ScheduleInfoFragment(this@ScheduleFragment)
                    val bundle = Bundle()
                    bundle.putString("scheduleID", entity.scheduleID)
                    bundle.putString("subjectID", entity.subjectID)
                    bottomSheetFragment.arguments = bundle
                    bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
                }
            }
        )

        timetable.setOnScheduleLongClickListener(
            object: OnScheduleLongClickListener{
                override fun scheduleLongClicked(entity: ScheduleEntity) {
                    val bottomSheetFragment = ScheduleInfoFragment(this@ScheduleFragment)
                    val bundle = Bundle()
                    bundle.putString("scheduleID", entity.scheduleID)
                    bundle.putString("subjectID", entity.subjectID)
                    bottomSheetFragment.arguments = bundle
                    bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
                }
            }
        )
    }

    private fun updateSchedules(): MutableLiveData<MutableList<ScheduleEntity>> {
        val mutableData = MutableLiveData<MutableList<ScheduleEntity>>()
        val scheduleList = mutableListOf<ScheduleEntity>()

        FirebaseFirestore.getInstance().collection("Users").document(userEmail!!).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").get(Source.CACHE).addOnSuccessListener { subjects ->
                for (id in subjects){
                    id.getString("subjectID")?.let { subjectList.add(it) }
                }
            }
            db.collection("Users/$userEmail/Phase$phase/Period$period/Schedules").get(Source.CACHE).addOnSuccessListener { schedules ->
                for (schedule in schedules){
                    val scheduleID = schedule.getString("scheduleID")!!
                    val subjectAcronym = schedule.getString("subjectAcronym")!!
                    val subjectPlace = when(schedule.getString("place")!!){
                        "6593632304" -> getString(R.string.online)
                        else -> schedule.getString("place")!!
                    }
                    val scheduleDay = schedule.getString("scheduleDay")!!.toInt()
                    val scheduleStartTime = schedule.getString("scheduleStartTime")!!
                    val scheduleEndTime = schedule.getString("scheduleEndTime")!!
                    val subjectBackground = schedule.getString("color")!!.toInt()
                    val subjectID = schedule.getString("subjectID")!!
                    val startTime12 = schedule.getString("startTime12")!!
                    val endTime12 = schedule.getString("endTime12")!!
                    val subjectSchedule = ScheduleEntity(scheduleID, subjectAcronym, subjectPlace, scheduleDay, scheduleStartTime, scheduleEndTime, subjectBackground, subjectID, startTime12, endTime12)
                    if (subjectList.contains(subjectID)){
                        scheduleList.add(subjectSchedule)
                    }
                }
                mutableData.value = scheduleList
            }
        }
        return mutableData
    }

    private fun newSchedule(){
        val addScheduleDialog = AddScheduleFragment(this)
        addScheduleDialog.show(parentFragmentManager, addScheduleDialog.tag)
    }

    override fun onDeleteClick() {
        timetable.initTable()
        updateSchedules().observe(viewLifecycleOwner){
            timetable.reloadSchedules(it)
        }
    }

    override fun callEdit(bundle: Bundle) {
        val editDialog = EditScheduleFragment(this)
        editDialog.arguments = bundle
        editDialog.show(parentFragmentManager, editDialog.tag)
    }

    override fun updateScheduleData() {
        timetable.initTable()
        updateSchedules().observe(viewLifecycleOwner){
            timetable.reloadSchedules(it)
        }
    }
}
