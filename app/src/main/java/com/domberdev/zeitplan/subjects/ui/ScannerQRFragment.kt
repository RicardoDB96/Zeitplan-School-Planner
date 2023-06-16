package com.domberdev.zeitplan.subjects.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.domberdev.zeitplan.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_scanner_qr.*

class ScannerQRFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scanner_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scannerQRToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        enterSubjectIDText()
    }

    private fun enterSubjectIDText(){

        subjectIDScan.addTextChangedListener {
            checkSubjectIDET.isEnabled = subjectIDScan.text?.length == 21
        }

        checkSubjectIDET.setOnClickListener {
            if (subjectIDScan.text?.length == 21) {
                if (subjectIDScan.text!!.startsWith("#") ){
                    FirebaseFirestore.getInstance().enableNetwork().addOnSuccessListener {
                        FirebaseFirestore.getInstance().collection("Tasks").document(subjectIDScan.text.toString()).get().addOnSuccessListener {
                            if (it.exists()){
                                val bundle = Bundle()
                                bundle.putString("subjectID", subjectIDScan.text.toString())
                                findNavController().navigate(R.id.scannerToJoinToSubject, bundle)
                            }else{
                                Toast.makeText(requireContext(),
                                    R.string.theIDCodeOfTheSubjectDoesNotExist, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }else
                    Toast.makeText(requireContext(), R.string.theIDCodeShouldStartWith, Toast.LENGTH_LONG).show()
            }
        }
    }
}