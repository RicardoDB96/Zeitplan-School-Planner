package com.domberdev.zeitplan.agenda.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.domberdev.zeitplan.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.fragment_task_info.*
import java.text.SimpleDateFormat
import java.util.*

class TaskInfoFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val userID = FirebaseAuth.getInstance().currentUser?.uid!!
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email!!
    private lateinit var taskID: String
    private lateinit var subjectID: String
    private var infoSubjectID: String? = null
    private var navID: Int = 0
    private lateinit var reference: DocumentReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskID = arguments?.getString("taskID")!!
        subjectID = arguments?.getString("subjectID")!!
        navID = arguments?.getInt("navID")!!
        infoSubjectID = arguments?.getString("infoSubjectID", null)
        reference = db.collection("Tasks/$subjectID/tasks").document(taskID)
        taskInfoToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        taskInfoToolbar.inflateMenu(R.menu.task_info_options)
        taskInfoToolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.editTaskAppbar ->{
                    val bundle = Bundle()
                    bundle.putString("taskID", taskID)
                    bundle.putString("subjectID", subjectID)
                    bundle.putInt("navID", navID)
                    if (infoSubjectID != null){
                        bundle.putString("infoSubjectID", infoSubjectID)
                    }
                    findNavController().navigate(R.id.infoTaskToEditTask, bundle)
                    true
                }
                R.id.deleteTaskAppbar -> {
                    val dialog = AlertDialog.Builder(requireContext())
                    dialog.setTitle(getString(R.string.deleteTask))
                        .setMessage(getString(R.string.deleteTaskMessage))
                        .setPositiveButton(R.string.Yes)
                        { dialogA, _ ->
                            db.enableNetwork().addOnSuccessListener {
                                reference.delete()
                            }
                            Toast.makeText(requireContext(), getString(R.string.deleting), Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                            dialogA.dismiss()
                        }
                        .setNegativeButton(R.string.No)
                        { dialogA, _ ->
                            dialogA.cancel()
                        }.show()
                    true
                }
                else -> false
            }
        }
        completeIncompleteTaskChange()
        bindData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.task_info_options, menu)
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it ->
        it.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun completeIncompleteTaskChange(){
        completeTaskButton.setOnClickListener {
            when(completeTaskButton.text){
                getString(R.string.markTaskAsCompleted) -> {reference.update(userID, true)

                    val today = Calendar.getInstance().time
                    val todayDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(today)

                    reference.update("${userID}finishedOn", todayDateFormat)}
                getString(R.string.markTaskAsNotCompleted) -> {reference.update(userID, false)
                    reference.update("${userID}finishedOn", "")}
            }
            findNavController().popBackStack()
        }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun bindData(){
        reference.get(Source.CACHE).addOnSuccessListener {
            db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                val phase = user.getString("Phase")!!
                val period = user.getString("Period")!!
                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get(Source.CACHE).addOnSuccessListener { subject ->
                    infoTaskSubject.text = subject.getString("subjectTitle")
                    infoTaskSubjectCircleColor.circleBackgroundColor = subject.getString("color")!!.toInt()
                }
            }

            val taskTittle = it.getString("taskTitle")!!

            val date = SimpleDateFormat("yyyy-MM-dd").parse(it.getString("day")!!)
            val day = SimpleDateFormat("EEEE, MMMM d, yyyy").format(date!!)

            val deadline = day.capitalizeWords()
            val finishedOn = it.getString("${userID}finishedOn")!!
            val category = when(it.getString("category")!!){
                "0" -> getString(R.string.reminder)
                "1" -> getString(R.string.Homework)
                "2" -> getString(R.string.Teamwork)
                "3" -> getString(R.string.Project)
                "4" -> getString(R.string.QuickTest)
                "5" -> getString(R.string.PartialExam)
                "6" -> getString(R.string.Exam)
                else -> it.getString("category")!!
            }
            val taskComplete = it.getBoolean(userID)
            val description = it.getString("description")!!

            infoTaskTitle.text = taskTittle
            infoTaskDeadline.text = deadline
            infoTaskCategory.text = category

            if (finishedOn == ""){
                infoFinishedOn.visibility = View.GONE
            }else{
                infoFinishedOn.visibility = View.VISIBLE
                try {
                    val finishDate = SimpleDateFormat("yyyy-MM-dd").parse(finishedOn)!!
                    val finishDay = SimpleDateFormat("EEEE, MMMM d, yyyy").format(finishDate).capitalizeWords()
                    infoFinishedOn.text = "${getString(R.string.finishOn)} $finishDay"
                }catch (e: Exception){
                    infoFinishedOn.text = finishedOn
                }
            }

            if (description == ""){
                infoTaskDescription.visibility = View.GONE
                infoTaskDescriptionTitle.visibility = View.GONE
                categoryDescriptionLine.visibility = View.GONE
            }else{
                infoTaskDescription.visibility = View.VISIBLE
                infoTaskDescriptionTitle.visibility = View.VISIBLE
                categoryDescriptionLine.visibility = View.VISIBLE
                infoTaskDescription.text = description
            }

            if(taskComplete == false){
                completeTaskButton.text = getString(R.string.markTaskAsCompleted)
            }else if (taskComplete == true){
                completeTaskButton.text = getString(R.string.markTaskAsNotCompleted)
            }
        }
    }
}