package com.domberdev.zeitplan.grades.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.dialog.DatePickerFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.fragment_edit_grade.*
import java.text.SimpleDateFormat
import java.util.*

class EditGradeFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email!!

    private lateinit var gradeID: String
    private lateinit var subjectID: String
    private lateinit var day: String
    private var dayGrade = String()

    private var interstitial: InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_grade, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editGradeToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        bindData()
        selectDay()
        saveGradeChanges()
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
        gradeID = arguments?.getString("gradeID")!!
        subjectID = arguments?.getString("subjectID")!!

        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects/$subjectID/Grades").document(gradeID).get(Source.CACHE).addOnSuccessListener { grade ->
                addAGradeTitleEdit.setText(grade.getString("gradeTitle")!!)
                addAGradeEdit.setText(grade.getString("grade")!!)
                addAWeightEdit.setText(grade.getString("gradeWeight"))

                val date = SimpleDateFormat("yyyy-MM-dd").parse(grade.getString("gradeDay")!!)
                val day = SimpleDateFormat("EEEE, MMMM d, yyyy").format(date!!).capitalizeWords()

                dayGrade = grade.getString("gradeDay")!!

                this.day = day
                selectDayEditGrade.text = day
            }
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get(Source.CACHE).addOnSuccessListener { subject ->
                selectSubjectEditGrade.text = subject.getString("subjectTitle")
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun selectDay(){
        selectDayEditGrade.setOnClickListener {
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

                dayGrade = "$year-$monthNo-$dayNo"
                val dayToDate = SimpleDateFormat("yyyy-MM-dd").parse(dayGrade)!!
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
                selectDayEditGrade.text = selectedDate
            }
            newFragment.show(parentFragmentManager, "datePicker")
        }
    }

    private fun saveGradeChanges(){
        saveGradeChangesButton.setOnClickListener {
            addAGradeTitleEdit.error = null
            addAGradeEdit.error = null
            addAWeightEdit.error = null

            if(addAGradeTitleEdit.text!!.isNotEmpty() && addAGradeEdit.text!!.isNotEmpty() && addAWeightEdit.text!!.isNotEmpty()){
                if (addAGradeEdit.text!!.toString().toFloat() > 100f || addAGradeEdit.text!!.toString().toFloat() < 0f){
                    addAGradeEdit.error = getString(R.string.wrongGradeErrorMessage)
                    Toast.makeText(requireContext(), getString(R.string.wrongGradeValueMessage), Toast.LENGTH_LONG).show()
                }else{
                    if (addAWeightEdit.text!!.toString().toFloat() > 100f || addAWeightEdit.text!!.toString().toFloat() < 0f){
                        addAWeightEdit.error = getString(R.string.wrongWeightErrorMessage)
                        Toast.makeText(requireContext(), getString(R.string.wrongWeightValueMessage), Toast.LENGTH_LONG).show()
                    }else{
                        db.enableNetwork().addOnCompleteListener {
                            db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                                val phase = user.getString("Phase")!!
                                val period = user.getString("Period")!!
                                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects/$subjectID/Grades").document(gradeID).update(
                                    mapOf(
                                        "gradeTitle" to addAGradeTitleEdit.text!!.toString(),
                                        "grade" to addAGradeEdit.text!!.toString(),
                                        "gradeDay" to dayGrade,
                                        "gradeID" to gradeID,
                                        "subjectID" to subjectID,
                                        "gradeWeight" to addAWeightEdit.text!!.toString(),
                                    ))
                            }
                        }
                        showAd()
                        findNavController().popBackStack()
                    }
                }
            }else{
                if (addAGradeTitleEdit.text!!.isEmpty()){
                    addAGradeTitleEdit.error = getString(R.string.gradeTitleError)
                }
                if (addAGradeEdit.text!!.isEmpty()){
                    addAGradeEdit.error = getString(R.string.gradeErrorMessage)
                }
                if (addAWeightEdit.text!!.isEmpty()){
                    addAWeightEdit.error = getString(R.string.gradeWeightErrorMessage)
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