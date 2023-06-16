package com.domberdev.zeitplan.profile.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.domberdev.zeitplan.PermissionRequester
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.dialog.InputTextDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_general_setting.*
import java.io.File

class GeneralSettingFragment : Fragment(), InputTextDialog.OnInputResult {

    private val uid = FirebaseAuth.getInstance().currentUser?.uid!!
    private val storageRef = Firebase.storage.reference
    private val db = Firebase.firestore

    private val photosPermission = PermissionRequester(this,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        onDenied = { Toast.makeText(requireContext(), R.string.mediaAccessDenied, Toast.LENGTH_SHORT).show() })

    private val userEmail = FirebaseAuth.getInstance().currentUser?.email!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_general_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generalSettingToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        changeAcademicInfo()
        changePeriod()
        changeRoundGrades()
        changeName()
        changeProfilePicture()
    }

    private fun changeAcademicInfo(){
        changeAcademicInfo.setOnClickListener {
            db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                val university = user.getString("University")!!
                val career = user.getString("Career")!!
                val duration = user.getString("Duration")!!.toInt()
                val type = user.getString("Type")!!.toInt()

                val dialog = AlertDialog.Builder(requireContext()).create()
                val v = layoutInflater.inflate(R.layout.academic_infomation, null)
                dialog.setView(v)
                dialog.show()

                val cancelButton = v.findViewById<TextView>(R.id.cancelAcademicButton)

                val universityEdit = v.findViewById<TextInputEditText>(R.id.universityEdit)
                val careerEdit = v.findViewById<TextInputEditText>(R.id.careerEdit)
                val semesterSelector = v.findViewById<Spinner>(R.id.semesterSelector)
                val durationSelector = v.findViewById<Spinner>(R.id.durationSelector)
                val saveButton = v.findViewById<TextView>(R.id.saveAcademicButton)

                universityEdit.setText(university)
                careerEdit.setText(career)
                semesterSelector.setSelection(duration)
                durationSelector.setSelection(type)

                saveButton.setOnClickListener {
                    if (universityEdit.text!!.isNotEmpty() && careerEdit.text!!.isNotEmpty()){
                        db.enableNetwork().addOnSuccessListener {
                            db.collection("Users").document(userEmail).update(
                                mapOf(
                                    "University" to universityEdit.text.toString(),
                                    "Career" to careerEdit.text.toString(),
                                    "Duration" to semesterSelector.selectedItemPosition.toString(),
                                    "Type" to durationSelector.selectedItemPosition.toString()
                                ))
                        }
                        val periodType = when(durationSelector.selectedItemPosition){
                            0 -> getString(R.string.changeBimester)
                            1 -> getString(R.string.changeTrimester)
                            2 -> getString(R.string.changeFourPeriod)
                            3 -> getString(R.string.changeSemester)
                            4 -> getString(R.string.changeYear)
                            else -> getString(R.string.changeSemester)
                        }
                        selectPeriod.text = periodType
                        dialog.dismiss()
                    }else{
                        if (universityEdit.text!!.isEmpty()){
                            universityEdit.error = getString(R.string.youMustWriteUniversityInstitution)
                        }
                        if (careerEdit.text!!.isEmpty()){
                            careerEdit.error = getString(R.string.youMustWriteCareerCourse)
                        }
                    }
                }

                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }

    private fun changePeriod(){
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val periodType = when(user.getString("Type")!!.toInt()){
                0 -> getString(R.string.changeBimester)
                1 -> getString(R.string.changeTrimester)
                2 -> getString(R.string.changeFourPeriod)
                3 -> getString(R.string.changeSemester)
                4 -> getString(R.string.changeYear)
                else -> getString(R.string.changeSemester)
            }
            selectPeriod.text = periodType
        }
        selectPeriod.setOnClickListener {
            db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                val current = user.getString("Current")!!.toInt()
                val period = user.getString("Period")!!.toInt()

                val periodsList = arrayListOf<String>()
                for (i in 1..current){
                    periodsList.add(i.toString())
                }

                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle(R.string.changePeriod)
                    .setSingleChoiceItems(periodsList.toTypedArray(), period - 1) { dialogI, i ->
                        when (i){
                            (period - 1) -> {
                                dialogI.dismiss()
                            }
                            i -> {
                                val newPeriod = i + 1
                                println(newPeriod)
                                db.enableNetwork().addOnCompleteListener {
                                    db.collection("Users").document(userEmail).update("Period", newPeriod.toString())
                                }
                                dialogI.dismiss()
                                findNavController().navigate(R.id.changePeriodAction)
                            }
                        }
                    }
                    .show()
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun changeRoundGrades(){
        val switchInit = context?.getSharedPreferences("userdata", Context.MODE_PRIVATE)?.getBoolean("roundGrades", false)!!
        roundGradeSwitch.isChecked = switchInit
        roundGradeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val prefs = context?.getSharedPreferences("userdata", Context.MODE_PRIVATE)?.edit()!!
            if (isChecked){
                prefs.putBoolean("roundGrades", true)
                prefs.apply()
            }else{
                prefs.putBoolean("roundGrades", false)
                prefs.apply()
            }
        }
    }

    private fun changeName(){
        changeNameButton.setOnClickListener {
            InputTextDialog(requireContext(), this).showDialogText(R.string.changeName)
        }
    }

    override fun getInputValue(inputValue: String) {
        FirebaseAuth.getInstance().currentUser.let {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(inputValue)
                .build()

            FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)
            Toast.makeText(requireContext(), R.string.profileNameChanged, Toast.LENGTH_SHORT).show()
        }

        FirebaseFirestore.getInstance().enableNetwork().addOnCompleteListener {
            FirebaseFirestore.getInstance().collection("Users").document(userEmail).update("Name", inputValue)
        }
    }

    private fun changeProfilePicture(){
        setProfilePictureButton.setOnClickListener {
            photosPermission.runWithPermission{
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                resultLauncher.launch(intent)
            }
        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK){
            val imageUri = result.data?.data!!

            startCrop(imageUri)
        }
    }

    private fun startCrop(uri: Uri){
        val destinationFileName = "profilePicture.jpg"
        val uCrop = UCrop.of(uri, Uri.fromFile(File(requireContext().cacheDir, destinationFileName)))
        uCrop.withAspectRatio(1f,1f)
        uCrop.withMaxResultSize(400,400)
        uCrop.withOptions(getCropOptions())

        resultUCropLauncher.launch(uCrop.getIntent(requireContext()))
    }

    private var resultUCropLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK){
            val cropImg = UCrop.getOutput(result.data!!)!!
            val userImageRef = storageRef.child("Users/$uid/profile")
            userImageRef.putFile(cropImg)
            FirebaseAuth.getInstance().currentUser.let {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(cropImg)
                    .build()

                it?.updateProfile(profileUpdates)
            }
            Toast.makeText(requireContext(), R.string.profilePictureChanged, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCropOptions(): UCrop.Options{
        val options = UCrop.Options()

        options.setCompressionQuality(70)

        //CompressType
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)

        //UI
        options.setHideBottomControls(false)
        options.setFreeStyleCropEnabled(true)

        //UI Color
        options.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.statusBar))
        options.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        options.setActiveControlsWidgetColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        options.setToolbarWidgetColor(ContextCompat.getColor(requireContext(), R.color.white))//Toolbar icon and text color

        options.setToolbarTitle(getString(R.string.editPhoto))

        return options
    }
}