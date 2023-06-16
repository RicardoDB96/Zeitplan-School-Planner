package com.domberdev.zeitplan.agenda.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.dialog.DatePickerFragment
import com.domberdev.zeitplan.dialog.SelectSubjectAlertDialog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.fragment_create_new_task.*
import java.text.SimpleDateFormat
import java.util.*

class CreateNewTaskFragment : Fragment(), SelectSubjectAlertDialog.GetSubjectList {

    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email!!
    private val uid = FirebaseAuth.getInstance().currentUser?.uid!!

    private var dayTask = String()
    private lateinit var subjectID: String

    private var interstitial: InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_new_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createNewTaskToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        checkSubjectIDExistence()
        selectSubject()
        selectDeadline()
        selectTaskCategory()
        saveTask()
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
        println(random)
        if (random <= 90){
            interstitial?.show(requireActivity())
        }
    }

    private fun checkSubjectIDExistence(){
        val subjectIDFromSubjectInfo = arguments?.getString("subjectID", null)
        if (subjectIDFromSubjectInfo != null){
            db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                val phase = user.getString("Phase")!!
                val period = user.getString("Period")!!
                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectIDFromSubjectInfo).get(Source.CACHE).addOnSuccessListener {
                    selectASubjectTask.text = it.getString("subjectTitle")
                    subjectID = subjectIDFromSubjectInfo
                }
            }
        }
    }

    private fun selectSubject(){
        SelectSubjectAlertDialog(requireContext(), this, null).getSubjectList(selectASubjectTask, this, R.id.newTaskToNewSubject)
    }

    override fun getSubjectList(subjectID: String) {
        this.subjectID = subjectID
    }

    private fun selectDeadline(){
        selectDeadlineTask.setOnClickListener {
            showDatePickerDialog()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment.newInstance { _, year, month, day ->

            val monthNo = when(month){
                0 -> "01"
                1 -> "02"
                2 -> "03"
                3 -> "04"
                4 -> "05"
                5 -> "06"
                6 -> "07"
                7 -> "08"
                8 -> "09"
                9 -> "10"
                10 -> "11"
                11 -> "12"
                else -> "Error"
            }

            val dayNo = when(day.toString()){
                "1" -> "01"
                "2" -> "02"
                "3" -> "03"
                "4" -> "04"
                "5" -> "05"
                "6" -> "06"
                "7" -> "07"
                "8" -> "08"
                "9" -> "09"
                else -> day.toString()
            }

            val monthW = when(month){
                0 -> getString(R.string.January)
                1 -> getString(R.string.February)
                2 -> getString(R.string.March)
                3 -> getString(R.string.April)
                4 -> getString(R.string.May)
                5 -> getString(R.string.June)
                6 -> getString(R.string.July)
                7 -> getString(R.string.August)
                8 -> getString(R.string.September)
                9 -> getString(R.string.October)
                10 -> getString(R.string.November)
                11 -> getString(R.string.December)
                else -> "error"
            }

            dayTask = "$year-$monthNo-$dayNo"
            val dayToDate = SimpleDateFormat("yyyy-MM-dd").parse(dayTask)!!
            val dateToDayOfWeek = SimpleDateFormat("EEEE", Locale.ENGLISH).format(dayToDate)

            val dayW = when(dateToDayOfWeek.toString()){
                "Sunday" -> getString(R.string.SundayC)
                "Monday" -> getString(R.string.MondayC)
                "Tuesday" -> getString(R.string.TuesdayC)
                "Wednesday" -> getString(R.string.WednesdayC)
                "Thursday" -> getString(R.string.ThursdayC)
                "Friday" -> getString(R.string.FridayC)
                "Saturday" -> getString(R.string.SaturdayC)
                else -> "Error"
            }

            val selectedDate = "$dayW, $monthW $day, $year"
            selectDeadlineTask.text = selectedDate
        }
        newFragment.show(parentFragmentManager, "datePicker")
    }
    
    private fun selectTaskCategory(){
        val array = arrayOf<CharSequence>(getString(R.string.reminder),getString(R.string.Homework), getString(R.string.Teamwork), getString(R.string.Project), getString(R.string.QuickTest), getString(R.string.PartialExam), getString(R.string.Exam))

        selectACategoryTask.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(R.string.selectTheTypeOfTask)
                .setItems(array) { _, which ->
                    when (which) {
                        0 -> selectACategoryTask.text = getString(R.string.reminder)
                        1 -> selectACategoryTask.text = getString(R.string.Homework)
                        2 -> selectACategoryTask.text = getString(R.string.Teamwork)
                        3 -> selectACategoryTask.text = getString(R.string.Project)
                        4 -> selectACategoryTask.text = getString(R.string.QuickTest)
                        5 -> selectACategoryTask.text = getString(R.string.PartialExam)
                        6 -> selectACategoryTask.text = getString(R.string.Exam)
                    }
                }
                .show()
        }
    }

    private fun getRandomString() : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..20)
            .map { allowedChars.random() }
            .joinToString("")
    }

    @SuppressLint("SimpleDateFormat")
    private fun saveTask(){
        addTaskButton.setOnClickListener {
            addATitle.error = null
            selectDeadlineTask.error = null
            selectASubjectTask.error = null
            selectACategoryTask.error = null

            val selectDeadlineError = selectDeadlineTask.text.toString() != getString(R.string.selectDeadline)
            val selectASubjectError = selectASubjectTask.text.toString() != getString(R.string.selectASubject)
            val selectACategoryError = selectACategoryTask.text.toString() != getString(R.string.selectTheTypeOfTask)

            val category = when(selectACategoryTask.text.toString()){
                getString(R.string.reminder) -> "0"
                getString(R.string.Homework) -> "1"
                getString(R.string.Teamwork) -> "2"
                getString(R.string.Project) -> "3"
                getString(R.string.QuickTest) -> "4"
                getString(R.string.PartialExam) -> "5"
                getString(R.string.Exam) -> "6"
                else -> selectACategoryTask.text.toString()
            }

            val taskID = getRandomString()
            if (addATitle.text!!.isNotEmpty() && selectDeadlineError && selectASubjectError && selectACategoryError){
                db.enableNetwork().addOnSuccessListener {
                    db.collection("Tasks/$subjectID/tasks").document(taskID).set(
                        mapOf(
                            "taskTitle" to addATitle.text.toString(),
                            "timeLimit" to "23:59",
                            "category" to category,
                            "taskID" to taskID,
                            "subjectID" to subjectID,
                            "day" to dayTask,
                            "description" to taskDescription.text.toString(),
                            uid to false,
                            "${uid}finishedOn" to ""
                        ))
                }
                showAd()
                findNavController().popBackStack()
            }else{
                if (addATitle.text!!.isEmpty()){
                    addATitle.error = getString(R.string.taskMustHaveATitle)
                }
                if (!selectDeadlineError){
                    selectDeadlineTask.error = "Error"
                }
                if (!selectASubjectError){
                    selectASubjectTask.error = "Error"
                }
                if (!selectACategoryError){
                    selectACategoryTask.error = "Error"
                }
            }
        }
    }
}