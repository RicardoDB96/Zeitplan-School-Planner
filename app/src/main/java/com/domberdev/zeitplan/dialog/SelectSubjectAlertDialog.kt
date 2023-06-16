package com.domberdev.zeitplan.dialog

import android.app.AlertDialog
import android.content.Context
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.domberdev.zeitplan.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source

class SelectSubjectAlertDialog(private val context: Context, private val getSubjectList: GetSubjectList, private val onDismissDialog: DismissDialog?) {

    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email!!

    private var subjectIDNew = ""

    interface GetSubjectList{
        fun getSubjectList(subjectID: String)
    }

    interface DismissDialog{
        fun dismissDialog()
    }

    fun getSubjectList(button: Button, fragment: Fragment, int: Int){
        var subjectID = String()

        var subjectID1 = String()
        var subjectID2 = String()
        var subjectID3 = String()
        var subjectID4 = String()
        var subjectID5 = String()
        var subjectID6 = String()
        var subjectID7 = String()
        var subjectID8 = String()
        var subjectID9 = String()
        var subjectID10 = String()
        var subjectID11 = String()
        var subjectID12 = String()

        var item1 = String()
        var item2 = String()
        var item3 = String()
        var item4 = String()
        var item5 = String()
        var item6 = String()
        var item7 = String()
        var item8 = String()
        var item9 = String()
        var item10 = String()
        var item11 = String()
        var item12 = String()

        var array = arrayOf<String>()

        db.disableNetwork().addOnCompleteListener {
            db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                val phase = user.getString("Phase")!!
                val period = user.getString("Period")!!
                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").orderBy("subjectTitle", Query.Direction.ASCENDING).get(Source.CACHE).addOnSuccessListener {
                    for (item in it){
                        if (it.size() == 12){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                            item4 = it.documents[3].getString("subjectTitle")!!; subjectID4 = it.documents[3].getString("subjectID")!!
                            item5 = it.documents[4].getString("subjectTitle")!!; subjectID5 = it.documents[4].getString("subjectID")!!
                            item6 = it.documents[5].getString("subjectTitle")!!; subjectID6 = it.documents[5].getString("subjectID")!!
                            item7 = it.documents[6].getString("subjectTitle")!!; subjectID7 = it.documents[6].getString("subjectID")!!
                            item8 = it.documents[7].getString("subjectTitle")!!; subjectID8 = it.documents[7].getString("subjectID")!!
                            item9 = it.documents[8].getString("subjectTitle")!!; subjectID9 = it.documents[8].getString("subjectID")!!
                            item10 = it.documents[9].getString("subjectTitle")!!; subjectID10 = it.documents[9].getString("subjectID")!!
                            item11 = it.documents[10].getString("subjectTitle")!!; subjectID11 = it.documents[10].getString("subjectID")!!
                            item12 = it.documents[11].getString("subjectTitle")!!; subjectID12 = it.documents[11].getString("subjectID")!!
                        }
                        if (it.size() == 11){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                            item4 = it.documents[3].getString("subjectTitle")!!; subjectID4 = it.documents[3].getString("subjectID")!!
                            item5 = it.documents[4].getString("subjectTitle")!!; subjectID5 = it.documents[4].getString("subjectID")!!
                            item6 = it.documents[5].getString("subjectTitle")!!; subjectID6 = it.documents[5].getString("subjectID")!!
                            item7 = it.documents[6].getString("subjectTitle")!!; subjectID7 = it.documents[6].getString("subjectID")!!
                            item8 = it.documents[7].getString("subjectTitle")!!; subjectID8 = it.documents[7].getString("subjectID")!!
                            item9 = it.documents[8].getString("subjectTitle")!!; subjectID9 = it.documents[8].getString("subjectID")!!
                            item10 = it.documents[9].getString("subjectTitle")!!; subjectID10 = it.documents[9].getString("subjectID")!!
                            item11 = it.documents[10].getString("subjectTitle")!!; subjectID11 = it.documents[10].getString("subjectID")!!
                        }
                        if (it.size() == 10){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                            item4 = it.documents[3].getString("subjectTitle")!!; subjectID4 = it.documents[3].getString("subjectID")!!
                            item5 = it.documents[4].getString("subjectTitle")!!; subjectID5 = it.documents[4].getString("subjectID")!!
                            item6 = it.documents[5].getString("subjectTitle")!!; subjectID6 = it.documents[5].getString("subjectID")!!
                            item7 = it.documents[6].getString("subjectTitle")!!; subjectID7 = it.documents[6].getString("subjectID")!!
                            item8 = it.documents[7].getString("subjectTitle")!!; subjectID8 = it.documents[7].getString("subjectID")!!
                            item9 = it.documents[8].getString("subjectTitle")!!; subjectID9 = it.documents[8].getString("subjectID")!!
                            item10 = it.documents[9].getString("subjectTitle")!!; subjectID10 = it.documents[9].getString("subjectID")!!
                        }
                        if (it.size() == 9){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                            item4 = it.documents[3].getString("subjectTitle")!!; subjectID4 = it.documents[3].getString("subjectID")!!
                            item5 = it.documents[4].getString("subjectTitle")!!; subjectID5 = it.documents[4].getString("subjectID")!!
                            item6 = it.documents[5].getString("subjectTitle")!!; subjectID6 = it.documents[5].getString("subjectID")!!
                            item7 = it.documents[6].getString("subjectTitle")!!; subjectID7 = it.documents[6].getString("subjectID")!!
                            item8 = it.documents[7].getString("subjectTitle")!!; subjectID8 = it.documents[7].getString("subjectID")!!
                            item9 = it.documents[8].getString("subjectTitle")!!; subjectID9 = it.documents[8].getString("subjectID")!!
                        }
                        if (it.size() == 8){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                            item4 = it.documents[3].getString("subjectTitle")!!; subjectID4 = it.documents[3].getString("subjectID")!!
                            item5 = it.documents[4].getString("subjectTitle")!!; subjectID5 = it.documents[4].getString("subjectID")!!
                            item6 = it.documents[5].getString("subjectTitle")!!; subjectID6 = it.documents[5].getString("subjectID")!!
                            item7 = it.documents[6].getString("subjectTitle")!!; subjectID7 = it.documents[6].getString("subjectID")!!
                            item8 = it.documents[7].getString("subjectTitle")!!; subjectID8 = it.documents[7].getString("subjectID")!!
                        }
                        if (it.size() == 7){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                            item4 = it.documents[3].getString("subjectTitle")!!; subjectID4 = it.documents[3].getString("subjectID")!!
                            item5 = it.documents[4].getString("subjectTitle")!!; subjectID5 = it.documents[4].getString("subjectID")!!
                            item6 = it.documents[5].getString("subjectTitle")!!; subjectID6 = it.documents[5].getString("subjectID")!!
                            item7 = it.documents[6].getString("subjectTitle")!!; subjectID7 = it.documents[6].getString("subjectID")!!
                        }
                        if (it.size() == 6){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                            item4 = it.documents[3].getString("subjectTitle")!!; subjectID4 = it.documents[3].getString("subjectID")!!
                            item5 = it.documents[4].getString("subjectTitle")!!; subjectID5 = it.documents[4].getString("subjectID")!!
                            item6 = it.documents[5].getString("subjectTitle")!!; subjectID6 = it.documents[5].getString("subjectID")!!
                        }
                        if (it.size() == 5){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                            item4 = it.documents[3].getString("subjectTitle")!!; subjectID4 = it.documents[3].getString("subjectID")!!
                            item5 = it.documents[4].getString("subjectTitle")!!; subjectID5 = it.documents[4].getString("subjectID")!!
                        }
                        if (it.size() == 4){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                            item4 = it.documents[3].getString("subjectTitle")!!; subjectID4 = it.documents[3].getString("subjectID")!!
                        }
                        if (it.size() == 3){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                        }
                        if (it.size() == 2){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                        }
                        if (it.size() == 1){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                        }

                        when(it.size()){
                            1 -> array = arrayOf(item1)
                            2 -> array = arrayOf(item1, item2)
                            3 -> array = arrayOf(item1, item2, item3)
                            4 -> array = arrayOf(item1, item2, item3, item4)
                            5 -> array = arrayOf(item1, item2, item3, item4, item5)
                            6 -> array = arrayOf(item1, item2, item3, item4, item5, item6)
                            7 -> array = arrayOf(item1, item2, item3, item4, item5, item6, item7)
                            8 -> array = arrayOf(item1, item2, item3, item4, item5, item6, item7, item8)
                            9 -> array = arrayOf(item1, item2, item3, item4, item5, item6, item7, item8, item9)
                            10 -> array = arrayOf(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)
                            11 -> array = arrayOf(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11)
                            12 -> array = arrayOf(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item12)
                        }
                    }
                }
            }
        }
        db.enableNetwork()

        button.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.selectASubject)
                .setItems(array) { dialog, which ->
                    when (which) {
                        0 -> {button.text = item1; subjectID = subjectID1}
                        1 -> {button.text = item2; subjectID = subjectID2}
                        2 -> {button.text = item3; subjectID = subjectID3}
                        3 -> {button.text = item4; subjectID = subjectID4}
                        4 -> {button.text = item5; subjectID = subjectID5}
                        5 -> {button.text = item6; subjectID = subjectID6}
                        6 -> {button.text = item7; subjectID = subjectID7}
                        7 -> {button.text = item8; subjectID = subjectID8}
                        8 -> {button.text = item9; subjectID = subjectID9}
                        9 -> {button.text = item10; subjectID = subjectID10}
                        10 -> {button.text = item11; subjectID = subjectID11}
                        11 -> {button.text = item12; subjectID = subjectID12}
                    }
                    db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                        val phase = user.getString("Phase")!!
                        val period = user.getString("Period")!!
                        db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get(Source.CACHE).addOnSuccessListener {
                            subjectIDNew = it.getString("subjectID")!!
                            getSubjectList.getSubjectList(subjectIDNew)
                        }
                    }
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.createNewSubject) { dialog, _ ->
                    dialog.dismiss()
                    findNavController(fragment).navigate(int)
                }
                .show()
        }
    }

    fun getSubjectListTextView(textView: TextView, fragment: Fragment, int: Int){
        var subjectID = String()

        var subjectID1 = String()
        var subjectID2 = String()
        var subjectID3 = String()
        var subjectID4 = String()
        var subjectID5 = String()
        var subjectID6 = String()
        var subjectID7 = String()
        var subjectID8 = String()
        var subjectID9 = String()
        var subjectID10 = String()
        var subjectID11 = String()
        var subjectID12 = String()

        var item1 = String()
        var item2 = String()
        var item3 = String()
        var item4 = String()
        var item5 = String()
        var item6 = String()
        var item7 = String()
        var item8 = String()
        var item9 = String()
        var item10 = String()
        var item11 = String()
        var item12 = String()

        var array = arrayOf<String>()

        db.disableNetwork().addOnCompleteListener {
            db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                val phase = user.getString("Phase")!!
                val period = user.getString("Period")!!
                db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").orderBy("subjectTitle", Query.Direction.ASCENDING).get(Source.CACHE).addOnSuccessListener {
                    for (item in it){
                        if (it.size() == 12){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                            item4 = it.documents[3].getString("subjectTitle")!!; subjectID4 = it.documents[3].getString("subjectID")!!
                            item5 = it.documents[4].getString("subjectTitle")!!; subjectID5 = it.documents[4].getString("subjectID")!!
                            item6 = it.documents[5].getString("subjectTitle")!!; subjectID6 = it.documents[5].getString("subjectID")!!
                            item7 = it.documents[6].getString("subjectTitle")!!; subjectID7 = it.documents[6].getString("subjectID")!!
                            item8 = it.documents[7].getString("subjectTitle")!!; subjectID8 = it.documents[7].getString("subjectID")!!
                            item9 = it.documents[8].getString("subjectTitle")!!; subjectID9 = it.documents[8].getString("subjectID")!!
                            item10 = it.documents[9].getString("subjectTitle")!!; subjectID10 = it.documents[9].getString("subjectID")!!
                            item11 = it.documents[10].getString("subjectTitle")!!; subjectID11 = it.documents[10].getString("subjectID")!!
                            item12 = it.documents[11].getString("subjectTitle")!!; subjectID12 = it.documents[11].getString("subjectID")!!
                        }
                        if (it.size() == 11){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                            item4 = it.documents[3].getString("subjectTitle")!!; subjectID4 = it.documents[3].getString("subjectID")!!
                            item5 = it.documents[4].getString("subjectTitle")!!; subjectID5 = it.documents[4].getString("subjectID")!!
                            item6 = it.documents[5].getString("subjectTitle")!!; subjectID6 = it.documents[5].getString("subjectID")!!
                            item7 = it.documents[6].getString("subjectTitle")!!; subjectID7 = it.documents[6].getString("subjectID")!!
                            item8 = it.documents[7].getString("subjectTitle")!!; subjectID8 = it.documents[7].getString("subjectID")!!
                            item9 = it.documents[8].getString("subjectTitle")!!; subjectID9 = it.documents[8].getString("subjectID")!!
                            item10 = it.documents[9].getString("subjectTitle")!!; subjectID10 = it.documents[9].getString("subjectID")!!
                            item11 = it.documents[10].getString("subjectTitle")!!; subjectID11 = it.documents[10].getString("subjectID")!!
                        }
                        if (it.size() == 10){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                            item4 = it.documents[3].getString("subjectTitle")!!; subjectID4 = it.documents[3].getString("subjectID")!!
                            item5 = it.documents[4].getString("subjectTitle")!!; subjectID5 = it.documents[4].getString("subjectID")!!
                            item6 = it.documents[5].getString("subjectTitle")!!; subjectID6 = it.documents[5].getString("subjectID")!!
                            item7 = it.documents[6].getString("subjectTitle")!!; subjectID7 = it.documents[6].getString("subjectID")!!
                            item8 = it.documents[7].getString("subjectTitle")!!; subjectID8 = it.documents[7].getString("subjectID")!!
                            item9 = it.documents[8].getString("subjectTitle")!!; subjectID9 = it.documents[8].getString("subjectID")!!
                            item10 = it.documents[9].getString("subjectTitle")!!; subjectID10 = it.documents[9].getString("subjectID")!!
                        }
                        if (it.size() == 9){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                            item4 = it.documents[3].getString("subjectTitle")!!; subjectID4 = it.documents[3].getString("subjectID")!!
                            item5 = it.documents[4].getString("subjectTitle")!!; subjectID5 = it.documents[4].getString("subjectID")!!
                            item6 = it.documents[5].getString("subjectTitle")!!; subjectID6 = it.documents[5].getString("subjectID")!!
                            item7 = it.documents[6].getString("subjectTitle")!!; subjectID7 = it.documents[6].getString("subjectID")!!
                            item8 = it.documents[7].getString("subjectTitle")!!; subjectID8 = it.documents[7].getString("subjectID")!!
                            item9 = it.documents[8].getString("subjectTitle")!!; subjectID9 = it.documents[8].getString("subjectID")!!
                        }
                        if (it.size() == 8){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                            item4 = it.documents[3].getString("subjectTitle")!!; subjectID4 = it.documents[3].getString("subjectID")!!
                            item5 = it.documents[4].getString("subjectTitle")!!; subjectID5 = it.documents[4].getString("subjectID")!!
                            item6 = it.documents[5].getString("subjectTitle")!!; subjectID6 = it.documents[5].getString("subjectID")!!
                            item7 = it.documents[6].getString("subjectTitle")!!; subjectID7 = it.documents[6].getString("subjectID")!!
                            item8 = it.documents[7].getString("subjectTitle")!!; subjectID8 = it.documents[7].getString("subjectID")!!
                        }
                        if (it.size() == 7){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                            item4 = it.documents[3].getString("subjectTitle")!!; subjectID4 = it.documents[3].getString("subjectID")!!
                            item5 = it.documents[4].getString("subjectTitle")!!; subjectID5 = it.documents[4].getString("subjectID")!!
                            item6 = it.documents[5].getString("subjectTitle")!!; subjectID6 = it.documents[5].getString("subjectID")!!
                            item7 = it.documents[6].getString("subjectTitle")!!; subjectID7 = it.documents[6].getString("subjectID")!!
                        }
                        if (it.size() == 6){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                            item4 = it.documents[3].getString("subjectTitle")!!; subjectID4 = it.documents[3].getString("subjectID")!!
                            item5 = it.documents[4].getString("subjectTitle")!!; subjectID5 = it.documents[4].getString("subjectID")!!
                            item6 = it.documents[5].getString("subjectTitle")!!; subjectID6 = it.documents[5].getString("subjectID")!!
                        }
                        if (it.size() == 5){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                            item4 = it.documents[3].getString("subjectTitle")!!; subjectID4 = it.documents[3].getString("subjectID")!!
                            item5 = it.documents[4].getString("subjectTitle")!!; subjectID5 = it.documents[4].getString("subjectID")!!
                        }
                        if (it.size() == 4){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                            item4 = it.documents[3].getString("subjectTitle")!!; subjectID4 = it.documents[3].getString("subjectID")!!
                        }
                        if (it.size() == 3){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                            item3 = it.documents[2].getString("subjectTitle")!!; subjectID3 = it.documents[2].getString("subjectID")!!
                        }
                        if (it.size() == 2){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                            item2 = it.documents[1].getString("subjectTitle")!!; subjectID2 = it.documents[1].getString("subjectID")!!
                        }
                        if (it.size() == 1){
                            item1 = it.documents[0].getString("subjectTitle")!!; subjectID1 = it.documents[0].getString("subjectID")!!
                        }

                        when(it.size()){
                            1 -> array = arrayOf(item1)
                            2 -> array = arrayOf(item1, item2)
                            3 -> array = arrayOf(item1, item2, item3)
                            4 -> array = arrayOf(item1, item2, item3, item4)
                            5 -> array = arrayOf(item1, item2, item3, item4, item5)
                            6 -> array = arrayOf(item1, item2, item3, item4, item5, item6)
                            7 -> array = arrayOf(item1, item2, item3, item4, item5, item6, item7)
                            8 -> array = arrayOf(item1, item2, item3, item4, item5, item6, item7, item8)
                            9 -> array = arrayOf(item1, item2, item3, item4, item5, item6, item7, item8, item9)
                            10 -> array = arrayOf(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)
                            11 -> array = arrayOf(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11)
                            12 -> array = arrayOf(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item12)
                        }
                    }
                }
            }
        }
        db.enableNetwork()

        textView.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.selectASubject)
                .setItems(array) { dialog, which ->
                    when (which) {
                        0 -> {textView.text = item1; subjectID = subjectID1}
                        1 -> {textView.text = item2; subjectID = subjectID2}
                        2 -> {textView.text = item3; subjectID = subjectID3}
                        3 -> {textView.text = item4; subjectID = subjectID4}
                        4 -> {textView.text = item5; subjectID = subjectID5}
                        5 -> {textView.text = item6; subjectID = subjectID6}
                        6 -> {textView.text = item7; subjectID = subjectID7}
                        7 -> {textView.text = item8; subjectID = subjectID8}
                        8 -> {textView.text = item9; subjectID = subjectID9}
                        9 -> {textView.text = item10; subjectID = subjectID10}
                        10 -> {textView.text = item11; subjectID = subjectID11}
                        11 -> {textView.text = item12; subjectID = subjectID12}
                    }
                    db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                        val phase = user.getString("Phase")!!
                        val period = user.getString("Period")!!
                        db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").document(subjectID).get(Source.CACHE).addOnSuccessListener {
                            subjectIDNew = it.getString("subjectID")!!
                            getSubjectList.getSubjectList(subjectIDNew)
                        }
                    }
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.createNewSubject) { dialog, _ ->
                    dialog.dismiss()
                    if(int == R.id.scheduleToNewSubject){
                        onDismissDialog?.dismissDialog()
                    }
                    findNavController(fragment).navigate(int)
                }
                .show()
        }
    }
}