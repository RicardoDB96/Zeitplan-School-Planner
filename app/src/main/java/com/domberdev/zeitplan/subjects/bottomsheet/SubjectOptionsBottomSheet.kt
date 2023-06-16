package com.domberdev.zeitplan.subjects.bottomsheet

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.domberdev.zeitplan.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.subject_options_bottom_sheet.*

class SubjectOptionsBottomSheet(private val onItemClickListener: OnSettingsSubjectListener): BottomSheetDialogFragment() {

    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.subject_options_bottom_sheet, container, false)
    }

    interface OnSettingsSubjectListener{
        fun updateSubjectData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //La acciones de cada uno de los botones
        setCountGeneralGrade()
        val subjectIDV = arguments?.getString("subjectID")!!
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            val reference = db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectIDV)

            val generalGrade = arguments?.getBoolean("generalGrade")!!
            countsForGlobalSubjectBottom.setOnClickListener {
                if (!generalGrade){
                    reference.update("generalGrade", true)
                }else{
                    reference.update("generalGrade", false)
                }
                onItemClickListener.updateSubjectData()
                dismiss()
            }
            editSubjectBottom.setOnClickListener {
                val subjectID = arguments?.getString("subjectID")!!
                val bundle = Bundle()
                bundle.putString("subjectID", subjectID)
                findNavController().navigate(R.id.subjectToEditSubject, bundle)
                dismiss()
            }
            deleteSubject()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setCountGeneralGrade(){
        val generalGrade = arguments?.getBoolean("generalGrade")!!
        val notCountDrawable = requireContext().resources?.getDrawable(R.drawable.not_count_bottomsheet, requireContext().theme)
        val countDrawable = requireContext().resources?.getDrawable(R.drawable.count_bottomsheet, requireContext().theme)

        if(!generalGrade){
            countsForGlobalSubjectBottom.text = getString(R.string.countsForGlobalGrade)
            countsForGlobalSubjectBottom.setCompoundDrawablesRelativeWithIntrinsicBounds(countDrawable, null, null, null)
        }else if (generalGrade){
            countsForGlobalSubjectBottom.text = getString(R.string.doNotCountGeneralGrade)
            countsForGlobalSubjectBottom.setCompoundDrawablesRelativeWithIntrinsicBounds(notCountDrawable, null, null, null)
        }
    }

    private fun deleteSubject(){
        val subjectID = arguments?.getString("subjectID")!!
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        deleteSubjectBottom.setOnClickListener {
            val dialogA = AlertDialog.Builder(requireContext())
            dialogA.setTitle(R.string.deleteSubject)
                .setMessage(R.string.deleteSubjectMessage)
                .setPositiveButton(R.string.Yes)
                { dialog, _ ->
                    FirebaseFirestore.getInstance().enableNetwork().addOnCompleteListener {
                        deleteGrades(subjectID)
                        db.collection("Users").document(userEmail!!).get(Source.CACHE).addOnSuccessListener { user ->
                            val phase = user.getString("Phase")!!
                            val period = user.getString("Period")!!
                            FirebaseFirestore.getInstance()
                                .collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID)
                                .delete()
                        }
                        FirebaseFirestore.getInstance().collection("Tasks").document(subjectID).update("createdBy", "SubjectReadyToDelete")
                    }
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.deleting),
                        Toast.LENGTH_SHORT
                    ).show()
                    onItemClickListener.updateSubjectData()
                    dialog.dismiss()
                    dismiss()
                }
                .setNegativeButton(R.string.No)
                { dialog, _ ->
                    dialog.cancel()
                }.show()
        }
    }

    private fun deleteGrades(subjectID: String){
        FirebaseFirestore.getInstance().collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            FirebaseFirestore.getInstance().collection("Users/$userEmail/Phase$phase/Period$period/Subjects/$subjectID/Grades").get(Source.CACHE).addOnSuccessListener { subject ->
                for (grade in subject){
                    grade.reference.delete()
                }
            }
        }
    }
}