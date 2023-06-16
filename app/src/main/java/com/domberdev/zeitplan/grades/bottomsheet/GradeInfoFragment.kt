package com.domberdev.zeitplan.grades.bottomsheet

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.domberdev.zeitplan.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.fragment_grade_info.*
import java.text.SimpleDateFormat
import java.util.*

class GradeInfoFragment(private val onGradeInfoButton: OnGradeInfoButton) : BottomSheetDialogFragment() {

    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email!!

    private lateinit var gradeID: String
    private lateinit var subjectID: String
    private var destination: Int = 0
    private var color: Int = 0

    interface OnGradeInfoButton{
        fun onDeleteClick()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grade_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindData()
        deleteGrade()
        editGrade()
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun bindData() {
        gradeID = arguments?.getString("gradeID")!!
        subjectID = arguments?.getString("subjectID")!!
        color = arguments?.getInt("color")!!
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects/$subjectID/Grades").document(gradeID).get(Source.CACHE).addOnSuccessListener { grade ->
                gradeInfoTitle.text = grade.getString("gradeTitle")
                gradeInfoTitle.setTextColor(color)
                gradeInfoGrade.text = grade.getString("grade")

                val date = SimpleDateFormat("yyyy-MM-dd").parse(grade.getString("gradeDay")!!)
                val day = SimpleDateFormat("MMMM d, yyyy").format(date!!).capitalizeWords()

                gradeInfoDate.text = day
                val gradeV = grade.getString("gradeWeight")
                gradeInfoWeight.text = "$gradeV%"
            }
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get(Source.CACHE).addOnSuccessListener {
                gradeInfoSubject.text = it.getString("subjectTitle")
            }
        }
        drawableColor(R.drawable.ic_grade_info, color, gradeInfoGrade)
        drawableColor(R.drawable.ic_subject_grade_info, color, gradeInfoSubject)
        drawableColor(R.drawable.ic_date_info, color, gradeInfoDate)
        drawableColor(R.drawable.ic_weight_info, color, gradeInfoWeight)
        gradeDeleteButtonCard.setCardBackgroundColor(color)
        gradeEditButtonCard.setCardBackgroundColor(color)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun drawableColor(drawable: Int, color: Int, textView: TextView){
        val drawableIcon = requireContext().resources?.getDrawable(drawable, requireContext().theme)
        drawableIcon?.setTint(color)
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableIcon,null,null,null)
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it ->
        it.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
    }

    private fun deleteGrade(){
        gradeDeleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(R.string.deleteGrade)
            builder.setMessage(R.string.deleteGradeMessage)
            builder.setPositiveButton(R.string.Yes){ dialog, _ ->
                db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                    val phase = user.getString("Phase")!!
                    val period = user.getString("Period")!!
                    db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects/$subjectID/Grades").document(gradeID).delete()
                }
                Toast.makeText(requireContext(), getString(R.string.deleting), Toast.LENGTH_SHORT).show()
                onGradeInfoButton.onDeleteClick()
                dialog.dismiss()
                dismiss()
            }
            builder.setNegativeButton(R.string.No){ dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun editGrade(){
        destination = arguments?.getInt("destination")!!
        gradeEditButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("gradeID", gradeID)
            bundle.putString("subjectID", subjectID)
            findNavController().navigate(destination, bundle)
            dismiss()
        }
    }
}