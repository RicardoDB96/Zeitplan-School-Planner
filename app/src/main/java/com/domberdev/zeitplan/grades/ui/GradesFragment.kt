package com.domberdev.zeitplan.grades.ui

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.domberdev.zeitplan.MainActivity
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.grades.adapter.SubjectInfoGradeAdapter
import com.domberdev.zeitplan.grades.bottomsheet.GradeInfoFragment
import com.domberdev.zeitplan.grades.network.GradeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.fragment_grades.*
import java.math.RoundingMode
import java.text.DecimalFormat

class GradesFragment : Fragment(), SubjectInfoGradeAdapter.OnGradeClickListener, GradeInfoFragment.OnGradeInfoButton {

    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email!!

    private lateinit var subjectID: String
    private var color: Int = 0

    private lateinit var subjectGradesAdapter: SubjectInfoGradeAdapter
    private val viewModelSubjectInfoGrade by lazy { ViewModelProvider(this).get(GradeViewModel::class.java) }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeGradesData(){
        viewModelSubjectInfoGrade.fetchGradesData(subjectID).observe(viewLifecycleOwner) {
            subjectGradesAdapter.setListData(it)
            subjectGradesAdapter.notifyDataSetChanged()
            if (subjectGradesAdapter.itemCount == 0) {
                gradesRV.visibility = View.GONE
            } else {
                gradesRV.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grades, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gradesToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        bindData()
        subjectGradesAdapter = SubjectInfoGradeAdapter(requireContext(), this)
        gradesRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        gradesRV.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        gradesRV.adapter = subjectGradesAdapter
        observeGradesData()
        setCurrentGradeInformation()
        val bundle = Bundle()
        bundle.putString("subjectID", subjectID)
        addGradeFAB.setOnClickListener { findNavController().navigate(R.id.gradesFragmentToNewGrade, bundle) }
    }

    private fun bindData(){
        subjectID = arguments?.getString("subjectID")!!
        color = arguments?.getInt("color")!!

        (activity as MainActivity).window.statusBarColor = color
        gradesToolbar.setBackgroundColor(color)
        addGradeFAB.backgroundTintList = ColorStateList.valueOf(color)
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get(Source.CACHE).addOnSuccessListener { subject ->
                gradesToolbar.subtitle = subject.getString("subjectTitle")!!
            }
        }
    }

    private fun setCurrentGradeInformation(){
        var subjectPoints = 0.0f
        var weightGlobal = 0.0f
        db.disableNetwork().addOnCompleteListener {
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
                        val df = DecimalFormat("#.##")
                        df.roundingMode = RoundingMode.CEILING
                        db.enableNetwork().addOnCompleteListener {
                            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).update("grade", df.format(subjectGrade).toString())
                        }
                        currentSubjectGrade.text = df.format(subjectGrade).toString()
                        currentSubjectTotalPoints.text = df.format(weightGlobal).toString()
                        currentSubjectPoints.text = df.format(subjectPoints).toString()
                    }else{
                        db.enableNetwork().addOnCompleteListener {
                            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).update("grade", "0")
                        }
                        currentSubjectGrade.text = "0"
                        currentSubjectTotalPoints.text = "0"
                        currentSubjectPoints.text = "0"
                    }
                }
            }
        }
        db.enableNetwork()
    }

    override fun onGradeClick(gradeID: String, subjectID: String) {
        val bottomSheetFragment = GradeInfoFragment(this)
        val bundle = Bundle()
        bundle.putString("gradeID", gradeID)
        bundle.putString("subjectID", subjectID)
        bundle.putInt("color", color)
        bundle.putInt("destination", R.id.gradesFragmentToEditGrade)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }

    override fun onGradeLongClick(gradeID: String, subjectID: String) {
        val bottomSheetFragment = GradeInfoFragment(this)
        val bundle = Bundle()
        bundle.putString("gradeID", gradeID)
        bundle.putString("subjectID", subjectID)
        bundle.putInt("color", color)
        bundle.putInt("destination", R.id.gradesFragmentToEditGrade)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }

    override fun onDeleteClick() {
        observeGradesData()
        setCurrentGradeInformation()
    }
}