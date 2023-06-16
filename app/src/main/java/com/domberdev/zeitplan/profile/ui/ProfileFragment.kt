package com.domberdev.zeitplan.profile.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.profile.adapter.SubjectsWithGradesAdapter
import com.domberdev.zeitplan.subjects.network.SubjectViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.File
import kotlin.math.roundToInt

class ProfileFragment : Fragment() {

    private lateinit var adapter: SubjectsWithGradesAdapter
    private val viewModel by lazy { ViewModelProvider(this)[SubjectViewModel::class.java] }
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email!!
    private val uid = Firebase.auth.currentUser?.uid!!
    private val photoUri = FirebaseAuth.getInstance().currentUser?.photoUrl

    private val storageRef = Firebase.storage.reference

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun observeData() {
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").whereEqualTo("generalGrade", true).get(Source.CACHE).addOnSuccessListener { subjects ->
                if (subjects.size() != 0){
                    viewModel.fetchSubjectGradeData().observe(viewLifecycleOwner) {
                        adapter.setListData(it)
                        adapter.notifyDataSetChanged()
                        val gradeChart = adapter.setCurrentGrade().replace(',', '.')
                        gradeProfile.text = "${adapter.setCurrentGrade()}/100"
                        gradeCircularBar.setProgressWithAnimation(gradeChart.toFloat(), 1000)
                        subjectsWithGradesRV.visibility = View.VISIBLE
                        noSubjectTVProfile.visibility = View.GONE
                        adapter.setCurrentGradePure()
                        db.enableNetwork().addOnCompleteListener {
                            if (adapter.setCurrentGradePure().isNotEmpty() || adapter.setCurrentGradeRound().isNotEmpty()) {
                                db.collection("Users/$userEmail/Phase$phase")
                                    .document("Period$period")
                                    .update("periodGrade", adapter.setCurrentGradePure())
                                    .addOnFailureListener {
                                        db.collection("Users/$userEmail/Phase$phase")
                                            .document("Period$period").set(
                                            mapOf("periodGrade" to adapter.setCurrentGradePure())
                                        )
                                    }
                                db.collection("Users/$userEmail/Phase$phase")
                                    .document("Period$period")
                                    .update("periodGradeRound", adapter.setCurrentGradeRound())
                                    .addOnFailureListener {
                                        db.collection("Users/$userEmail/Phase$phase")
                                            .document("Period$period").set(
                                            mapOf("periodGradeRound" to adapter.setCurrentGradeRound())
                                        )
                                    }
                            } else {
                                db.collection("Users/$userEmail/Phase$phase")
                                    .document("Period$period").update("periodGrade", "0")
                                    .addOnFailureListener {
                                        db.collection("Users/$userEmail/Phase$phase")
                                            .document("Period$period").set(
                                            mapOf("periodGrade" to "0")
                                        )
                                    }
                                db.collection("Users/$userEmail/Phase$phase")
                                    .document("Period$period").update("periodGradeRound", "0")
                                    .addOnFailureListener {
                                        db.collection("Users/$userEmail/Phase$phase")
                                            .document("Period$period").set(
                                            mapOf("periodGradeRound" to "0")
                                        )
                                    }
                            }
                        }
                    }
                }else{
                    db.enableNetwork().addOnCompleteListener {
                        db.collection("Users/$userEmail/Phase$phase").document("Period$period").update("periodGrade", "0").addOnFailureListener {
                            db.collection("Users/$userEmail/Phase$phase").document("Period$period").set(
                                mapOf("periodGrade" to "0")
                            )}
                        db.collection("Users/$userEmail/Phase$phase").document("Period$period").update("periodGradeRound", "0").addOnFailureListener {
                            db.collection("Users/$userEmail/Phase$phase").document("Period$period").set(
                                mapOf("periodGradeRound" to "0")
                            )}
                    }
                    subjectsWithGradesRV.visibility = View.GONE
                    noSubjectTVProfile.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileToolbar.inflateMenu(R.menu.profile_options)
        profileToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.configurationAppbar -> {
                    findNavController().navigate(R.id.profileToConfiguration)
                    true
                }
                else -> false
            }
        }
        adapter = SubjectsWithGradesAdapter(requireContext())
        subjectsWithGradesRV.layoutManager = LinearLayoutManager(context)
        subjectsWithGradesRV.adapter = adapter
        observeData()
        bindUserData()
        objectiveChartData()
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val current = user.getString("Current")!!.toInt()
            val duration = user.getString("Duration")!!.toInt().plus(1)

            val periodType = when(user.getString("Type")!!.toInt()){
                0 -> getString(R.string.nextBimester)
                1 -> getString(R.string.nextTrimester)
                2 -> getString(R.string.nextFourPeriod)
                3 -> getString(R.string.nextSemester)
                4 -> getString(R.string.nextYear)
                else -> getString(R.string.nextSemester)
            }
            passPeriodButton.text = periodType
            if (current >= duration){
                passPeriodButton.visibility = View.GONE
                passPeriodDivider.visibility = View.GONE
            }
        }
        bindAcademicInfo()
        nextSemester()
    }

    private fun bindUserData() {
        val username = FirebaseAuth.getInstance().currentUser?.displayName
        profileName.text = username
        val profilePhoto = File(requireContext().cacheDir, "profilePicture.jpg")
        if (profilePhoto.exists() && photoUri != null){
            profileImage.setImageURI(photoUri)
        }else{
            val userImageRef = storageRef.child("Users/$uid/profile")
            userImageRef.getFile(Uri.fromFile(File(requireContext().cacheDir, "profilePicture.jpg"))).addOnSuccessListener {
                if (photoUri != null && this.isVisible && profilePhoto.exists()){
                    profileImage.setImageURI(profilePhoto.toUri())

                    auth.currentUser.let {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.fromFile(File(requireContext().cacheDir, "profilePicture.jpg")))
                            .build()

                        auth.currentUser?.updateProfile(profileUpdates)
                    }
                }
            }.addOnFailureListener {
                Log.d("Error", it.message!!)
            }
        }
    }

    private fun objectiveChartData(){
        objectivesChart.description = null
        objectivesChart.setPinchZoom(false)
        objectivesChart.legend.isEnabled = false

        val axisLeft = objectivesChart.axisLeft
        val axisRight = objectivesChart.axisRight

        axisLeft.isEnabled = true
        axisLeft.axisMinimum = 0f
        axisLeft.axisMaximum = 100f
        axisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.textColor)
        axisLeft.textSize = 12f

        axisRight.isEnabled = false
        axisRight.axisMinimum = 0f

        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").orderBy("subjectTitle", Query.Direction.ASCENDING).whereEqualTo("generalGrade", true).get(Source.CACHE).addOnSuccessListener { result ->
                if (result.size() != 0){
                    axisRight.axisMaximum = result.documents.size.toFloat()
                    val subjects = arrayListOf<String>()
                    val grades = arrayListOf<BarEntry>()
                    val objectives = arrayListOf<BarEntry>()
                    val colors = arrayListOf<Int>()
                    val secondaryColors = arrayListOf<Int>()
                    for ((i, subject) in result.withIndex()){
                        if (subject.getString("goal") != "-"){
                            val subjectAcronym = subject.getString("subjectAcronym")!!
                            val gradeText = subject.getString("grade")!!
                            val objective = subject.getString("goal")!!.toFloat()
                            val color = subject.getString("color")!!.toInt()
                            val secondaryColor = subject.getString("secondaryColor")!!.toInt()

                            val prefs = requireContext().getSharedPreferences("userdata", Context.MODE_PRIVATE).getBoolean("roundGrades", false)

                            val gradeChart = gradeText.replace(',', '.')

                            val grade = if (prefs){
                                gradeChart.toFloat().roundToInt().toFloat()
                            }else{
                                gradeChart.toFloat()
                            }
                            subjects.add(subjectAcronym)

                            grades.add(BarEntry(i.toFloat(), grade))
                            objectives.add(BarEntry(i.toFloat(), objective))

                            colors.add(color)
                            secondaryColors.add(secondaryColor)
                        }
                    }

                    if (grades.isNotEmpty() || objectives.isNotEmpty()){
                        val gradesSet = BarDataSet(grades, "Grades")
                        val objectivesSet = BarDataSet(objectives, "Objectives")

                        gradesSet.colors = secondaryColors
                        objectivesSet.colors = colors

                        val groupSpace = 0.06f
                        val barSpace = 0.02f // x2 dataset

                        val barWidth = 0.45f // x2 dataset

                        val data = BarData(gradesSet, objectivesSet)

                        data.setDrawValues(false)
                        data.barWidth = barWidth

                        val xAxis = objectivesChart.xAxis
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textColor)
                        xAxis.valueFormatter = IndexAxisValueFormatter(subjects)
                        xAxis.labelCount = subjects.size
                        xAxis.textSize = 12f
                        xAxis.setDrawGridLines(false)

                        objectivesChart.extraBottomOffset = 4f

                        objectivesChart.data = data
                        objectivesChart.setDrawValueAboveBar(false)
                        objectivesChart.data.isHighlightEnabled = false
                        objectivesChart.setScaleEnabled(false)
                        objectivesChart.setFitBars(true)
                        objectivesChart.groupBars(-0.5f, groupSpace, barSpace) // perform the "explicit" grouping
                        objectivesChart.invalidate() // refresh
                    }else{
                        objectivesChart.visibility = View.GONE
                        noObjectivesTV.visibility = View.VISIBLE
                    }
                }else{
                    objectivesChart.visibility = View.GONE
                    noObjectivesTV.visibility = View.VISIBLE
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindAcademicInfo(){
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val university = user.getString("University")
            val career = user.getString("Career")
            val duration = user.getString("Duration")!!.toInt() + 1
            val periodType = when(user.getString("Type")!!.toInt()){
                0 -> getString(R.string.currentBimester)
                1 -> getString(R.string.currentTrimester)
                2 -> getString(R.string.currentFourPeriod)
                3 -> getString(R.string.currentSemester)
                4 -> getString(R.string.currentYear)
                else -> getString(R.string.currentSemester)
            }
            val currentPeriod = user.getString("Current")!!.toInt()

            universityProfile.text = university
            careerProfile.text = career
            currentPeriodProfile.text = "$periodType: $currentPeriod/$duration"

            careerProgress.text = "$currentPeriod/$duration"
            careerProgressCircularBar.progressMax = duration.toFloat()
            careerProgressCircularBar.setProgressWithAnimation(currentPeriod.toFloat(), 1000)
        }
    }

    private fun nextSemester(){
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val period = user.getString("Period")!!.toInt()
            val current = user.getString("Current")!!.toInt()

            if (period < current){
                passPeriodButton.visibility = View.GONE
                passPeriodDivider.visibility = View.GONE
            }

            passPeriodButton.setOnClickListener {
                val periodType = when(user.getString("Type")!!.toInt()){
                    0 -> getString(R.string.nextBimesterTitle)
                    1 -> getString(R.string.nextTrimesterTitle)
                    2 -> getString(R.string.nextFourPeriodTitle)
                    3 -> getString(R.string.nextSemesterTitle)
                    4 -> getString(R.string.nextYeatTitle)
                    else -> getString(R.string.nextSemesterTitle)
                }

                val periodTypeM = when(user.getString("Type")!!.toInt()){
                    0 -> getString(R.string.bimester)
                    1 -> getString(R.string.trimester)
                    2 -> getString(R.string.fourMonthPeriod)
                    3 -> getString(R.string.semesters)
                    4 -> getString(R.string.year)
                    else -> getString(R.string.semesters)
                }

                val dialog = AlertDialog.Builder(requireContext())

                dialog.setTitle(periodType)
                dialog.setMessage("${getString(R.string.nextPeriodMessage)}${periodTypeM.dropLast(1).lowercase()}.")
                dialog.setPositiveButton(R.string.ok) { dialogI, _ ->
                    db.enableNetwork().addOnCompleteListener {
                        db.collection("Users").document(userEmail).update("Period", period.plus(1).toString())
                        db.collection("Users").document(userEmail).update("Current", current.plus(1).toString())
                    }
                    dialogI.dismiss()
                    findNavController().navigate(R.id.newPeriodAction)
                }
                dialog.setNegativeButton(R.string.cancel) { dialogI, _ ->
                    dialogI.dismiss()
                }
                dialog.show()
            }
        }
    }
}