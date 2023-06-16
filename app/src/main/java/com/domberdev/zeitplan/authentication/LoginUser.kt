package com.domberdev.zeitplan.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.domberdev.zeitplan.MainActivity
import com.domberdev.zeitplan.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login_user.*

class LoginUser : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        darkModeActivateStatus()
        setTheme(R.style.FullFragmentTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_user)
        supportActionBar?.hide()
        session()
        loginUser()
        loginWGoogle()
        registerNewUser()
        forgotPassword()
    }

    //Muestra el Layout si no se encontro un correo con el cual iniciar sesion.
    override fun onStart() {
        super.onStart()
        authLayout.visibility = View.VISIBLE
    }

    //Funcion que reconoce si el usuario ya se loggeo anteriormente y inicia la sesion automaticamente.
    private fun session() {
        val prefs = getSharedPreferences("userdata", Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        if (email != null) {
            authLayout.visibility = View.INVISIBLE
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun darkModeActivateStatus() {
        val status = getSharedPreferences("userdata", MODE_PRIVATE).getInt("modeStatus", -1)
        AppCompatDelegate.setDefaultNightMode(status)
    }

    //Funcion que oculta el teclado, al mismo tiempo que quita el foco a los TextFields.
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            emailLayout.clearFocus()
            passwordLayout.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

    //Boton para iniciar sesion, que verifica que los datos sean correctos.
    private fun loginUser() {
        login.setOnClickListener {
            emailLayout.error = null
            passwordLayout.error = null
            if (email.text!!.isEmpty()) {
                emailLayout.error = getString(R.string.enterAEmail)
            }
            if (password.text!!.isEmpty()) {
                passwordLayout.error = getString(R.string.enterAPassword)
            }
            if (email.text!!.isNotEmpty() && password.text!!.isNotEmpty()) {
                Firebase.auth.signInWithEmailAndPassword(
                    email.text.toString(),
                    password.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val prefs = getSharedPreferences("userdata", Context.MODE_PRIVATE).edit()
                        val intent = Intent(this, MainActivity::class.java)
                        prefs.putString("email", email.text.toString())
                        prefs.putBoolean("showTasks", false)
                        prefs.putBoolean("roundGrades", false)
                        prefs.putBoolean("getAllData", true)
                        prefs.apply()
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            getString(R.string.errorLogInIncorrect),
                            Toast.LENGTH_LONG
                        ).show() //Mensaje cuando el usuario pone un correo o contraseña invalida.
                    }
                }
            }
        }
    }

    //Botón que invoca la actividad de autenticacion de Google.
    private fun loginWGoogle() {
        LoginWGoogle.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("42055559539-l27bkdgguvjvh3km12h2j3jatedqfbip.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
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
                            val email = acct?.email!!

                            Firebase.firestore.enableNetwork().addOnSuccessListener {
                                Firebase.firestore.collection("Users").document(email).update("Email", email).addOnSuccessListener {
                                    val prefs = getSharedPreferences("userdata", Context.MODE_PRIVATE).edit()
                                    val intentMain = Intent(this, MainActivity::class.java)
                                    prefs.putString("email", email)
                                    prefs.putBoolean("showTasks", false)
                                    prefs.putBoolean("roundGrades", false)
                                    prefs.putBoolean("getAllData", true)
                                    prefs.apply()
                                    startActivity(intentMain)
                                    finish()
                                }.addOnFailureListener {
                                    Toast.makeText(this, getString(R.string.notRegisteredAccountGoogle), Toast.LENGTH_LONG).show()
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

    //Abre la Activity de registrar un nuevo usuario.
    private fun registerNewUser() {
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //Botón que controla el envio del correo de recuperacion de contraseña(Agregar un AlertDialog cuando pueda)
    private fun forgotPassword() {
        emailLayout.error = null
        val auth = Firebase.auth
        val emailForgot = email.text
        auth.useAppLanguage() //Recupera el idioma del dispositivo y lo usa para mandar el mail con el respectivo idioma.
        forgotPassword.setOnClickListener {
            if (email.text!!.isEmpty()) {
                emailLayout.error = getString(R.string.enterEmailToRecoveryPassword) //Mensaje de error cuando no hay un correo que utilizar para recuperar la contraseña.
            } else {
                auth.sendPasswordResetEmail(emailForgot.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle(getString(R.string.recoveryPassword))
                                .setMessage(getString(R.string.recoveryPasswordMessage))
                                .setPositiveButton(R.string.ok) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show() //Mensaje de notificacion para notificar el envio del correo de recuperacion al usuario.
                        }
                    }
            }
        }
    }
}