package com.domberdev.zeitplan.schedule.bottomsheet

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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.fragment_schedule_info.*

class ScheduleInfoFragment(private val onScheduleInfoButton: OnScheduleInfoButton) : BottomSheetDialogFragment() {

    private val userEmail = FirebaseAuth.getInstance().currentUser?.email!!
    private val db = FirebaseFirestore.getInstance()
    private lateinit var scheduleID: String
    private lateinit var subjectID: String

    interface OnScheduleInfoButton{
        fun onDeleteClick()
        fun callEdit(bundle: Bundle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindData()
        deleteSchedule()
        editScheduleInfo()
        scheduleSubjectTitle.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("subjectID", subjectID)
            findNavController().navigate(R.id.scheduleToSubjectInfo, bundle)
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun bindData(){
        scheduleID = arguments?.getString("scheduleID")!!
        subjectID = arguments?.getString("subjectID")!!
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get(Source.CACHE).addOnSuccessListener { subject ->
                scheduleSubjectTitle.text = subject.getString("subjectTitle")
                schedulePlace.text = when(subject.getString("place")){
                    "6593632304" -> getString(R.string.online)
                    else -> subject.getString("place")
                }
            }
            db.collection("Users/$userEmail/Phase$phase/Period$period/Schedules").document(scheduleID).get(Source.CACHE).addOnSuccessListener { schedule ->
                val color = schedule.getString("color")!!.toInt()
                val placeDrawable = requireContext().getDrawable(R.drawable.ic_place_schedule)
                placeDrawable?.setTint(color)

                scheduleSubjectTitle.setTextColor(color)

                scheduleDay.text = when(schedule.getString("scheduleDay")!!.toInt()){
                    0 -> getString(R.string.MondayC)
                    1 -> getString(R.string.TuesdayC)
                    2 -> getString(R.string.WednesdayC)
                    3 -> getString(R.string.ThursdayC)
                    4 -> getString(R.string.FridayC)
                    5 -> getString(R.string.SaturdayC)
                    6 -> getString(R.string.SundayC)
                    else -> "Error"
                }

                startTimeSchedule.text = " ${schedule.getString("startTime12")} "
                startTimeSchedule.setTextColor(color)
                endTimeSchedule.text = " ${schedule.getString("endTime12")}"
                endTimeSchedule.setTextColor(color)

                scheduleDeleteButtonCard.setCardBackgroundColor(color)
                scheduleEditButtonCard.setCardBackgroundColor(color)
                schedulePlace.setCompoundDrawablesRelativeWithIntrinsicBounds(placeDrawable, null, null, null)
            }
        }
    }

    private fun deleteSchedule(){
        scheduleDeleteButton.setOnClickListener {
            db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                val phase = user.getString("Phase")!!
                val period = user.getString("Period")!!
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(R.string.deleteClassSchedule)
                builder.setMessage(R.string.deleteClassScheduleMessage)
                builder.setPositiveButton(R.string.Yes){ dialog, _ ->
                    db.collection("Users/$userEmail/Phase$phase/Period$period/Schedules").document(scheduleID).get(Source.CACHE).addOnSuccessListener {
                        db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).update("scheduleDays", FieldValue.arrayRemove(it.getString("scheduleDay")))
                    }
                    db.collection("Users/$userEmail/Phase$phase/Period$period/Schedules").document(scheduleID).delete()
                    Toast.makeText(requireContext(), getString(R.string.deleting), Toast.LENGTH_SHORT).show()
                    onScheduleInfoButton.onDeleteClick()
                    dialog.dismiss()
                    dismiss()
                }
                builder.setNegativeButton(R.string.No){ dialog, _ ->
                    dialog.dismiss()
                }
                builder.show()
            }
        }
    }

    private fun editScheduleInfo(){
        scheduleEditButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("scheduleID", scheduleID)
            bundle.putString("subjectID", subjectID)
            onScheduleInfoButton.callEdit(bundle)
            dismiss()
        }
    }
}