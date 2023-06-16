package com.domberdev.zeitplan.subjects.ui

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.domberdev.zeitplan.MainActivity
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.agenda.network.TaskViewModel
import com.domberdev.zeitplan.customViews.CircleText
import com.domberdev.zeitplan.dialog.InputTextDialog
import com.domberdev.zeitplan.grades.adapter.SubjectInfoGradeAdapter
import com.domberdev.zeitplan.grades.bottomsheet.GradeInfoFragment
import com.domberdev.zeitplan.grades.network.GradeViewModel
import com.domberdev.zeitplan.subjects.adapter.SubjectInfoTasksAdapter
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_subject_info.*
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.roundToInt

@SuppressLint("UseCompatLoadingForDrawables")
class SubjectInfoFragment : Fragment(), InputTextDialog.OnInputResult, SubjectInfoTasksAdapter.OnSubjectInfoTaskListener, SubjectInfoGradeAdapter.OnGradeClickListener, GradeInfoFragment.OnGradeInfoButton {

    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email!!
    private lateinit var subjectID: String
    private var color: Int = 0

    private lateinit var subjectInfoTaskAdapter: SubjectInfoTasksAdapter
    private val viewModelSubjectInfoTask by lazy { ViewModelProvider(this)[TaskViewModel::class.java] }

    private lateinit var subjectInfoGradeAdapter: SubjectInfoGradeAdapter
    private val viewModelSubjectInfoGrade by lazy { ViewModelProvider(this)[GradeViewModel::class.java] }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeSubjectInfoTaskData(){
        db.disableNetwork().addOnCompleteListener {
            viewModelSubjectInfoTask.fetchUpcomingSubjectInfoTasks(subjectID).observe(viewLifecycleOwner) {
                subjectInfoTaskAdapter.setListData(it)
                subjectInfoTaskAdapter.notifyDataSetChanged()
                if (subjectInfoTaskAdapter.itemCount == 0) {
                    subjectInfoTasksRV.visibility = View.GONE
                    noUpcomingSubjectInfoTasksData.visibility = View.VISIBLE
                } else {
                    subjectInfoTasksRV.visibility = View.VISIBLE
                    noUpcomingSubjectInfoTasksData.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeSubjectInfoGradeData(){
        viewModelSubjectInfoGrade.fetchGradeData(subjectID).observe(viewLifecycleOwner) {
            subjectInfoGradeAdapter.setListData(it)
            subjectInfoGradeAdapter.notifyDataSetChanged()
            if (subjectInfoGradeAdapter.itemCount == 0) {
                subjectInfoGradesRV.visibility = View.GONE
                noUpcomingSubjectInfoGradesData.visibility = View.VISIBLE
                seeAllGradesButton.visibility = View.GONE
                gradeDivider.visibility = View.GONE
            } else {
                subjectInfoGradesRV.visibility = View.VISIBLE
                noUpcomingSubjectInfoGradesData.visibility = View.GONE
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subject_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subjectID = arguments?.getString("subjectID")!!
        subjectInfoToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        noUpcomingSubjectInfoTasksData.visibility = View.GONE
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get(Source.CACHE).addOnSuccessListener {
                val title = it.getString("subjectTitle")
                val acronym = it.getString("subjectAcronym")
                val color = it.getString("color")!!.toInt()
                val secondaryColor = it.getString("secondaryColor")!!.toInt()
                this.color = color

                subjectInfoSubjectAcronym.text = acronym

                subjectInfoCollapsingToolbar.title = title
                subjectInfoCollapsingToolbar.setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
                subjectInfoCollapsingToolbar.setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
                subjectInfoCollapsingToolbar.setExpandedTitleColor(0x00FFFFFF)
                subjectInfoCollapsingToolbar.setCollapsedTitleTextColor(Color.WHITE)
                subjectInfoCollapsingToolbar.setBackgroundColor(secondaryColor)
                subjectInfoTitle.text = title

                appbar.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, i ->
                    setAppBarConfiguration(abs(i / appBarLayout.totalScrollRange.toFloat()))
                })

                (activity as MainActivity).window.statusBarColor = color

                subjectInfoFAB.menuButtonColorNormal = color
                subjectInfoFAB.menuButtonColorPressed = color
                createNewTaskSubjectInfo.colorNormal = color
                createNewTaskSubjectInfo.colorPressed = color
                createNewGradeSubjectInfo.colorNormal = color
                createNewGradeSubjectInfo.colorPressed = color
            }
        }

        monCircle.setCircle(CircleText(ContextCompat.getColor(requireContext(), R.color.backgroundColor), ContextCompat.getColor(requireContext(), R.color.textColor), getString(R.string.Monday)))
        tueCircle.setCircle(CircleText(ContextCompat.getColor(requireContext(), R.color.backgroundColor), ContextCompat.getColor(requireContext(), R.color.textColor), getString(R.string.Tuesday)))
        wedCircle.setCircle(CircleText(ContextCompat.getColor(requireContext(), R.color.backgroundColor), ContextCompat.getColor(requireContext(), R.color.textColor), getString(R.string.Wednesday)))
        thuCircle.setCircle(CircleText(ContextCompat.getColor(requireContext(), R.color.backgroundColor), ContextCompat.getColor(requireContext(), R.color.textColor), getString(R.string.Thursday)))
        friCircle.setCircle(CircleText(ContextCompat.getColor(requireContext(), R.color.backgroundColor), ContextCompat.getColor(requireContext(), R.color.textColor), getString(R.string.Friday)))
        satCircle.setCircle(CircleText(ContextCompat.getColor(requireContext(), R.color.backgroundColor), ContextCompat.getColor(requireContext(), R.color.textColor), getString(R.string.Saturday)))
        sunCircle.setCircle(CircleText(ContextCompat.getColor(requireContext(), R.color.backgroundColor), ContextCompat.getColor(requireContext(), R.color.textColor), getString(R.string.Sunday)))
        val bundle = Bundle()
        bundle.putString("subjectID", subjectID)
        subjectInfoToolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.subjectInfoEdit -> {
                    findNavController().navigate(R.id.subjectInfoToEditSubject, bundle)
                    true
                }
                else -> false
            }
        }
        subjectInfoTaskAdapter = SubjectInfoTasksAdapter(requireContext(), this)
        subjectInfoTasksRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        subjectInfoTasksRV.adapter = subjectInfoTaskAdapter
        subjectInfoGradeAdapter = SubjectInfoGradeAdapter(requireContext(), this)
        subjectInfoGradesRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        subjectInfoGradesRV.adapter = subjectInfoGradeAdapter
        bindData()
        setCurrentGrade()
        fabMenu()
        observeSubjectInfoTaskData()
        observeSubjectInfoGradeData()
        setGradeObjective()
        seeAllGrades()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        val db = Firebase.firestore
        setCurrentGrade()
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                val phase = user.getString("Phase")!!
                val period = user.getString("Period")!!
                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get(Source.CACHE).addOnSuccessListener {
                    val place = when(it.getString("place")!!){
                        "6593632304" -> getString(R.string.online)
                        else -> it.getString("place")!!
                    }

                    val gradeText = it.getString("grade")!!
                    val professor = it.getString("teacher")!!
                    val color = it.getString("color")!!.toInt()
                    this.color = color
                    subjectInfoPlace.text = place
                    subjectInfoProfessor.text = professor

                    val prefs = requireContext().getSharedPreferences("userdata", Context.MODE_PRIVATE).getBoolean("roundGrades", false)

                    val gradeChart = gradeText.replace(',', '.')

                    val grade = if (prefs){
                        gradeChart.toFloat().roundToInt().toString()
                    }else{
                        gradeText
                    }

                    val gradeAnim = if (prefs){
                        gradeChart.toFloat().roundToInt().toFloat()
                    }else{
                        gradeChart.toFloat()
                    }

                    circularProgressBarSubjectInfo.setProgressWithAnimation(gradeAnim, 1000)
                    gradeSubjectInfo.text = grade

                    (activity as MainActivity).window.statusBarColor = color

                    subjectInfoFAB.menuButtonColorNormal = color
                    subjectInfoFAB.menuButtonColorPressed = color
                    createNewTaskSubjectInfo.colorNormal = color
                    createNewTaskSubjectInfo.colorPressed = color
                    createNewGradeSubjectInfo.colorNormal = color
                    createNewGradeSubjectInfo.colorPressed = color

                    circularProgressBarSubjectInfo.progressBarColor = color
                    progressBarObjective.progressTintList = ColorStateList.valueOf(color)
                    val drawableCircleSubject = requireContext().resources?.getDrawable(R.drawable.circle_subject_info, requireContext().theme)!!
                    drawableCircleSubject.setTint(color)
                    db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get(Source.CACHE).addOnSuccessListener { schedules ->
                        if ((schedules.get("scheduleDays") as ArrayList<*>).isNotEmpty()){
                            for (day in schedules.get("scheduleDays") as ArrayList<*>){
                                when(day.toString().toInt()){
                                    0 -> {
                                        monCircle.setCircle(CircleText(color, Color.WHITE, getString(R.string.Monday)))
                                    }
                                    1 -> {
                                        tueCircle.setCircle(CircleText(color, Color.WHITE, getString(R.string.Tuesday)))
                                    }
                                    2 -> {
                                        wedCircle.setCircle(CircleText(color, Color.WHITE, getString(R.string.Wednesday)))
                                    }
                                    3 -> {
                                        thuCircle.setCircle(CircleText(color, Color.WHITE, getString(R.string.Thursday)))
                                    }
                                    4 -> {
                                        friCircle.setCircle(CircleText(color, Color.WHITE, getString(R.string.Friday)))
                                    }
                                    5 -> {
                                        satCircle.setCircle(CircleText(color, Color.WHITE, getString(R.string.Saturday)))
                                        satSunLayout.visibility = View.VISIBLE
                                    }
                                    6 -> {
                                        sunCircle.setCircle(CircleText(color, Color.WHITE, getString(R.string.Sunday)))
                                        satSunLayout.visibility = View.VISIBLE
                                    }
                                }
                            }
                        }
                    }


                    drawableColor(R.drawable.ic_average, color, averageTV)
                    drawableColor(R.drawable.ic_schedule_info, color, taskSubjectInfo)
                    drawableColor(R.drawable.ic_subject_info, color, scheduleSubjectInfo)
                    drawableColor(R.drawable.ic_grades_info, color, gradesSubjectInfoTV)
                    drawableColor(R.drawable.ic_idcode_white, color, shareSubjectInfoTV)
                }
        }
    }

    private fun seeAllGrades(){
        seeAllGradesButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("subjectID", subjectID)
            bundle.putInt("color", color)
            findNavController().navigate(R.id.subjectInfoToGradesFragment, bundle)
        }
    }

    private fun fabMenu(){
        val bundle = Bundle()
        bundle.putString("subjectID", subjectID)
        createNewTaskSubjectInfo.setOnClickListener { findNavController().navigate(R.id.subjectInfoToNewTask, bundle) }
        createNewGradeSubjectInfo.setOnClickListener { findNavController().navigate(R.id.subjectInfoToNewGrade, bundle) }
    }

    private fun setAppBarConfiguration(collapsingPercent: Float){
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get(Source.CACHE).addOnSuccessListener {

                val subjectColor = it.getString("color")!!.toInt()
                val secondaryColor = it.getString("secondaryColor")!!.toInt()

                val color = ColorUtils.blendARGB(secondaryColor, subjectColor, collapsingPercent)
                subjectInfoCollapsingToolbar.setBackgroundColor(color)
            }
        }

        if (collapsingPercent <= .50f){
            subjectInfoTitle.setTextColor(ColorUtils.blendARGB(0xFFFFFFFF.toInt(), 0x00FFFFFF, collapsingPercent * 2))
        }else{
            subjectInfoTitle.setTextColor(0x00FFFFFF)
        }

        subjectInfoSubjectAcronym.setTextColor(ColorUtils.blendARGB(0xFFFFFFFF.toInt(), 0x00FFFFFF, collapsingPercent))
        subjectInfoPlace.setTextColor(ColorUtils.blendARGB(0xFFFFFFFF.toInt(), 0x00FFFFFF, collapsingPercent))
        subjectInfoProfessor.setTextColor(ColorUtils.blendARGB(0xFFFFFFFF.toInt(), 0x00FFFFFF, collapsingPercent))
    }

    private fun bindData(){
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get(Source.CACHE).addOnSuccessListener {
                val color = it.getString("color")!!.toInt()
                drawableColor(R.drawable.ic_average, color, averageTV)
                drawableColor(R.drawable.ic_schedule_info, color, taskSubjectInfo)
                drawableColor(R.drawable.ic_subject_info, color, scheduleSubjectInfo)
                drawableColor(R.drawable.ic_grades_info, color, gradesSubjectInfoTV)
                drawableColor(R.drawable.ic_idcode_white, color, shareSubjectInfoTV)
                circularProgressBarSubjectInfo.progressBarColor = color
                val place = when(it.getString("place")!!){
                    "6593632304"-> getString(R.string.online)
                    else -> it.getString("place")!!
                }
                val professor = it.getString("teacher")!!
                val gradeText = it.getString("grade")!!
                val goal = it.getString("goal")!!

                subjectInfoPlace.text = place
                subjectInfoProfessor.text = professor

                if (goal != "-"){
                    progressBarObjective.max = goal.toInt()
                }else{
                    progressBarObjective.max = 0
                }

                val prefs = requireContext().getSharedPreferences("userdata", Context.MODE_PRIVATE).getBoolean("roundGrades", false)

                val gradeChart = gradeText.replace(',', '.')

                val grade = if (prefs){
                    gradeChart.toFloat().roundToInt().toString()
                }else{
                    gradeText
                }

                val gradeAnim = if (prefs){
                    gradeChart.toFloat().roundToInt().toFloat()
                }else{
                    gradeChart.toFloat()
                }
                progressBarObjective.progressTintList = ColorStateList.valueOf(color)
                circularProgressBarSubjectInfo.setProgressWithAnimation(gradeAnim, 1000)
                gradeSubjectInfo.text = grade
                objectiveInfo.text = goal
                objectiveInfo.text = it.getString("goal")
                val drawableCircleSubject = requireContext().resources?.getDrawable(R.drawable.circle_subject_info, requireContext().theme)!!
                drawableCircleSubject.setTint(color)
                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get(Source.CACHE).addOnSuccessListener { schedules ->
                    if ((schedules.get("scheduleDays") as ArrayList<*>).isNotEmpty()){
                        for (day in schedules.get("scheduleDays") as ArrayList<*>){
                            when(day.toString().toInt()){
                                0 -> {
                                    monCircle.setCircle(CircleText(color, Color.WHITE, getString(R.string.Monday)))
                                }
                                1 -> {
                                    tueCircle.setCircle(CircleText(color, Color.WHITE, getString(R.string.Tuesday)))
                                }
                                2 -> {
                                    wedCircle.setCircle(CircleText(color, Color.WHITE, getString(R.string.Wednesday)))
                                }
                                3 -> {
                                    thuCircle.setCircle(CircleText(color, Color.WHITE, getString(R.string.Thursday)))
                                }
                                4 -> {
                                    friCircle.setCircle(CircleText(color, Color.WHITE, getString(R.string.Friday)))
                                }
                                5 -> {
                                    satCircle.setCircle(CircleText(color, Color.WHITE, getString(R.string.Saturday)))
                                    satSunLayout.visibility = View.VISIBLE
                                }
                                6 -> {
                                    sunCircle.setCircle(CircleText(color, Color.WHITE, getString(R.string.Sunday)))
                                    satSunLayout.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
                subjectIDInfoSubject.text = subjectID
            }
        }
    }

    private fun drawableColor(drawable: Int, color: Int, textView: TextView){
        val drawableIcon = requireContext().resources?.getDrawable(drawable, requireContext().theme)
        drawableIcon?.setTint(color)
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableIcon,null,null,null)
    }

    private fun setGradeObjective(){
        setObjectiveButton.setOnClickListener {
            InputTextDialog(requireContext(), this).showDialog(R.string.setObjective)
        }
    }

    @SuppressLint("Recycle")
    override fun getInputValue(inputValue: String) {
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            val subjectReference = db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID)
            db.enableNetwork().addOnCompleteListener {
                subjectReference.update("goal", inputValue)
                subjectReference.get(Source.CACHE).addOnSuccessListener {
                    val goal = it.getString("goal")!!
                    val grade = it.getString("grade")!!
                    if (goal != "-"){
                        progressBarObjective.max = goal.toInt()
                    }else{
                        progressBarObjective.max = 0
                    }
                    ObjectAnimator.ofInt(progressBarObjective, "progress", grade.toFloat().toInt())
                        .setDuration(1000)
                        .start()
                    objectiveInfo.text = goal
                }
            }
        }
    }

    override fun onSubjectInfoTaskClick(taskID: String, subjectID: String) {
        val bundle = Bundle()
        bundle.putString("taskID", taskID)
        bundle.putString("subjectID", subjectID)
        bundle.putString("infoSubjectID", this.subjectID)
        bundle.putInt("navID", R.id.editTaskToSubjectInfo)
        findNavController().navigate(R.id.subjectInfoToTaskInfo, bundle)
    }

    private fun setCurrentGrade(){
        var subjectPoints = 0.0f
        var weightGlobal = 0.0f
        val db = Firebase.firestore
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects/$subjectID/Grades").get(Source.CACHE).addOnSuccessListener { grades ->
                for(grade in grades){
                    val gradeV = grade.getString("grade")!!.toFloat()
                    val gradeWeight = grade.getString("gradeWeight")!!.toInt()
                    weightGlobal += gradeWeight
                    subjectPoints += gradeV * gradeWeight / 100
                }
                val subjectGrade = subjectPoints / weightGlobal * 100
                if (!subjectGrade.isNaN()){
                    circularProgressBarSubjectInfo.setProgressWithAnimation(subjectGrade, 1000)
                    val df = DecimalFormat("#.##")
                    df.roundingMode = RoundingMode.CEILING
                    db.enableNetwork().addOnCompleteListener {
                        db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).update("grade", df.format(subjectGrade).toString())
                    }
                    gradeSubjectInfo.text = df.format(subjectGrade).toString()
                    ObjectAnimator.ofInt(progressBarObjective, "progress", subjectGrade.toInt())
                        .setDuration(1000)
                        .start()
                }else{
                    if (grades.isEmpty){
                        db.enableNetwork().addOnCompleteListener {
                            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).update("grade", "0")
                        }
                        gradeSubjectInfo.text = "0"
                        ObjectAnimator.ofInt(progressBarObjective, "progress", 0)
                            .setDuration(1000)
                            .start()
                        circularProgressBarSubjectInfo.setProgressWithAnimation(0f, 1000)
                    }
                }
            }
        }
    }

    override fun onGradeClick(gradeID: String, subjectID: String) {
        val bottomSheetFragment = GradeInfoFragment(this)
        val bundle = Bundle()
        bundle.putString("gradeID", gradeID)
        bundle.putString("subjectID", subjectID)
        bundle.putInt("color", color)
        bundle.putInt("destination", R.id.subjectInfoToEditGrade)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }

    override fun onGradeLongClick(gradeID: String, subjectID: String) {
        val bottomSheetFragment = GradeInfoFragment(this)
        val bundle = Bundle()
        bundle.putString("gradeID", gradeID)
        bundle.putString("subjectID", subjectID)
        bundle.putInt("color", color)
        bundle.putInt("destination", R.id.subjectInfoToEditGrade)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }

    override fun onDeleteClick() {
        observeSubjectInfoGradeData()
        setCurrentGrade()
    }
}