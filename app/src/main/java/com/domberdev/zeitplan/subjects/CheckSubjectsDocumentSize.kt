package com.domberdev.zeitplan.subjects

import android.app.AlertDialog
import android.content.Context
import com.domberdev.zeitplan.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class CheckSubjectsDocumentSize(private val context: Context, private val createNewSubject: CreateNewSubject, private val createOrJoin: Int) {

    private val db = FirebaseFirestore.getInstance()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email!!

    interface CreateNewSubject{
        fun createNewSubject()
        fun joinToSubject()
    }

    fun checkSubjectSize(){
        db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
            val phase = user.getString("Phase")!!
            val period = user.getString("Period")!!
            db.collection("Users/$userEmail/Phase$phase/Period$period/Subjects").get(Source.CACHE).addOnSuccessListener { subjects ->
                println(subjects.size())
                if (subjects.size() == 12){
                    val dialog = AlertDialog.Builder(context)
                    dialog.setTitle(R.string.youCannotCreateMore12Subjects)
                    dialog.setMessage(R.string.youHaveReachedDeleteOnesCurrentlyHave)
                    dialog.setPositiveButton(R.string.ok) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    dialog.show()
                }else{
                    if (createOrJoin == 0){
                        createNewSubject.createNewSubject()
                    }else if (createOrJoin == 1)
                        createNewSubject.joinToSubject()
                }
            }
        }
    }
}