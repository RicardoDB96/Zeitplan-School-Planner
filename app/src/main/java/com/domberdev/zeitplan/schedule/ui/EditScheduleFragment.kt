package com.domberdev.zeitplan.schedule.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.dialog.SelectSubjectAlertDialog
import com.domberdev.zeitplan.dialog.TimePickerFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.fragment_edit_schedule.*

class EditScheduleFragment(private val updateScheduleData: UpdateScheduleData?) : DialogFragment(), SelectSubjectAlertDialog.GetSubjectList, TimePickerFragment.OnTimeSelected, SelectSubjectAlertDialog.DismissDialog {

    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email!!

    private lateinit var scheduleID: String
    private lateinit var subjectID: String
    private lateinit var subjectIDNew: String
    private lateinit var day: String
    private lateinit var dayNew: String
    private var startTime: String = "0"
    private var endTime: String = "0"

    private var interstitial: InterstitialAd? = null

    interface UpdateScheduleData{
        fun updateScheduleData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_edit_schedule, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scheduleID = arguments?.getString("scheduleID")!!
        subjectID = arguments?.getString("subjectID")!!
        subjectIDNew = subjectID
        editScheduleToolbar.setNavigationOnClickListener { dismiss() }
        bindData()
        selectSubject()
        selectDay()
        selectStartTime()
        selectEndTime()
        saveChanges()
        initAds()
    }

    private fun initAds(){
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(), getString(R.string.interstitialAdId), adRequest, object: InterstitialAdLoadCallback(){
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                interstitial = interstitialAd
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                interstitial = null
            }
        })
    }

    private fun showAd(){
        val random = (1..100).random()
        if (random <=65){
            interstitial?.show(requireActivity())
        }
    }

    private fun bindData(){
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get(Source.CACHE).addOnSuccessListener {
                editSelectSubjectSchedule.text = it.getString("subjectTitle")
                editClassroomSubjectSchedule.text = when(it.getString("place")){
                    "6593632304" -> getString(R.string.online)
                    else -> it.getString("place")
                }
                editProfessorNameSchedule.text = it.getString("teacher")
            }
            db.collection("Users/$userEmail/Phase$phase/Period$period/Schedules").document(scheduleID).get(Source.CACHE).addOnSuccessListener {
                day = it.getString("scheduleDay")!!
                val dayV = when(day.toInt()){
                    0 -> getString(R.string.MondayC)
                    1 -> getString(R.string.TuesdayC)
                    2 -> getString(R.string.WednesdayC)
                    3 -> getString(R.string.ThursdayC)
                    4 -> getString(R.string.FridayC)
                    5 -> getString(R.string.SaturdayC)
                    6 -> getString(R.string.SundayC)
                    else -> "Error"
                }
                editSelectDaySchedule.text = dayV

                startTime = it.getString("scheduleStartTime")!!
                endTime = it.getString("scheduleEndTime")!!
                dayNew = day

                editSelectStartTime.text = it.getString("startTime12")
                editSelectEndTime.text = it.getString("endTime12")
            }
        }
    }

    private fun selectSubject(){
        SelectSubjectAlertDialog(requireContext(), this, this).getSubjectListTextView(editSelectSubjectSchedule, this, R.id.scheduleToNewSubject)
    }

    override fun getSubjectList(subjectID: String) {
        this.subjectIDNew = subjectID
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get(Source.CACHE).addOnSuccessListener { subjectData ->
                editClassroomSubjectSchedule.text = subjectData.getString("place")
                editProfessorNameSchedule.text = subjectData.getString("teacher")
            }
        }
    }

    private fun selectDay(){
        editSelectDaySchedule.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle(R.string.selectADay)
            builder.setItems(resources.getStringArray(R.array.dayC)){ dialog, which ->
                when(which){
                    0 -> {
                        editSelectDaySchedule.text = getString(R.string.MondayC)
                        dayNew = "0"
                    }
                    1 -> {
                        editSelectDaySchedule.text = getString(R.string.TuesdayC)
                        dayNew = "1"
                    }
                    2 -> {
                        editSelectDaySchedule.text = getString(R.string.WednesdayC)
                        dayNew = "2"
                    }
                    3 -> {
                        editSelectDaySchedule.text = getString(R.string.ThursdayC)
                        dayNew = "3"
                    }
                    4 -> {
                        editSelectDaySchedule.text = getString(R.string.FridayC)
                        dayNew = "4"
                    }
                    5 -> {
                        editSelectDaySchedule.text = getString(R.string.SaturdayC)
                        dayNew = "5"
                    }
                    6 -> {
                        editSelectDaySchedule.text = getString(R.string.SundayC)
                        dayNew = "6"
                    }
                }
                dialog.dismiss()
            }
            builder.setNegativeButton(R.string.cancel){_,_ -> }
            builder.show()
        }
    }

    private fun selectStartTime() {
        editSelectStartTime.setOnClickListener {
            val timePicker = TimePickerFragment(this, editSelectStartTime)
            timePicker.show(parentFragmentManager, "timePicker")
        }
    }

    private fun selectEndTime() {
        editSelectEndTime.setOnClickListener {
            val timePicker = TimePickerFragment(this, editSelectEndTime)
            timePicker.show(parentFragmentManager, "timePicker")
        }
    }

    override fun onTimeSelected(text: String, time: String, textView: TextView) {
        when(textView){
            editSelectStartTime -> {
                startTime = time
                editSelectStartTime.text = text
            }
            editSelectEndTime -> {
                endTime = time
                editSelectEndTime.text = text
            }
        }
    }

    private fun saveChanges(){
        editScheduleButton.setOnClickListener {
            editSelectSubjectSchedule.error = null
            editSelectDaySchedule.error = null
            editSelectStartTime.error = null
            editSelectEndTime.error = null
            editClassroomSubjectSchedule.error = null
            editProfessorNameSchedule.error = null

            val selectSubjectScheduleError = editSelectSubjectSchedule.text.toString() != getString(R.string.selectASubject)
            val selectDayScheduleError = editSelectDaySchedule.text.toString() != getString(R.string.selectADay)
            val selectStartTimeError = editSelectStartTime.text.toString() != getString(R.string.selectAStartTime)
            val selectEndTimeError = editSelectEndTime.text.toString() != getString(R.string.selectAnEndTime)
            val classroomSubjectScheduleError = editClassroomSubjectSchedule.text.toString() != getString(R.string.classroomOfTheSubject)
            val professorNameScheduleError = editProfessorNameSchedule.text.toString() != getString(R.string.teachersName)

            val startTimeV = editSelectStartTime.text.toString()
            val endTimeV = editSelectEndTime.text.toString()

            if(selectSubjectScheduleError && selectDayScheduleError && selectStartTimeError && selectEndTimeError && classroomSubjectScheduleError && professorNameScheduleError) {
                if (startTime > endTime) {
                    editSelectStartTime.error = ""
                    editSelectEndTime.error = ""
                    Toast.makeText(requireContext(), R.string.theEndTimeCannotGoBeforeTheStartTime, Toast.LENGTH_LONG).show()
                }else {
                    db.enableNetwork().addOnCompleteListener {
                        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                            val phase = user.getString("Phase")!!
                            val period = user.getString("Period")!!
                            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectIDNew).get(Source.CACHE).addOnSuccessListener {
                                val place = when(it.getString("place")){
                                    R.string.online.toString() -> R.string.online.toString()
                                    else -> it.getString("place")
                                }
                                val subjectAcronym = it.getString("subjectAcronym")
                                val color = it.getString("color")
                                db.collection("Users/$userEmail/Phase$phase/Period$period/Schedules").document(scheduleID).update(
                                    mapOf(
                                        "scheduleID" to scheduleID,
                                        "subjectID" to subjectIDNew,
                                        "scheduleDay" to dayNew,
                                        "place" to place,
                                        "subjectAcronym" to subjectAcronym,
                                        "scheduleStartTime" to startTime,
                                        "scheduleEndTime" to endTime,
                                        "color" to color,
                                        "startTime12" to startTimeV,
                                        "endTime12" to endTimeV
                                    ))
                                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).update(
                                    "scheduleDays", FieldValue.arrayRemove(day)
                                )
                                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectIDNew).update(
                                    "scheduleDays", FieldValue.arrayUnion(dayNew)
                                )
                                updateScheduleData?.updateScheduleData()
                            }
                        }
                        showAd()
                        dismiss()
                    }
                }
            }else{
                if (!selectSubjectScheduleError){
                    editSelectSubjectSchedule.error = ""
                }
                if (!selectDayScheduleError){
                    editSelectDaySchedule.error = ""
                }
                if (!selectStartTimeError){
                    editSelectStartTime.error = ""
                }
                if (!selectEndTimeError){
                    editSelectEndTime.error = ""
                }
                if (!classroomSubjectScheduleError){
                    editClassroomSubjectSchedule.error = "a"
                }
                if (!professorNameScheduleError){
                    editProfessorNameSchedule.error = "a"
                }
            }
        }
    }

    override fun dismissDialog() {
        dismiss()
    }
}