package com.domberdev.zeitplan.subjects.ui

import android.annotation.SuppressLint
import android.graphics.Color.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.subjects.CheckSubjectExistence
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.fragment_new_subject.*
import java.text.SimpleDateFormat
import java.util.*

class NewSubjectFragment : Fragment(){

    private val db = FirebaseFirestore.getInstance()

    private var interstitial:InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_subject, container, false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newSubjectToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        val drawable = requireContext().resources?.getDrawable(R.drawable.ic_circle, requireContext().theme)
        val bgColor = ContextCompat.getColor(requireContext(), R.color.backgroundColor)
        drawable?.setTint(bgColor)
        checkSubjectID()
        colorOfSubject.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
        switchChanges()
        selectColor()
        addSubject()
        initAds()
    }

    private fun initAds(){
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(), getString(R.string.interstitialAdId), adRequest, object: InterstitialAdLoadCallback(){
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                interstitial = interstitialAd
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                interstitial = null
            }
        })
    }

    private fun showAd(){
        val random = (1..100).random()
        if (random <=95){
            interstitial?.show(requireActivity())
        }
    }

    private fun getRandomString() : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..20)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun checkSubjectID(){
        val id = "#" + getRandomString()
        CheckSubjectExistence().checkSubjectID(subjectID, requireContext(), id)
    }

    private fun switchChanges(){
        onlineSubjectSwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                classroom.setText(R.string.online)
                classroom.isEnabled = false
            }else{
                classroom.setText("")
                classroom.isEnabled = true
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

    private var colorValue: Int? = WHITE
    private var colorValueLight: Int? = WHITE

    @SuppressLint("UseCompatLoadingForDrawables", "CheckResult")
    fun selectColor() {
        colorOfSubject.setOnClickListener {
            val colors = intArrayOf(RED, GREEN, BLUE, CYAN, YELLOW, MAGENTA, PINK, ORANGE, VIOLET, BROWN)

            val subColors = arrayOf( // size = 3
                intArrayOf(LTRED,RED,DKRED),
                intArrayOf(LTGREEN,GREEN,DKGREEN),
                intArrayOf(LTBLUE,BLUE,DKBLUE),
                intArrayOf(LTCYAN,CYAN,DKCYAN),
                intArrayOf(YELLOW,DKYELLOW),
                intArrayOf(LTMAGENTA,MAGENTA,DKMAGENTA),
                intArrayOf(LTPINK,PINK,DKPINK),
                intArrayOf(LTORANGE,ORANGE,DKORANGE),
                intArrayOf(LTVIOLET,VIOLET,DKVIOLET),
                intArrayOf(LTBROWN,BROWN,DKBROWN),
            )
            val drawble = requireContext().resources?.getDrawable(
                R.drawable.ic_circle,
                requireContext().theme
            )

            context?.let { it1 ->
                MaterialDialog(it1).show {
                    title(R.string.colors)
                    colorChooser(colors, subColors) { _, color ->
                        drawble?.setTint(color)
                        colorValue = color
                        when(color){
                            LTRED -> colorValueLight = 0xFFFF5050.toInt()
                            RED -> colorValueLight = 0xFFFF4040.toInt()
                            DKRED -> colorValueLight =  0xFFE30000.toInt()
                            LTGREEN -> colorValueLight = 0xFF4AFFA4.toInt()
                            GREEN -> colorValueLight = 0xFF00BD00.toInt()
                            DKGREEN -> colorValueLight = 0xFF00A600.toInt()
                            LTBLUE -> colorValueLight = 0xFF8080FF.toInt()
                            BLUE -> colorValueLight = 0xFF4040FF.toInt()
                            DKBLUE -> colorValueLight = 0xFF0000DB.toInt()
                            LTCYAN -> colorValueLight = 0xFFB8E4FF.toInt()
                            CYAN -> colorValueLight = 0xFF30FFFF.toInt()
                            DKCYAN -> colorValueLight = 0xFF00C0C0.toInt()
                            YELLOW -> colorValueLight = 0xFFFFFF8F.toInt()
                            DKYELLOW -> colorValueLight = 0xFFFFE250.toInt()
                            LTMAGENTA -> colorValueLight = 0xFFFFBFFF.toInt()
                            MAGENTA -> colorValueLight = 0xFFFF5FFF.toInt()
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
                colorOfSubject.setCompoundDrawablesRelativeWithIntrinsicBounds(drawble, null, null, null)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun addSubject(){
        addSubjectButton.setOnClickListener {
            nameOfSubjectLayout.error = null
            nameOfSubjectLayout.clearFocus()
            acronymOfSubjectLayout.error = null
            acronymOfSubjectLayout.clearFocus()
            classroomLayout.error = null
            classroomLayout.clearFocus()
            professorNameLayout.error = null
            professorNameLayout.clearFocus()
            subjectIDLayout.error = null
            subjectIDLayout.clearFocus()
            colorOfSubject.error =  null
            colorOfSubject.clearFocus()
            val subjectName = nameOfSubject.text!!.toString()
            val classroomValue = when(classroom.text!!.toString()){
                getString(R.string.online) -> "6593632304"
                else -> classroom.text!!.toString()
            }
            val professorNameValue = professorName.text!!.toString()
            val subjectIDValue = subjectID.text!!.toString()
            val subjectAcronym = acronymOfSubject.text!!.toString()

            val correctAcronym = acronymOfSubject.text!!.isNotEmpty() && acronymOfSubject.text!!.length <= 7

            val userEmail = FirebaseAuth.getInstance().currentUser?.email!!
            val userUID = FirebaseAuth.getInstance().currentUser?.uid

            val arrayList = arrayListOf<String>()
            val scheduleArrayList = arrayListOf<String>()

            val sdf = SimpleDateFormat("yyyy/MM/dd")
            val currentDate = sdf.format(Date())

            if (nameOfSubject.text!!.isNotEmpty() && classroom.text!!.isNotEmpty() && professorName.text!!.isNotEmpty() && subjectID.text!!.isNotEmpty() && colorValue != WHITE && correctAcronym){
                db.enableNetwork().addOnSuccessListener {
                    db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                        val phase = user.getString("Phase")!!
                        val period = user.getString("Period")!!
                        db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectIDValue).set(
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
                    }

                    arrayList.add(userUID!!)
                    db.collection("Tasks").document(subjectIDValue).set(
                        mapOf(
                            "createdBy" to userEmail,
                            "createdOn" to currentDate
                        )
                    )
                }
                showAd()
                findNavController().popBackStack()
            }else{
                if (colorValue == WHITE){
                    colorOfSubject.error = "Select a color"
                }
                if (nameOfSubject.text!!.isEmpty()){
                    nameOfSubjectLayout.error = getString(R.string.enterASubjectName)
                }
                if (acronymOfSubject.text!!.isEmpty()){
                    acronymOfSubjectLayout.error = getString(R.string.enterAcronymSubject)
                }
                if (acronymOfSubject.text!!.length > 7){
                    acronymOfSubjectLayout.error = getString(R.string.theAcronymMustBeLess8Characters)
                }
                if (classroom.text!!.isEmpty()){
                    classroomLayout.error = getString(R.string.enterAClassroom)
                }
                if (professorName.text!!.isEmpty()){
                    professorNameLayout.error = getString(R.string.enterAProfessorName)
                }
                if (subjectID.text!!.isEmpty()){
                    subjectIDLayout.error = getString(R.string.enterASubjectID)
                }
            }
        }
    }
}