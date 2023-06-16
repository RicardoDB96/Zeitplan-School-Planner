package com.domberdev.zeitplan.subjects.ui

import android.annotation.SuppressLint
import android.graphics.Color.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser
import com.domberdev.zeitplan.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.fragment_edit_subject.*

class EditSubjectFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email!!
    private var colorValue = 1
    private var colorValueLight = 1
    private lateinit var subjectID: String

    private var interstitial:InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_subject, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editSubjectToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        bindData()
        switchChanges()
        selectColor()
        saveChanges()
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
        if (random <= 90){
            interstitial?.show(requireActivity())
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun bindData (){
        subjectID = arguments?.getString("subjectID")!!
        db.disableNetwork().addOnCompleteListener {
            db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                val phase = user.getString("Phase")!!
                val period = user.getString("Period")!!
                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get().addOnSuccessListener {
                    nameOfSubjectEdit.setText(it.getString("subjectTitle"))
                    acronymOfSubjectEdit.setText(it.getString("subjectAcronym"))
                    val drawable = context?.resources?.getDrawable(
                        R.drawable.ic_circle,
                        requireContext().theme
                    )
                    val color = it.getString("color")?.toInt()!!
                    drawable?.setTint(color)
                    colorOfSubjectEdit.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
                    colorValue = color
                    colorValueLight = it.getString("secondaryColor")?.toInt()!!

                    if (it.getString("place") == "6593632304"){
                        classroomEdit.setText(R.string.online)
                        classroomEdit.isEnabled = false
                        onlineSubjectSwitchEdit.isChecked = true
                    }else{
                        classroomEdit.setText(it.getString("place").toString())
                        onlineSubjectSwitchEdit.isChecked = false
                        classroomEdit.isEnabled = true
                    }
                    professorNameEdit.setText(it.getString("teacher").toString())
                    subjectIDEdit.setText(it.getString("subjectID").toString())
                    subjectIDEdit.text.toString()
                    subjectIDEdit.isEnabled = false
                }
            }
        }
    }


    private fun switchChanges(){
        onlineSubjectSwitchEdit.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                classroomEdit.setText(R.string.online)
                classroomEdit.isEnabled = false
            } else {
                classroomEdit.setText("")
                classroomEdit.isEnabled = true
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
        colorOfSubjectEdit.setOnClickListener {
            val colors = intArrayOf(RED, GREEN, BLUE, CYAN, YELLOW, MAGENTA, PINK, ORANGE, VIOLET, BROWN)

            val subColors = arrayOf( // size = 3
                intArrayOf(LTRED, RED,DKRED),
                intArrayOf(LTGREEN, GREEN,DKGREEN),
                intArrayOf(LTBLUE, BLUE,DKBLUE),
                intArrayOf(LTCYAN, CYAN,DKCYAN),
                intArrayOf(YELLOW,DKYELLOW),
                intArrayOf(LTMAGENTA, MAGENTA,DKMAGENTA),
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
                colorOfSubjectEdit.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    drawble,
                    null,
                    null,
                    null
                )
            }
        }
    }

    private fun saveChanges(){
        editSubjectButton.setOnClickListener {
            nameOfSubjectLayoutEdit.error = null
            acronymOfSubjectLayoutEdit.error = null
            classroomLayoutEdit.error = null
            professorNameLayoutEdit.error = null
            subjectIDLayoutEdit.error = null
            colorOfSubjectEdit.error =  null

            val classroomValue = when(classroomEdit.text!!.toString()){
                getString(R.string.online) -> "6593632304"
                else -> classroomEdit.text!!.toString()
            }
            val professorNameValue = professorNameEdit.text!!.toString()

            val correctAcronym = acronymOfSubjectEdit.text!!.isNotEmpty() && acronymOfSubjectEdit.text!!.length <= 7

            if (nameOfSubjectEdit.text!!.isNotEmpty() && classroomEdit.text!!.isNotEmpty() && professorNameEdit.text!!.isNotEmpty() && subjectIDEdit.text!!.isNotEmpty() && colorValue != WHITE && colorValueLight != WHITE  && correctAcronym){
                db.enableNetwork().addOnSuccessListener {
                    db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                        val phase = user.getString("Phase")!!
                        val period = user.getString("Period")!!
                        db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).update(
                            mapOf(
                                "place" to classroomValue,
                                "teacher" to professorNameValue,
                                "color" to colorValue.toString(),
                                "secondaryColor" to colorValueLight.toString()
                            )
                        )
                    }
                }
                showAd()
                findNavController().popBackStack()
            }else{
                if (colorValue == WHITE){
                    colorOfSubjectEdit.error = "Select a color"
                }
                if (nameOfSubjectEdit.text!!.isEmpty()){
                    nameOfSubjectLayoutEdit.error = getString(R.string.enterASubjectName)
                }
                if (acronymOfSubjectEdit.text!!.isEmpty()){
                    acronymOfSubjectLayoutEdit.error = getString(R.string.enterAcronymSubject)
                }
                if (acronymOfSubjectEdit.text!!.length > 7){
                    acronymOfSubjectLayoutEdit.error = getString(R.string.theAcronymMustBeLess8Characters)
                }
                if (classroomEdit.text!!.isEmpty()){
                    classroomLayoutEdit.error = getString(R.string.enterAClassroom)
                }
                if (professorNameEdit.text!!.isEmpty()){
                    professorNameLayoutEdit.error = getString(R.string.enterAProfessorName)
                }
                if (subjectIDEdit.text!!.isEmpty()){
                    subjectIDLayoutEdit.error = getString(R.string.enterASubjectID)
                }
            }
        }
    }
}