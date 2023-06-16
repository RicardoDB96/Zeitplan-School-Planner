package com.domberdev.zeitplan.agenda.bottomsheet

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
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_task_bottom_sheet.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class TaskOptionsBottomSheetFragment(private val onItemClickListener: OnSettingsTaskListener) : BottomSheetDialogFragment(){

    private val db = FirebaseFirestore.getInstance()
    private val userID = FirebaseAuth.getInstance().currentUser?.uid!!
    private lateinit var taskID: String
    private lateinit var subjectID: String
    private lateinit var reference: DocumentReference
    private var navID: Int = 0
    private var finished by Delegates.notNull<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_bottom_sheet, container, false)
    }

    interface OnSettingsTaskListener{
        fun updateData()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskID = arguments?.getString("taskID")!!
        subjectID = arguments?.getString("subjectID")!!
        reference = db.collection("Tasks/$subjectID/tasks").document(taskID)
        finished = arguments?.getBoolean("finished")!!
        navID = arguments?.getInt("navID")!!

        val checkDrawable = requireContext().resources?.getDrawable(R.drawable.check_bottomsheet, requireContext().theme)
        val uncheckDrawable = requireContext().resources?.getDrawable(R.drawable.uncheck_bottomsheet, requireContext().theme)

        if(!finished){
            markTaskCompleteBottom.text = getString(R.string.markTaskAsCompleted)
            markTaskCompleteBottom.setCompoundDrawablesRelativeWithIntrinsicBounds(checkDrawable, null, null, null)
        }else if (finished){
            markTaskCompleteBottom.text = getString(R.string.markTaskAsNotCompleted)
            markTaskCompleteBottom.setCompoundDrawablesRelativeWithIntrinsicBounds(uncheckDrawable, null, null, null)
        }

        markTaskCompleteBottom.setOnClickListener {
            if (!finished){
                db.enableNetwork().addOnSuccessListener {
                    reference.update(userID, true)

                    val today = Calendar.getInstance().time
                    val todayDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(today)

                    reference.update("${userID}finishedOn", todayDateFormat)}
            }else{
                db.enableNetwork().addOnSuccessListener {
                    reference.update(userID, false)
                    reference.update("${userID}finishedOn", "")}
            }
            onItemClickListener.updateData()
            dismiss()
        }

        editTaskBottom.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("taskID", taskID)
            bundle.putString("subjectID", subjectID)
            bundle.putInt("navID", navID)
            findNavController().navigate(R.id.taskToEditTask, bundle)
            dismiss()
        }

        deleteTaskBottom.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle(getString(R.string.deleteTask))
                .setMessage(getString(R.string.deleteTaskMessage))
                .setPositiveButton(R.string.Yes)
                { dialogP, _ ->
                    db.enableNetwork().addOnSuccessListener {
                        reference.delete()
                    }
                    Toast.makeText(context, getString(R.string.deleting), Toast.LENGTH_SHORT).show()
                    onItemClickListener.updateData()
                    dialogP.dismiss()
                    dismiss()
                }
                .setNegativeButton(R.string.No)
                { dialogN, _ ->
                    dialogN.dismiss()
                }.show()
        }
    }
}