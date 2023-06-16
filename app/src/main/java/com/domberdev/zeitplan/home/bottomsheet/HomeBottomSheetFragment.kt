package com.domberdev.zeitplan.home.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.subjects.CheckSubjectsDocumentSize
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.home_subject_options_bottom_sheet.*

class HomeBottomSheetFragment: BottomSheetDialogFragment(), CheckSubjectsDocumentSize.CreateNewSubject{

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_subject_options_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //La acciones de cada uno de los botones
        goToSubjectBottom.setOnClickListener {
            findNavController().navigate(R.id.homeToSubject)
            dismiss()
        }
        createNewSubjectBottom.setOnClickListener {
            CheckSubjectsDocumentSize(requireContext(), this, 0).checkSubjectSize()
        }
    }

    override fun createNewSubject() {
        findNavController().navigate(R.id.homeToNewSubject)
        dismiss()
    }

    override fun joinToSubject() {
        findNavController().navigate(R.id.homeToScannerQR)
        dismiss()
    }
}