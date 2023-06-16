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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_create_new_task.*
import kotlinx.android.synthetic.main.fragment_edit_task.*
import java.text.SimpleDateFormat
import java.util.*

class EditTaskFragment : Fragment(), SelectSubjectAlertDialog.GetSubjectList {

    val db = FirebaseFirestore.getInstance()
    val userEmail = FirebaseAuth.getInstance().currentUser?.email!!
    private lateinit var taskID: String
    private lateinit var subjectID: String
    private lateinit var reference: DocumentReference

    private var navID: Int = 0

    private var dayTask = String()
    private var subjectIDOld = String()
    private lateinit var subjectIDNew: String

    private var interstitial: InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskID = arguments?.getString("taskID")!!
        subjectID = arguments?.getString("subjectID")!!
        navID = arguments?.getInt("navID")!!
        reference = db.collection("Tasks/$subjectID/tasks").document(taskID)
        editTaskToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        bindData()
        selectSubject()
        selectDeadline()
        selectTaskCategory()
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
        if (random <= 85){
            interstitial?.show(requireActivity())
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun bindData(){
        reference.get(Source.CACHE).addOnSuccessListener {
            db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                val phase = user.getString("Phase")!!
                val period = user.getString("Period")!!
                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get(Source.CACHE).addOnSuccessListener { subject ->
                    editSelectASubjectTask.text = subject.getString("subjectTitle")
                    subjectIDOld = subject.getString("subjectID")!!
                    subjectIDNew = subject.getString("subjectID")!!
                }
            }

            editATitle.setText(it.getString("taskTitle"))

            val date = SimpleDateFormat("yyyy-MM-dd").parse(it.getString("day")!!)
            val day = SimpleDateFormat("EEEE, MMMM d, yyyy").format(date!!)
            editSelectDeadlineTask.text = day.capitalizeWords()
            editSelectACategoryTask.text = when(it.getString("category")){
                "0" -> getString(R.string.reminder)
                "1" -> getString(R.string.Homework)
                "2" -> getString(R.string.Teamwork)
                "3" -> getString(R.string.Project)
                "4" -> getString(R.string.QuickTest)
                "5" -> getString(R.string.PartialExam)
                "6" -> getString(R.string.Exam)
                else -> it.getString("category")
            }
            editTaskDescription.setText(it.getString("description"))
            dayTask = it.getString("day")!!
        }
    }

    private fun selectSubject(){
        SelectSubjectAlertDialog(requireContext(), this, null).getSubjectList(editSelectASubjectTask, this, R.id.editTaskToNewSubject)
    }

    override fun getSubjectList(subjectID: String) {
        subjectIDNew = subjectID
    }

    private fun selectDeadline(){
        editSelectDeadlineTask.setOnClickListener {
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
            editSelectDeadlineTask.text = selectedDate
        }

        newFragment.show(parentFragmentManager, "datePicker")
    }

    private fun selectTaskCategory(){
        val array = arrayOf<CharSequence>(getString(R.string.reminder),getString(R.string.Homework), getString(R.string.Teamwork), getString(R.string.Project), getString(R.string.QuickTest), getString(R.string.PartialExam), getString(R.string.Exam))

        editSelectACategoryTask.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(R.string.selectTheTypeOfTask)
                .setItems(array) { _, which ->
                    when (which) {
                        0 -> editSelectACategoryTask.text = getString(R.string.reminder)
                        1 -> editSelectACategoryTask.text = getString(R.string.Homework)
                        2 -> editSelectACategoryTask.text = getString(R.string.Teamwork)
                        3 -> editSelectACategoryTask.text = getString(R.string.Project)
                        4 -> editSelectACategoryTask.text = getString(R.string.QuickTest)
                        5 -> editSelectACategoryTask.text = getString(R.string.PartialExam)
                        6 -> editSelectACategoryTask.text = getString(R.string.Exam)
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

    private fun saveChanges() {
        editTaskButton.setOnClickListener {
            editATitle.error = null
            editSelectDeadlineTask.error = null
            editSelectASubjectTask.error = null
            editSelectACategoryTask.error = null

            val editSelectDeadlineError = editSelectDeadlineTask.text.toString() != getString(R.string.selectDeadline)
            val editSelectASubjectError = editSelectASubjectTask.text.toString() != getString(R.string.selectASubject)
            val editSelectACategoryError = editSelectACategoryTask.text.toString() != getString(R.string.selectTheTypeOfTask)

            val category = when(editSelectACategoryTask.text.toString()){
                getString(R.string.reminder) -> "0"
                getString(R.string.Homework) -> "1"
                getString(R.string.Teamwork) -> "2"
                getString(R.string.Project) -> "3"
                getString(R.string.QuickTest) -> "4"
                getString(R.string.PartialExam) -> "5"
                getString(R.string.Exam) -> "6"
                else -> editSelectACategoryTask.text.toString()
            }

            val taskID = getRandomString()
            //var usersLinked: Any?
            val newReference = db.collection("Tasks/$subjectIDNew/tasks").document(taskID)
            if (editATitle.text!!.isNotEmpty() && editSelectDeadlineError && editSelectASubjectError && editSelectACategoryError){
                if (subjectIDNew == subjectIDOld){
                    db.enableNetwork().addOnSuccessListener {
                        reference.update(
                            mapOf(
                                "taskTitle" to editATitle.text.toString(),
                                "subject" to editSelectASubjectTask.text.toString(),
                                "category" to category,
                                "day" to dayTask,
                                "description" to editTaskDescription.text.toString()
                            ))
                        if (navID == R.id.editTaskToSubjectInfo){
                            val subjectID = arguments?.getString("infoSubjectID")
                            val bundle = Bundle()
                            bundle.putString("subjectID", subjectID)
                            showAd()
                            findNavController().navigate(R.id.editTaskToSubjectInfo, bundle)
                        }else{
                            showAd()
                            findNavController().navigate(navID)
                        }
                    }
                }else{
                    val uid = Firebase.auth.uid!!
                    db.enableNetwork().addOnSuccessListener {
                        newReference.set(
                            mapOf(
                                "taskTitle" to editATitle.text.toString(),
                                "category" to category,
                                "taskID" to taskID,
                                "subjectID" to subjectIDNew,
                                "day" to dayTask,
                                "description" to editTaskDescription.text.toString(),
                                "timeLimit" to "23:59",
                                uid to false,
                                "${uid}finishedOn" to ""
                            )
                        )
                        reference.delete()
                        if (navID == R.id.editTaskToSubjectInfo){
                            val subjectID = arguments?.getString("infoSubjectID")
                            val bundle = Bundle()
                            bundle.putString("subjectID", subjectID)
                            showAd()
                            db.waitForPendingWrites().addOnSuccessListener {
                                findNavController().navigate(R.id.editTaskToSubjectInfo, bundle)
                            }
                        }else{
                            showAd()
                            db.waitForPendingWrites().addOnSuccessListener {
                                findNavController().navigate(navID)
                            }
                        }
                    }
                }
            }else{
                if (editATitle.text!!.isEmpty()){
                    editATitle.error = getString(R.string.taskMustHaveATitle)
                }
                if (!editSelectDeadlineError){
                    selectDeadlineTask.error = "Error"
                }
                if (!editSelectASubjectError){
                    selectASubjectTask.error = "Error"
                }
                if (!editSelectACategoryError){
                    selectACategoryTask.error = "Error"
                }
            }
        }
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it ->
        it.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
    }
}