package com.domberdev.zeitplan.profile.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.authentication.LoginUser
import com.domberdev.zeitplan.profile.adapter.ListAdapter
import com.domberdev.zeitplan.profile.model.ListData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_configuration.*
import java.io.File

class ConfigurationFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()
    val userEmail = Firebase.auth.currentUser?.email!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_configuration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurationToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        listOptions()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun listOptions(){
        val arrayList = ArrayList<ListData>()

        val status = this.activity?.getSharedPreferences("userdata", Context.MODE_PRIVATE)?.getInt("darkModeStatus", 2)

        val darkModeStatus = when(status!!){
            0 -> getString(R.string.lightMode)
            1 -> getString(R.string.darkMode)
            2 -> getString(R.string.systemDefault)
            else -> getString(R.string.systemDefault)
        }

        arrayList.add(ListData(getString(R.string.general),getString(R.string.generalConfig), context?.getDrawable(R.drawable.ic_configuration)))
        //arrayList.add(ListData(getString(R.string.notifications), getString(R.string.notificationsConfig), context?.getDrawable(R.drawable.ic_notifications)))
        arrayList.add(ListData(getString(R.string.theme), darkModeStatus, context?.getDrawable(R.drawable.ic_theme_mode)))
        arrayList.add(ListData(getString(R.string.about), getString(R.string.app_name), context?.getDrawable(R.drawable.ic_info)))
        arrayList.add(ListData(getString(R.string.openSourceLibraries), getString(R.string.openSourceLicense), context?.getDrawable(R.drawable.ic_code)))
        arrayList.add(ListData(getString(R.string.LogOut), getString(R.string.closeCurrentSession), context?.getDrawable(R.drawable.ic_logout)))

        val adapter = ListAdapter(requireContext(), arrayList)

        profileOptions.adapter = adapter

        profileOptions.setOnItemClickListener { _, _, i, _ ->
            when(i){
                0 -> generalSetting()
                1 -> darkMode()
                2 -> aboutZeitplan()
                3 -> licenses()
                4 -> logOut()
            }
        }
    }

    private fun generalSetting(){
        findNavController().navigate(R.id.configurationToGeneralSettings)
    }

    private fun darkMode(){
        val darkModeOptions = arrayOf(getString(R.string.lightMode), getString(R.string.darkMode), getString(
            R.string.systemDefault
        ))

        val darkMode = this.activity?.getSharedPreferences("userdata", Context.MODE_PRIVATE)

        val status = darkMode?.getInt("darkModeStatus", 2)

        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle(R.string.theme)
        dialog.setSingleChoiceItems(darkModeOptions, status!!) { d, i ->
            when(i){
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    darkMode.edit()?.putInt("darkModeStatus", 0)?.apply()
                    darkMode.edit()?.putInt("modeStatus", 1)?.apply()
                    d.dismiss()
                    listOptions()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    darkMode.edit()?.putInt("darkModeStatus", 1)?.apply()
                    darkMode.edit()?.putInt("modeStatus", 2)?.apply()
                    d.dismiss()
                    listOptions()
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    darkMode.edit()?.putInt("darkModeStatus", 2)?.apply()
                    darkMode.edit()?.putInt("modeStatus", -1)?.apply()
                    d.dismiss()
                    listOptions()
                }
            }
        }
        dialog.setNegativeButton(R.string.cancel) { d, _ ->
            d.dismiss()
        }
        dialog.show()
    }

    private fun aboutZeitplan(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.aboutZeitplan)
        builder.setMessage(R.string.zeitplanAbout)
        builder.setPositiveButton(R.string.close) { _, _ ->
        }
        builder.show()
    }

    private fun licenses(){
        findNavController().navigate(R.id.configurationToLicensesFragment)
    }

    private fun logOut(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.LogOut)
        builder.setMessage(R.string.logOutMessage)
        builder.setPositiveButton(R.string.ok){ _, _ ->
            val db = Firebase.firestore
            db.enableNetwork().addOnCompleteListener {
                db.collection("Users").document(userEmail).get(Source.CACHE).addOnSuccessListener { user ->
                    val currentPeriod = user.get("Current")
                    db.collection("Users").document(userEmail).update("Period", currentPeriod).addOnCompleteListener {
                        Firebase.auth.signOut()

                        val prefs = this.activity?.getSharedPreferences("userdata", Context.MODE_PRIVATE)?.edit()
                        prefs?.clear()
                        prefs?.apply()

                        val intent = Intent(context, LoginUser::class.java)
                        clearApplicationData()
                        Firebase.firestore.terminate()
                        Firebase.firestore.clearPersistence()
                        startActivity(intent)
                        activity?.finish()
                    }
                }
            }
        }
        builder.setNegativeButton(R.string.cancel){ _, _ -> }
        builder.show()
    }

    private fun clearApplicationData() {
        val cache: File = requireContext().cacheDir
        val appDir = File(cache.parent!!)
        if (appDir.exists()) {
            val children = appDir.list()!!
            for (s in children) {
                if (s != "lib") {
                    deleteDir(File(appDir, s))
                    Log.i("TAG", "File /data/data/APP_PACKAGE/$s DELETED ")
                }
            }
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()!!
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        return dir!!.delete()
    }
}