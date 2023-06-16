package com.domberdev.zeitplan

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)
        mainBottomNavigation.setupWithNavController(Navigation.findNavController(this, R.id.MainFragment))
        supportActionBar?.hide()
        setUpNav()
        setUpSystemUI()
    }

    //Función que oculta el teclado, al mismo tiempo que quita el foco a los TextFields.
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus?.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun setUpNav(){
        findNavController(R.id.MainFragment).addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.homeFragment-> {
                    showBottomNav()
                    window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)
                }
                R.id.taskFragment-> {
                    showBottomNav()
                    window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)
                }
                R.id.scheduleFragment-> {
                    showBottomNav()
                    window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)
                }
                R.id.profileFragment-> {
                    showBottomNav()
                    window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)
                }
                else -> {
                    hideBottomNav()
                }
            }
        }
    }

    private fun setUpSystemUI(){
        findNavController(R.id.MainFragment).addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.newSubjectFragment -> fullFragmentTheme()
                R.id.editSubjectFragment -> fullFragmentTheme()
                R.id.joinToSubjectFragment -> fullFragmentTheme()
                R.id.createNewTaskFragment -> fullFragmentTheme()
                R.id.editTaskFragment -> fullFragmentTheme()
                R.id.taskInfoFragment -> fullFragmentTheme()
                R.id.newGradeFragment -> fullFragmentTheme()
                R.id.editGradeFragment -> fullFragmentTheme()
                else -> {
                    val decorView = window.decorView
                    val wic = WindowInsetsControllerCompat(window, decorView)
                    window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)
                    wic.isAppearanceLightStatusBars = false
                }
            }
        }
    }

    private fun fullFragmentTheme(){
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M){
            window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)
        }else{
            when (this.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    val decorView = window.decorView
                    val wic = WindowInsetsControllerCompat(window, decorView)

                    wic.isAppearanceLightStatusBars = true
                }
            }
            window.statusBarColor = ContextCompat.getColor(this, R.color.backgroundColor)
        }
    }

    private fun hideBottomNav() {
        mainBottomNavigation.visibility = View.GONE
    }

    private fun showBottomNav() {
        mainBottomNavigation.visibility = View.VISIBLE
    }
}
