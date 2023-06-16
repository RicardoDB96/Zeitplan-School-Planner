package com.domberdev.zeitplan.subjects.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser
import com.domberdev.zeitplan.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_join_to_subject.*

class JoinToSubjectFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var colorValue = Color.WHITE
    private var colorValueLight: Int? = Color.WHITE
    private lateinit var subjectID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join_to_subject, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        joinToSubjectToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        bindData()
        selectColor()
        switchChanges()
        saveData()
    }

    private fun bindData(){
        subjectID = arguments?.getString("subjectID")!!

        db.enableNetwork().addOnSuccessListener {
            db.collection("Tasks").document(subjectID).get().addOnSuccessListener {
                nameOfSubjectJoin.setText(it.getString("subjectTitle"))
                acronymOfSubjectJoin.setText(it.getString("subjectAcronym"))
                if (it.getString("place") == getString(R.string.online)){
                    classroomJoin.setText(R.string.online)
                    classroomJoin.isEnabled = false
                    onlineSubjectSwitchJoin.isChecked = true
                }else{
                    classroomJoin.setText(it.getString("place"))
                    onlineSubjectSwitchJoin.isChecked = false
                    classroomJoin.isEnabled = true
                }
                professorNameJoin.setText(it.getString("teacher"))
                subjectIDJoin.setText(subjectID)
            }
        }
    }

    val DKRED = 0xFFB40000.toInt()
    val LTRED = 0xFFFF3232.toInt()

    val DKGREEN = 0xFF008000.toInt()
    val GREEN = 0xFF00A000.toInt()
    val LTGREEN = 0xFF00FF7F.toInt()

    val DKBLUE = 0xFF0000A0.toInt()
    val LTBLUE = 0xFF5A5AFF.toInt()

    val DKYELLOW = 0xFFFFD700.toInt()

    val DKMAGENTA = 0xFF8B008B.toInt()
    val LTMAGENTA = 0xFFE78BE7.toInt()

    val LTPINK = 0xFFFFC0CB.toInt()
    val PINK = 0xFFFF69B4.toInt()
    val DKPINK = 0xFFFF1493.toInt()

    val LTORANGE = 0xFFFFA07A.toInt()
    val ORANGE = 0xFFFFA500.toInt()
    val DKORANGE = 0xFFFF8C00.toInt()

    val LTVIOLET = 0xFFDDA0DD.toInt()
    val VIOLET = 0xFFEE82EE.toInt()
    val DKVIOLET = 0xFF9400D3.toInt()

    val LTCYAN = 0xFF87CEFA.toInt()
    val DKCYAN = 0xFF008B8B.toInt()

    val LTBROWN = 0xFFD2691E.toInt()
    val BROWN = 0xFFA0522D.toInt()
    val DKBROWN = 0xFFA52A2A.toInt()


    @SuppressLint("UseCompatLoadingForDrawables", "CheckResult")
    private fun selectColor() {
        colorOfSubjectJoin.setOnClickListener {
            val colors = intArrayOf(
                Color.RED, GREEN,
                Color.BLUE,
                Color.CYAN,
                Color.YELLOW,
                Color.MAGENTA, PINK, ORANGE, VIOLET, BROWN)

            val subColors = arrayOf( // size = 3
                intArrayOf(LTRED, Color.RED,DKRED),
                intArrayOf(LTGREEN, GREEN,DKGREEN),
                intArrayOf(LTBLUE, Color.BLUE,DKBLUE),
                intArrayOf(LTCYAN, Color.CYAN,DKCYAN),
                intArrayOf(Color.YELLOW,DKYELLOW),
                intArrayOf(LTMAGENTA, Color.MAGENTA,DKMAGENTA),
                intArrayOf(LTPINK,PINK,DKPINK),
                intArrayOf(LTORANGE,ORANGE,DKORANGE),
                intArrayOf(LTVIOLET,VIOLET,DKVIOLET),
                intArrayOf(LTBROWN,BROWN,DKBROWN),
            )
            val drawable = requireContext().resources?.getDrawable(
                R.drawable.ic_circle,
                requireContext().theme
            )

            context?.let { it1 ->
                MaterialDialog(it1).show {
                    title(R.string.colors)
                    colorChooser(colors, subColors) { _, color ->
                        drawable?.setTint(color)
                        colorValue = color
                        when(color){
                            LTRED -> colorValueLight = 0xFFFF5050.toInt()
                            Color.RED -> colorValueLight = 0xFFFF4040.toInt()
                            DKRED -> colorValueLight =  0xFFE30000.toInt()
                            LTGREEN -> colorValueLight = 0xFF4AFFA4.toInt()
                            GREEN -> colorValueLight = 0xFF00BD00.toInt()
                            DKGREEN -> colorValueLight = 0xFF00A600.toInt()
                            LTBLUE -> colorValueLight = 0xFF8080FF.toInt()
                            Color.BLUE -> colorValueLight = 0xFF4040FF.toInt()
                            DKBLUE -> colorValueLight = 0xFF0000DB.toInt()
                            LTCYAN -> colorValueLight = 0xFFB8E4FF.toInt()
                            Color.CYAN -> colorValueLight = 0xFF30FFFF.toInt()
                            DKCYAN -> colorValueLight = 0xFF00C0C0.toInt()
                            Color.YELLOW -> colorValueLight = 0xFFFFFF8F.toInt()
                            DKYELLOW -> colorValueLight = 0xFFFFE250.toInt()
                            LTMAGENTA -> colorValueLight = 0xFFFFBFFF.toInt()
                            Color.MAGENTA -> colorValueLight = 0xFFFF5FFF.toInt()
                            DKMAGENTA -> colorValueLight = 0xFFD500D5.toInt()
                            LTPINK -> colorValueLight = 0xFFFFD6DD.toInt()
                            PINK -> colorValueLight = 0xFFFF8CC6.toInt()
                            DKPINK -> colorValueLight = 0xFFFF54B0.toInt()
                            LTORANGE -> colorValueLight = 0xFFFFBDA3.toInt()
                            ORANGE -> colorValueLight = 0xFFFFBC40.toInt()
                            DKORANGE -> colorValueLight = 0xFFFFA333.toInt()
                            LTVIOLET -> colorValueLight = 0xFFFFCFFF.toInt()
                            VIOLET -> colorValueLight = 0xFFFFA6FF.toInt()
                            DKVIOLET -> colorValueLight = 0xFFC438FF.toInt()
                            LTBROWN -> colorValueLight = 0xFFFF974D.toInt()
                            BROWN -> colorValueLight = 0xFFC77A56.toInt()
                            DKBROWN -> colorValueLight = 0xFFC94D4D.toInt()
                        }
                    }
                    positiveButton(R.string.select)
                }
                colorOfSubjectJoin.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    drawable,
                    null,
                    null,
                    null
                )
            }
        }
    }

    private fun switchChanges(){
        onlineSubjectSwitchJoin.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                classroomJoin.setText(R.string.online)
                classroomJoin.isEnabled = false
            } else {
                classroomJoin.setText("")
                classroomJoin.isEnabled = true
            }
        }
    }

    private fun saveData(){
        addSubjectButtonJoin.setOnClickListener {
            nameOfSubjectLayoutJoin.error = null
            nameOfSubjectLayoutJoin.clearFocus()
            acronymOfSubjectLayoutJoin.error = null
            acronymOfSubjectLayoutJoin.clearFocus()
            classroomLayoutJoin.error = null
            classroomLayoutJoin.clearFocus()
            professorNameLayoutJoin.error = null
            professorNameLayoutJoin.clearFocus()
            subjectIDLayoutJoin.error = null
            subjectIDLayoutJoin.clearFocus()
            colorOfSubjectJoin.error =  null
            colorOfSubjectJoin.clearFocus()
            val subjectName = nameOfSubjectJoin.text!!.toString()
            val classroomValue = classroomJoin.text!!.toString()
            val professorNameValue = professorNameJoin.text!!.toString()
            val subjectIDValue = subjectIDJoin.text!!.toString()
            val subjectAcronym = acronymOfSubjectJoin.text!!.toString()

            val correctAcronym = acronymOfSubjectJoin.text!!.isNotEmpty() && acronymOfSubjectJoin.text!!.length <= 7

            val userUID = FirebaseAuth.getInstance().currentUser?.uid!!

            val arrayList = db.collection("Tasks").document(subjectIDValue)
            val scheduleArrayList = arrayListOf<String>()

            if (nameOfSubjectJoin.text!!.isNotEmpty() && classroomJoin.text!!.isNotEmpty() && professorNameJoin.text!!.isNotEmpty() && subjectIDJoin.text!!.isNotEmpty() && colorValue != Color.WHITE && correctAcronym){
                db.enableNetwork().addOnSuccessListener {
                    db.collection("Users").document(subjectIDValue).set(
                        mapOf(
                            "subjectTitle" to subjectName,
                            "subjectAcronym" to subjectAcronym,
                            "place" to classroomValue,
                            "teacher" to professorNameValue,
                            "subjectID" to subjectIDValue,
                            "color" to colorValue.toString(),
                            "secondaryColor" to colorValueLight.toString(),
                            "grade" to "0",
                            "goal" to "-",
                            "generalGrade" to true,
                            "scheduleDays" to scheduleArrayList
                        )
                    )
                    arrayList.update("usersLinked", FieldValue.arrayUnion(userUID))
                }
                findNavController().navigate(R.id.joinToSubjectToHome)
            } else{
                if (colorValue == Color.WHITE){
                    colorOfSubjectJoin.error = "Select a color"
                }
                if (nameOfSubjectJoin.text!!.isEmpty()){
                    nameOfSubjectLayoutJoin.error = getString(R.string.enterASubjectName)
                }
                if (acronymOfSubjectJoin.text!!.isEmpty()){
                    acronymOfSubjectLayoutJoin.error = getString(R.string.enterAcronymSubject)
                }
                if (acronymOfSubjectJoin.text!!.length > 7){
                    acronymOfSubjectLayoutJoin.error = getString(R.string.theAcronymMustBeLess8Characters)
                }
                if (classroomJoin.text!!.isEmpty()){
                    classroomLayoutJoin.error = getString(R.string.enterAClassroom)
                }
                if (professorNameJoin.text!!.isEmpty()){
                    professorNameLayoutJoin.error = getString(R.string.enterAProfessorName)
                }
                if (subjectIDJoin.text!!.isEmpty()){
                    subjectIDLayoutJoin.error = getString(R.string.enterASubjectID)
                }
            }
        }
    }
}