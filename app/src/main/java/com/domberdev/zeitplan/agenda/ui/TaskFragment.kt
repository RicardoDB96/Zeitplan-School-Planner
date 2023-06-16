package com.domberdev.zeitplan.agenda.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.agenda.adapter.TaskAdapter
import com.domberdev.zeitplan.agenda.bottomsheet.TaskOptionsBottomSheetFragment
import com.domberdev.zeitplan.agenda.network.TaskViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_task.*

class TaskFragment : Fragment(), TaskAdapter.OnTaskClickListener, TaskOptionsBottomSheetFragment.OnSettingsTaskListener {

    private val db = FirebaseFirestore.getInstance()

    private val userEmail = FirebaseAuth.getInstance().currentUser?.email

    private lateinit var adapter: TaskAdapter
    private val viewModel by lazy { ViewModelProvider(this)[TaskViewModel::class.java] }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeData(){
        val db = Firebase.firestore
        noTaskData.visibility = View.GONE
        shimmerTask.showShimmer(true)
        db.collection("Users").document(userEmail!!).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").get(Source.CACHE).addOnSuccessListener { subjects ->
                if (subjects.size() != 0){
                    viewModel.fetchTaskData(requireContext()).observe(viewLifecycleOwner) {
                        shimmerTask.stopShimmer()
                        shimmerTask.showShimmer(false)
                        shimmerTask.visibility = View.GONE
                        adapter.setListData(it)
                        adapter.notifyDataSetChanged()
                        if (adapter.itemCount == 0) {
                            noTaskData.visibility = View.VISIBLE
                        }else {
                            noTaskData.visibility = View.GONE
                        }
                    }
                }else{
                    shimmerTask.stopShimmer()
                    shimmerTask.showShimmer(false)
                    shimmerTask.visibility = View.GONE
                    noTaskData.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val showTask = this.activity?.getSharedPreferences("userdata", Context.MODE_PRIVATE)
        if(showTask?.getBoolean("showTasks", false) == true){
            taskToolbar.menu[0].isChecked = true
        }else if (showTask?.getBoolean("showTasks", false) == false){
            taskToolbar.menu[0].isChecked = false
        }
        taskToolbar.setOnMenuItemClickListener {
            if (showTask?.getBoolean("showTasks", false) == true){
                showTask.edit()?.putBoolean("showTasks", false)?.apply()
                observeData()
                it.isChecked = false
            }else{
                showTask?.edit()?.putBoolean("showTasks", true)?.apply()
                observeData()
                it.isChecked = true
            }
            false
        }

        noTaskData.visibility = View.GONE

        addTaskFAB.setOnClickListener {
            findNavController().navigate(R.id.taskToAddTask)
        }

        adapter = TaskAdapter(requireContext(), this)
        taskRV.layoutManager = LinearLayoutManager(context)
        taskRV.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
        taskRV.adapter = adapter
        observeData()
    }

    override fun onTaskClick(taskID: String, subjectID: String) {
        val bundle = Bundle()
        bundle.putString("taskID", taskID)
        bundle.putString("subjectID", subjectID)
        bundle.putInt("navID", R.id.editTaskToTaskFragment)
        findNavController().navigate(R.id.taskToInfoTask, bundle)
    }

    override fun onTaskLongClick(finished: Boolean, taskID: String, subjectID: String) {
        val bottomSheetFragment = TaskOptionsBottomSheetFragment(this)
        val bundle = Bundle()
        bundle.putString("taskID", taskID)
        bundle.putString("subjectID", subjectID)
        bundle.putBoolean("finished", finished)
        bundle.putInt("navID", R.id.editTaskToTaskFragment)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
    }

    override fun onSettingsClick(finished: Boolean, taskID: String, subjectID: String) {
        val bottomSheetFragment = TaskOptionsBottomSheetFragment(this)
        val bundle = Bundle()
        bundle.putString("taskID", taskID)
        bundle.putString("subjectID", subjectID)
        bundle.putBoolean("finished", finished)
        bundle.putInt("navID", R.id.editTaskToTaskFragment)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
    }

    override fun updateData() {
        observeData()
    }
}