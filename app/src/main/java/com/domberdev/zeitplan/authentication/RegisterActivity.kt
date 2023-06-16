package com.domberdev.zeitplan.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.util.Linkify
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.domberdev.zeitplan.MainActivity
import com.domberdev.zeitplan.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        registerUser()
        backToLogin()
        checkBoxLinks()
        registerWGoogle()
    }

    //Si el usuario presiona Back, lo manda a la activity LoginUser
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LoginUser::class.java)
        startActivity(intent)
        finish()
    }

    //Funcion que oculta el teclado, al mismo tiempo que quita el foco a los TextFields.
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            fullNameRegisterLayout.clearFocus()
            emailRegisterLayout.clearFocus()
            passwordRegisterLayout.clearFocus()
            confirmPasswordRegisterLayout.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

    //Boton que verifica los datos, marca cuando haya algun error y registra el usuario para que posteriormente pueda registrarse con este.
    private fun registerUser() {
        singin.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            fullNameRegisterLayout.error = null
            emailRegisterLayout.error = null
            passwordRegisterLayout.error = null
            confirmPasswordRegisterLayout.error = null
            checkTaCAndPP.error = null
            if (fullNameRegister.text!!.isNotEmpty() && emailRegister.text!!.isNotEmpty() && passwordRegister.text!!.isNotEmpty() && confirmPasswordRegister.text!!.isNotEmpty() /*&& checkTaCAndPP.isChecked*/) {
                if (passwordRegister.text.toString() == confirmPasswordRegister.text.toString()){
                    auth.useAppLanguage()
                    auth.createUserWithEmailAndPassword(
                        emailRegister.text.toString(),
                        passwordRegister.text.toString())
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                FirebaseFirestore.getInstance().collection("Users").document(emailRegister.text.toString())
                                    .set(mapOf(
                                        "Name" to fullNameRegister.text.toString(),
                                        "Email" to emailRegister.text.toString(),
                                        "Phase" to "1",
                                        "Period" to "1"
                                    ))
                                val intent = Intent(this, LoginUser::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                        .addOnFailureListener {
                            val message = when (it.message){
                                "The email address is badly formatted." -> getString(R.string.theEmailAddressIsInvalid)
                                "The given password is invalid. [ Password should be at least 6 characters ]" -> getString(R.string.theGivenPasswordIsInvalid)
                                "The email address is already in use by another account." -> getString(R.string.theEmailAddressIsAlreadyInUseByAnotherAccount)
                                "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> getString(R.string.aNetworkErrorHasOccurred)
                                else -> "Error"
                            }
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        }
                }else{
                    confirmPasswordRegisterLayout.error = getString(R.string.passwordsAreDifferent)
                }
            }
            if (fullNameRegister.text!!.isEmpty()){
                fullNameRegisterLayout.error = getString(R.string.enterAFullName)
            }
            if (emailRegister.text!!.isEmpty()){
                emailRegisterLayout.error = getString(R.string.enterAEmail)
            }
            if (passwordRegister.text!!.isEmpty()){
                passwordRegisterLayout.error = getString(R.string.enterAPassword)
            }   
            if (confirmPasswordRegister.text!!.isEmpty()){
                confirmPasswordRegisterLayout.error = getString(R.string.enterAConfirmPassword)
            }
        }
    }

    private fun registerWGoogle() {
        registerWGoogle.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("42055559539-l27bkdgguvjvh3km12h2j3jatedqfbip.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            Firebase.auth.signOut()
            googleClient.signOut()

            resultLauncher.launch(googleClient.signInIntent)
        }
    }

    //Controla la apertura para seleccionar las cuentas de google registradas en el dispositivo con Google.
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data

            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)

            try {
                val account = task.getResult(ApiException::class.java)

                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                Firebase.auth.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val acct = GoogleSignIn.getLastSignedInAccount(this)

                        val name = acct?.displayName.toString()
                        val email = acct?.email!!

                        Firebase.firestore.enableNetwork().addOnSuccessListener {
                            Firebase.firestore.collection("Users").document(email).get().addOnSuccessListener { user ->
                                if (!user.exists()){
                                    Firebase.firestore.collection("Users").document(email).set(
                                        hashMapOf(
                                            "Email" to email,
                                            "Name" to name,
                                            "Phase" to "1",
                                            "Period" to "1"
                                        )
                                    )
                                    val prefs = getSharedPreferences("userdata", Context.MODE_PRIVATE).edit()
                                    val intentMain = Intent(this, MainActivity::class.java)
                                    prefs.putString("email", email)
                                    prefs.putBoolean("showTasks", false)
                                    prefs.putBoolean("roundGrades", false)
                                    prefs.putBoolean("getAllData", true)
                                    prefs.apply()
                                    startActivity(intentMain)
                                    finish()
                                } else{
                                    Toast.makeText(this, getString(R.string.theEmailAddressIsAlreadyInUseByAnotherAccount), Toast.LENGTH_LONG).show()
                                }
                            }.addOnFailureListener {
                                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.googleLogCouldNotStarted), Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, getString(R.string.googleLogCouldNotStarted), Toast.LENGTH_LONG).show()
                }
            } catch (e: ApiException) {
                println(e.message)
            }
        }
    }

    //Boton que abre la Activity de Login para el el usuario vuelva a esa Activity.
    private fun backToLogin(){
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginUser::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkBoxLinks(){
        val termsAndConditionsMatcher = Pattern.compile(getString(R.string.TaC))
        val privacyPolicyMatcher = Pattern.compile(getString(R.string.privacyPolicy))

        Linkify.addLinks(checkTaCAndPP, termsAndConditionsMatcher, "terms:")
        Linkify.addLinks(checkTaCAndPP, privacyPolicyMatcher, "privacy:")
    }
}