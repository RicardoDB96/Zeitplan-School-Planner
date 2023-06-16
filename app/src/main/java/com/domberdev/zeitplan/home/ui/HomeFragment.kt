package com.domberdev.zeitplan.home.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.agenda.network.TaskViewModel
import com.domberdev.zeitplan.home.adapter.HomeSubjectAdapter
import com.domberdev.zeitplan.home.adapter.UpcomingEventsAdapter
import com.domberdev.zeitplan.home.bottomsheet.HomeBottomSheetFragment
import com.domberdev.zeitplan.home.bottomsheet.HomeSubjectOptionsBottomSheet
import com.domberdev.zeitplan.schedule.timetable.model.ScheduleEntity
import com.domberdev.zeitplan.subjects.network.SubjectViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.File

class HomeFragment : Fragment(), HomeSubjectAdapter.OnSubjectClickListener, HomeSubjectOptionsBottomSheet.OnSettingsSubjectListener, UpcomingEventsAdapter.OnUpcomingEventClickListener {

    private val db = Firebase.firestore

    private lateinit var adapter: HomeSubjectAdapter
    private lateinit var eventsAdapter: UpcomingEventsAdapter
    private val viewModel by lazy { ViewModelProvider(this)[SubjectViewModel::class.java] }
    private val viewModelUpcomingEvents by lazy { ViewModelProvider(this)[TaskViewModel::class.java] }
    private val userEmail = Firebase.auth.currentUser?.email!!
    private val uid = Firebase.auth.currentUser?.uid!!
    private val storageRef = Firebase.storage.reference

    @SuppressLint("NotifyDataSetChanged")
    private fun observeSubjectsData() {
        noSubjectHomeData.visibility = View.GONE
        val dbSubjects = Firebase.firestore
        shimmerSubjectsHome.showShimmer(true)
        val cache = when(requireContext().getSharedPreferences("userdata", Context.MODE_PRIVATE).getBoolean("getAllData", false)){
            true -> Source.SERVER
            false -> Source.CACHE
        }
        if (cache == Source.SERVER){
            val mutableData = MutableLiveData<MutableList<ScheduleEntity>>()
            val scheduleList = mutableListOf<ScheduleEntity>()
            dbSubjects.collection("Users").document(userEmail).get(Source.SERVER).addOnSuccessListener { user ->
                val phase = user.getString("Phase")!!
                val period = user.getString("Period")!!

                dbSubjects.collection("Users/$userEmail/Phase$phase").get(Source.SERVER).addOnSuccessListener { periods ->
                    for (grades in periods){
                        grades.getString("periodGrade")!!
                        grades.getString("periodGradeRound")!!
                    }
                }

                dbSubjects.collection("Users/$userEmail/Phase$phase/Period$period/Schedules").get(Source.SERVER).addOnSuccessListener { schedules ->
                    for (schedule in schedules){
                        val scheduleID = schedule.getString("scheduleID")!!
                        val subjectAcronym = schedule.getString("subjectAcronym")!!
                        val subjectPlace = schedule.getString("place")!!
                        val scheduleDay = schedule.getString("scheduleDay")!!.toInt()
                        val scheduleStartTime = schedule.getString("scheduleStartTime")!!
                        val scheduleEndTime = schedule.getString("scheduleEndTime")!!
                        val subjectBackground = schedule.getString("color")!!.toInt()
                        val subjectID = schedule.getString("subjectID")!!
                        val startTime12 = schedule.getString("startTime12")!!
                        val endTime12 = schedule.getString("endTime12")!!
                        val subjectSchedule = ScheduleEntity(scheduleID, subjectAcronym, subjectPlace, scheduleDay, scheduleStartTime, scheduleEndTime, subjectBackground, subjectID, startTime12, endTime12)
                        scheduleList.add(subjectSchedule)
                    }
                    mutableData.value = scheduleList
                }
            }
        }
        dbSubjects.collection("Users").document(userEmail).get(cache).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!

            dbSubjects.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").get(cache).addOnSuccessListener { subjects ->
                if (subjects.size() != 0 || !subjects.isEmpty){
                    subjectHomeRV.visibility = View.VISIBLE
                    viewModel.fetchSubjectHomeData(requireContext()).observe(viewLifecycleOwner) {
                        adapter.setListData(it)
                        adapter.notifyDataSetChanged()
                        shimmerSubjectsHome.stopShimmer()
                        shimmerSubjectsHome.showShimmer(false)
                        shimmerSubjectsHome.visibility = View.GONE
                        if (adapter.itemCount == 0) {
                            noSubjectHomeData.visibility = View.VISIBLE
                            subjectHomeRV.visibility = View.GONE
                        } else {
                            noSubjectHomeData.visibility = View.GONE
                            subjectHomeRV.visibility = View.VISIBLE
                        }
                    }
                }else{
                    shimmerSubjectsHome.stopShimmer()
                    shimmerSubjectsHome.showShimmer(false)
                    shimmerSubjectsHome.visibility = View.GONE
                    noSubjectHomeData.visibility = View.VISIBLE
                    subjectHomeRV.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeUpcomingEventsData(){
        shimmerUpcomingTaskHome.showShimmer(true)
        val db = Firebase.firestore
        val cache = when(requireContext().getSharedPreferences("userdata", Context.MODE_PRIVATE).getBoolean("getAllData", false)){
            true -> Source.SERVER
            false -> Source.CACHE
        }
        db.collection("Users").document(userEmail).get(cache).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!

            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").get(cache).addOnSuccessListener { upcomingEvents ->
                if (upcomingEvents.size() != 0 || !upcomingEvents.isEmpty){
                    viewModelUpcomingEvents.fetchUpcomingEvents(cache).observe(viewLifecycleOwner) {
                        shimmerUpcomingTaskHome.stopShimmer()
                        shimmerUpcomingTaskHome.showShimmer(false)
                        shimmerUpcomingTaskHome.visibility = View.GONE
                        eventsAdapter.setListData(it)
                        eventsAdapter.notifyDataSetChanged()
                        if (eventsAdapter.itemCount == 0){
                            noUpcomingEventsData.visibility = View.VISIBLE
                            upcomingEventsRV.visibility = View.GONE
                        }else{
                            noUpcomingEventsData.visibility = View.GONE
                            upcomingEventsRV.visibility = View.VISIBLE
                        }
                    }
                }else{
                    shimmerUpcomingTaskHome.stopShimmer()
                    shimmerUpcomingTaskHome.showShimmer(false)
                    shimmerUpcomingTaskHome.visibility = View.GONE
                    noUpcomingEventsData.visibility = View.VISIBLE
                    upcomingEventsRV.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)  // initialize it here
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        periodsOnCache()
        if (requireContext().getSharedPreferences("userdata", Context.MODE_PRIVATE).getBoolean("getAllData", false)){
            db.collection("Users").document(userEmail).get(Source.SERVER).addOnSuccessListener { user ->
                user.getString("Phase")!!
                val period = user.get("Period")!!.toString()
                val currentPeriod = user.get("Current")
                user.get("newUser")
                user.get("career")

                val gson = Gson()

                val periodsOnCache = arrayListOf<String>()
                if (currentPeriod == null){
                    db.collection("Users").document(userEmail).update("Current", period)
                    periodsOnCache.add(period)
                }else {
                    db.collection("Users").document(userEmail).update("Period", currentPeriod)
                    periodsOnCache.add(currentPeriod.toString())
                }

                val list = gson.toJson(periodsOnCache)

                val prefs = requireContext().getSharedPreferences("userdata", Context.MODE_PRIVATE).edit()
                prefs.putString("periodsOnCache", list)
                prefs.apply()


                val name = user.getString("Name")!!

                Firebase.auth.currentUser.let {
                    val userImageRef = storageRef.child("Users/$uid/profile")
                    userImageRef.getFile(Uri.fromFile(File(requireContext().cacheDir, "profilePicture.jpg")))
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .setPhotoUri(Uri.fromFile(File(requireContext().cacheDir, "profilePicture.jpg")))
                        .build()

                    Firebase.auth.currentUser?.updateProfile(profileUpdates)
                }
            }
        }
        noSubjectHomeData.visibility = View.GONE
        eventsAdapter = UpcomingEventsAdapter(requireContext(), this)
        upcomingEventsRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        upcomingEventsRV.adapter = eventsAdapter
        adapter = HomeSubjectAdapter(requireContext(), this)
        subjectHomeRV.layoutManager = GridLayoutManager(context, 2)
        subjectHomeRV.adapter = adapter
        addSomethingFAB()
        homeSubjectBottomSheet()

        db.waitForPendingWrites().addOnCompleteListener {
            if (it.isComplete){
                observeUpcomingEventsData()
                observeSubjectsData()
                val prefs = requireContext().getSharedPreferences("userdata", Context.MODE_PRIVATE).edit()
                prefs.putBoolean("getAllData", false)
                prefs.apply()
            }
        }
        newUserData()
    }

    private fun newUserData(){
        val newUserDB = FirebaseFirestore.getInstance()
        newUserDB.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val newUser = user.get("newUser")
            val career = user.get("career")

            if (newUser == null && career == null){
                val dialog = AlertDialog.Builder(requireContext()).create()
                val v = layoutInflater.inflate(R.layout.academic_infomation, null)
                dialog.setView(v)
                dialog.setCancelable(false)
                dialog.show()

                val cancelButton = v.findViewById<TextView>(R.id.cancelAcademicButton)
                cancelButton.visibility = View.GONE

                val universityEdit = v.findViewById<TextInputEditText>(R.id.universityEdit)
                val careerEdit = v.findViewById<TextInputEditText>(R.id.careerEdit)
                val semesterSelector = v.findViewById<Spinner>(R.id.semesterSelector)
                val durationSelector = v.findViewById<Spinner>(R.id.durationSelector)
                val saveButton = v.findViewById<TextView>(R.id.saveAcademicButton)

                semesterSelector.setSelection(7)
                durationSelector.setSelection(3)

                saveButton.setOnClickListener {
                    if (universityEdit.text!!.isNotEmpty() && careerEdit.text!!.isNotEmpty()){
                        newUserDB.enableNetwork().addOnSuccessListener {
                            newUserDB.collection("Users").document(userEmail).update(
                                mapOf(
                                    "University" to universityEdit.text.toString(),
                                    "Career" to careerEdit.text.toString(),
                                    "Duration" to semesterSelector.selectedItemPosition.toString(),
                                    "Type" to durationSelector.selectedItemPosition.toString()
                                ))

                            newUserDB.collection("Users").document(userEmail).update("newUser", false)
                        }
                        dialog.dismiss()
                    }else{
                        if (universityEdit.text!!.isEmpty()){
                            universityEdit.error = getString(R.string.youMustWriteUniversityInstitution)
                        }
                        if (careerEdit.text!!.isEmpty()){
                            careerEdit.error = getString(R.string.youMustWriteCareerCourse)
                        }
                    }
                }
            }
        }
    }

    private fun addSomethingFAB(){
        createNewTaskHome.setOnClickListener {
            findNavController().navigate(R.id.homeToNewTask)
        }
        createNewSubjectHome.setOnClickListener {
            findNavController().navigate(R.id.homeToNewSubject)
        }
    }

    override fun onUpcomingEventClick(taskID: String, subjectID: String) {
        val bundle = Bundle()
        bundle.putString("taskID", taskID)
        bundle.putString("subjectID", subjectID)
        bundle.putInt("navID", R.id.editTaskToHomeFragment)
        findNavController().navigate(R.id.homeToTaskInfo, bundle)
    }

    override fun onSubjectClick(subjectID: String) {
        val bundle = Bundle()
        bundle.putString("subjectID", subjectID)
        findNavController().navigate(R.id.homeToInfoSubject, bundle)
    }

    override fun onSubjectLongClick(subjectID: String, generalGrade: Boolean) {
        val bottomSheetFragment = HomeSubjectOptionsBottomSheet(this)
        val bundle = Bundle()
        bundle.putString("subjectID", subjectID)
        bundle.putBoolean("generalGrade", generalGrade)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
    }

    override fun onSettingsClick(subjectID: String, generalGrade: Boolean) {
        val bottomSheetFragment = HomeSubjectOptionsBottomSheet(this)
        val bundle = Bundle()
        bundle.putString("subjectID", subjectID)
        bundle.putBoolean("generalGrade", generalGrade)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
    }

    override fun updateSubjectData() {
        observeSubjectsData()
        db.waitForPendingWrites().addOnCompleteListener {
            if (it.isComplete){
                observeUpcomingEventsData()
            }
        }
    }

    private fun homeSubjectBottomSheet(){
        val bottomSheetFragment = HomeBottomSheetFragment()
        settingButtonSubjectHome.setOnClickListener {
            bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
        }
    }

    private fun periodsOnCache(){
        val periodsOnCache = requireContext().getSharedPreferences("userdata", Context.MODE_PRIVATE).getString("periodsOnCache", null)

        if (periodsOnCache != null){
            val list = Gson().fromJson(periodsOnCache, ArrayList<String>()::class.java)
            db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                val period = user.getString("Period")!!

                if (list.contains(period)){
                    println("Cache")
                }else{
                    list.add(period)
                    val periods = Gson().toJson(list)
                    val prefs = requireContext().getSharedPreferences("userdata", Context.MODE_PRIVATE).edit()
                    prefs.putString("periodsOnCache", periods)
                    prefs.apply()
                    db.enableNetwork().addOnCompleteListener {
                        observePeriodData()
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observePeriodData(){
        noSubjectHomeData.visibility = View.GONE
        shimmerSubjectsHome.showShimmer(true)
        db.collection("Users").document(userEmail).get(Source.SERVER).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Schedules").get(Source.SERVER).addOnSuccessListener { schedules ->
                for (schedule in schedules){
                    schedule.getString("scheduleID")!!
                    schedule.getString("subjectAcronym")!!
                    schedule.getString("place")!!
                    schedule.getString("scheduleDay")!!.toInt()
                    schedule.getString("scheduleStartTime")!!
                    schedule.getString("scheduleEndTime")!!
                    schedule.getString("color")!!.toInt()
                    schedule.getString("subjectID")!!
                    schedule.getString("startTime12")!!
                    schedule.getString("endTime12")!!
                }
            }

            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").get(Source.SERVER).addOnSuccessListener { subjects ->
                if (subjects.size() != 0){
                    subjectHomeRV.visibility = View.VISIBLE
                    viewModel.fetchSubjectHomeData(requireContext()).observe(viewLifecycleOwner) {
                        adapter.setListData(it)
                        adapter.notifyDataSetChanged()
                        shimmerSubjectsHome.stopShimmer()
                        shimmerSubjectsHome.showShimmer(false)
                        noSubjectHomeData.visibility = View.GONE
                        shimmerSubjectsHome.visibility = View.GONE
                    }
                }else{
                    shimmerSubjectsHome.stopShimmer()
                    shimmerSubjectsHome.showShimmer(false)
                    shimmerSubjectsHome.visibility = View.GONE
                    noSubjectHomeData.visibility = View.VISIBLE
                    subjectHomeRV.visibility = View.GONE
                }
            }

            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").get(Source.SERVER).addOnSuccessListener { upcomingEvents ->
                if (upcomingEvents.size() != 0 && !upcomingEvents.isEmpty){
                    viewModelUpcomingEvents.fetchUpcomingEvents(Source.SERVER).observe(viewLifecycleOwner) {
                        shimmerUpcomingTaskHome.stopShimmer()
                        shimmerUpcomingTaskHome.showShimmer(false)
                        shimmerUpcomingTaskHome.visibility = View.GONE
                        eventsAdapter.setListData(it)
                        eventsAdapter.notifyDataSetChanged()
                        if (eventsAdapter.itemCount == 0){
                            noUpcomingEventsData.visibility = View.VISIBLE
                            upcomingEventsRV.visibility = View.GONE
                        }else{
                            noUpcomingEventsData.visibility = View.GONE
                            upcomingEventsRV.visibility = View.VISIBLE
                        }
                    }
                }else{
                    shimmerUpcomingTaskHome.stopShimmer()
                    shimmerUpcomingTaskHome.showShimmer(false)
                    shimmerUpcomingTaskHome.visibility = View.GONE
                    noUpcomingEventsData.visibility = View.VISIBLE
                    upcomingEventsRV.visibility = View.GONE
                }
            }
        }
    }
}