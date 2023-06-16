package com.domberdev.zeitplan.authentication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.domberdev.zeitplan.R
import kotlinx.android.synthetic.main.activity_legal_things.*

class LegalThingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_legal_things)
        when (intent.dataString) {
            "terms:${getString(R.string.TaC)}" -> {
                legalThingsWeb.loadUrl("https://github.com/RicardoDB96/Zeitplan-Agenda-Escolar/blob/main/Terminos.md")
            }
            "privacy:${getString(R.string.privacyPolicy)}" -> {
                legalThingsWeb.loadUrl("https://github.com/RicardoDB96/Zeitplan-Agenda-Escolar/blob/main/Privacidad.md")
            }
            else -> {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}