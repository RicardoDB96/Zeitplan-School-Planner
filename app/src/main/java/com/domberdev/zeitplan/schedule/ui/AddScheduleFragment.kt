package com.domberdev.zeitplan.schedule.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
import kotlinx.android.synthetic.main.fragment_add_schedule.*

class AddScheduleFragment(private val updateScheduleData: UpdateScheduleData?) : DialogFragment(), SelectSubjectAlertDialog.GetSubjectList, TimePickerFragment.OnTimeSelected, SelectSubjectAlertDialog.DismissDialog {

    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email!!

    private lateinit var subjectID: String
    private lateinit var day: String
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
        return inflater.inflate(R.layout.fragment_add_schedule, container, false)
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
        addScheduleToolbar.setNavigationOnClickListener { dismiss() }
        selectSubject()
        selectDay()
        selectStartTime()
        selectEndTime()
        addSchedule()
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
        if (random <=70){
            interstitial?.show(requireActivity())
        }
    }

    private fun selectSubject(){
        SelectSubjectAlertDialog(requireContext(), this, this).getSubjectListTextView(selectSubjectSchedule, this, R.id.scheduleToNewSubject)
    }

    override fun getSubjectList(subjectID: String) {
        this.subjectID = subjectID
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get(Source.CACHE).addOnSuccessListener { subjectData ->
                classroomSubjectSchedule.text = when(subjectData.getString("place")){
                    "6593632304" -> getString(R.string.online)
                    else -> subjectData.getString("place")
                }
                professorNameSchedule.text = subjectData.getString("teacher")
            }
        }
    }

    private fun selectDay(){
        selectDaySchedule.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle(R.string.selectADay)
            builder.setItems(resources.getStringArray(R.array.dayC)){ dialog, which ->
                when(which){
                    0 -> {
                        selectDaySchedule.text = getString(R.string.MondayC)
                        day = "0"
                    }
                    1 -> {
                        selectDaySchedule.text = getString(R.string.TuesdayC)
                        day = "1"
                    }
                    2 -> {
                        selectDaySchedule.text = getString(R.string.WednesdayC)
                        day = "2"
                    }
                    3 -> {
                        selectDaySchedule.text = getString(R.string.ThursdayC)
                        day = "3"
                    }
                    4 -> {
                        selectDaySchedule.text = getString(R.string.FridayC)
                        day = "4"
                    }
                    5 -> {
                        selectDaySchedule.text = getString(R.string.SaturdayC)
                        day = "5"
                    }
                    6 -> {
                        selectDaySchedule.text = getString(R.string.SundayC)
                        day = "6"
                    }
                }
                dialog.dismiss()
            }
            builder.setNegativeButton(R.string.cancel){_,_ -> }
            builder.show()
        }
    }

    private fun selectStartTime() {
        selectStartTime.setOnClickListener {
            val timePicker = TimePickerFragment(this, selectStartTime)
            timePicker.show(parentFragmentManager, "timePicker")
        }
    }

    private fun selectEndTime() {
        selectEndTime.setOnClickListener {
            val timePicker = TimePickerFragment(this, selectEndTime)
            timePicker.show(parentFragmentManager, "timePicker")
        }
    }

    override fun onTimeSelected(text: String, time: String, textView: TextView) {
        when(textView){
            selectStartTime -> {
                startTime = time
                selectStartTime.text = text
            }
            selectEndTime -> {
                endTime = time
                selectEndTime.text = text
            }
        }
    }

    private fun getRandomString() : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..20)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun addSchedule(){
        addScheduleButton.setOnClickListener {
            selectSubjectSchedule.error = null
            selectDaySchedule.error = null
            selectStartTime.error = null
            selectEndTime.error = null
            classroomSubjectSchedule.error = null
            professorNameSchedule.error = null

            val selectSubjectScheduleError = selectSubjectSchedule.text.toString() != getString(R.string.selectASubject)
            val selectDayScheduleError = selectDaySchedule.text.toString() != getString(R.string.selectADay)
            val selectStartTimeError = selectStartTime.text.toString() != getString(R.string.selectAStartTime)
            val selectEndTimeError = selectEndTime.text.toString() != getString(R.string.selectAnEndTime)
            val classroomSubjectScheduleError = classroomSubjectSchedule.text.toString() != getString(R.string.classroomOfTheSubject)
            val professorNameScheduleError = professorNameSchedule.text.toString() != getString(R.string.teachersName)

            val startTimeV = selectStartTime.text.toString()
            val endTimeV = selectEndTime.text.toString()

            val scheduleID = getRandomString()

            if(selectSubjectScheduleError && selectDayScheduleError && selectStartTimeError && selectEndTimeError && classroomSubjectScheduleError && professorNameScheduleError){
                if (startTime > endTime){
                    selectStartTime.error = ""
                    selectEndTime.error = ""
                    Toast.makeText(requireContext(), R.string.theEndTimeCannotGoBeforeTheStartTime, Toast.LENGTH_LONG).show()
                }else{
                    db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                        val phase = user.getString("Phase")!!
                        val period = user.getString("Period")!!
                        db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get(Source.CACHE).addOnSuccessListener {
                            val place = when(it.getString("place")){
                                "6593632304" -> R.string.online.toString()
                                else -> it.getString("place")
                            }
                            val subjectAcronym = it.getString("subjectAcronym")
                            val color = it.getString("color")
                            db.enableNetwork().addOnCompleteListener {
                                db.collection("Users/$userEmail/Phase$phase/Period$period/Schedules").document(scheduleID).set(
                                    mapOf(
                                        "scheduleID" to scheduleID,
                                        "subjectID" to subjectID,
                                        "scheduleDay" to day,
                                        "place" to place,
                                        "subjectAcronym" to subjectAcronym,
                                        "scheduleStartTime" to startTime,
                                        "scheduleEndTime" to endTime,
                                        "color" to color,
                                        "startTime12" to startTimeV,
                                        "endTime12" to endTimeV
                                    ))
                                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).update(
                                    "scheduleDays", FieldValue.arrayUnion(day)
                                )
                            }
                            updateScheduleData?.updateScheduleData()
                        }
                        showAd()
                        dismiss()
                    }
                }
            }else{
                if (!selectSubjectScheduleError){
                    selectSubjectSchedule.error = ""
                }
                if (!selectDayScheduleError){
                    selectDaySchedule.error = ""
                }
                if (!selectStartTimeError){
                    selectStartTime.error = ""
                }
                if (!selectEndTimeError){
                    selectEndTime.error = ""
                }
                if (!classroomSubjectScheduleError){
                    classroomSubjectSchedule.error = "a"
                }
                if (!professorNameScheduleError){
                    professorNameSchedule.error = "a"
                }
            }
        }
    }

    override fun dismissDialog() {
        dismiss()
    }
}