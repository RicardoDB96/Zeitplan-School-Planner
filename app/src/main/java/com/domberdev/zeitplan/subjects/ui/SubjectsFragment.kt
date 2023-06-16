package com.domberdev.zeitplan.subjects.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.subjects.CheckSubjectsDocumentSize
import com.domberdev.zeitplan.subjects.adapter.SubjectAdapter
import com.domberdev.zeitplan.subjects.bottomsheet.SubjectOptionsBottomSheet
import com.domberdev.zeitplan.subjects.network.SubjectViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.fragment_subjects.*

@Suppress("NAME_SHADOWING")
class SubjectsFragment : Fragment(), SubjectAdapter.OnSubjectClickListener, SubjectOptionsBottomSheet.OnSettingsSubjectListener, CheckSubjectsDocumentSize.CreateNewSubject {

    private lateinit var adapter: SubjectAdapter
    private val viewModel by lazy { ViewModelProvider(this)[SubjectViewModel::class.java] }
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("NotifyDataSetChanged")
    private fun observeData(){
        noSubjectData.visibility = View.GONE
        shimmerSubjects.showShimmer(true)
        db.collection("Users").document(userEmail!!).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").get(Source.CACHE).addOnSuccessListener { subjects ->
                if (subjects.size() != 0){
                    subjectsRV.visibility = View.VISIBLE
                    viewModel.fetchSubjectData().observe(viewLifecycleOwner) {
                        shimmerSubjects.stopShimmer()
                        shimmerSubjects.showShimmer(false)
                        shimmerSubjects.visibility = View.GONE
                        adapter.setListData(it)
                        adapter.notifyDataSetChanged()
                    }
                }else{
                    shimmerSubjects.stopShimmer()
                    shimmerSubjects.showShimmer(false)
                    shimmerSubjects.visibility = View.GONE
                    noSubjectData.visibility = View.VISIBLE
                    subjectsRV.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subjects, container, false)  // initialize it here
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subjectsToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        fabMenu()
        adapter = SubjectAdapter(requireContext(),this)
        subjectsRV.layoutManager = LinearLayoutManager(context)
        subjectsRV.adapter = adapter
        observeData()
    }

    private fun fabMenu(){
        createSubject.setOnClickListener { CheckSubjectsDocumentSize(requireContext(), this, 0).checkSubjectSize() }
    }

    override fun onSubjectClick(subjectID: String) {
        val bundle = Bundle()
        bundle.putString("subjectID", subjectID)
        findNavController().navigate(R.id.subjectToInfoSubject, bundle)
    }

    override fun onSubjectLongClick(subjectID: String, generalGrade: Boolean) {
        val bottomSheetFragment = SubjectOptionsBottomSheet(this)
        val bundle = Bundle()
        bundle.putString("subjectID", subjectID)
        bundle.putBoolean("generalGrade", generalGrade)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
    }

    override fun onSettingsClick(subjectID: String, generalGrade: Boolean) {
        val bottomSheetFragment = SubjectOptionsBottomSheet(this)
        val bundle = Bundle()
        bundle.putString("subjectID", subjectID)
        bundle.putBoolean("generalGrade", generalGrade)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
    }

    override fun updateSubjectData() {
        observeData()
    }

    override fun createNewSubject() {
        findNavController().navigate(R.id.subjectToNewSubject)
    }

    override fun joinToSubject() {
        findNavController().navigate(R.id.subjectToScanner)
    }
}
