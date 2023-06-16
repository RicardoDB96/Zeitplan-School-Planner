package com.domberdev.zeitplan.subjects

import android.content.Context
import android.widget.Toast
import com.domberdev.zeitplan.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class CheckSubjectExistence {

    val db = FirebaseFirestore.getInstance()

    fun checkSubjectID(subjectID: TextInputEditText, context: Context, sID: String){
        var id = sID
        db.collection("Tasks").whereEqualTo("subjectID", id)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    if (task.result.size() != 0){
                        subjectID.setText(id)
                    }else{
                        id = "#" + getRandomString()
                        subjectID.setText(id)
                    }
                }else{
                    Toast.makeText(context, R.string.errorWhenTryingCreateASubject, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getRandomString() : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..20)
            .map { allowedChars.random() }
            .joinToString("")
    }
}