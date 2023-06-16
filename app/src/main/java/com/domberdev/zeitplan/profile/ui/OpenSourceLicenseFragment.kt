package com.domberdev.zeitplan.profile.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.profile.adapter.LicenseAdapter
import com.domberdev.zeitplan.profile.model.Licenses
import kotlinx.android.synthetic.main.fragment_open_source_license.*

class OpenSourceLicenseFragment : Fragment(), LicenseAdapter.OnLicenseListener {

    var licenses: List<Licenses> = listOf(
        Licenses("FloatingActionButton", "https://github.com/Clans/FloatingActionButton/blob/master/LICENSE", "https://github.com/Clans/FloatingActionButton"),
        Licenses("Material Dialogs", "https://github.com/afollestad/material-dialogs/blob/main/LICENSE.md", "https://github.com/afollestad/material-dialogs"),
        Licenses("CircularProgressBar", "https://github.com/lopspower/CircularProgressBar/blob/master/LICENSE", "https://github.com/lopspower/CircularProgressBar"),
        Licenses("CircleImageView", "https://github.com/hdodenhof/CircleImageView/blob/master/LICENSE.txt", "https://github.com/hdodenhof/CircleImageView"),
        Licenses("Shimmer Android","https://github.com/facebook/shimmer-android/blob/main/LICENSE", "http://facebook.github.io/shimmer-android/"),
        Licenses("uCrop", "https://github.com/Yalantis/uCrop#license", "https://github.com/Yalantis/uCrop"),
        Licenses("MPAndroidChart", "https://github.com/PhilJay/MPAndroidChart/blob/master/LICENSE", "https://github.com/PhilJay/MPAndroidChart"),
        Licenses("Gson", "https://github.com/google/gson/blob/master/LICENSE", "https://github.com/google/gson"),
        Licenses("MinTimetable", "https://github.com/islandparadise14/MinTimetable/blob/master/LICENSE", "https://github.com/islandparadise14/MinTimetable")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_open_source_license, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        openLicenseToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        listOptions()
    }

    private fun listOptions(){
        licensesRV.layoutManager = LinearLayoutManager(requireContext())
        licensesRV.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
        val adapter = LicenseAdapter(licenses, this)
        licensesRV.adapter = adapter
    }

    override fun onLicenseClick(Url: String) {
        val uri = Uri.parse(Url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    override fun onWebsiteClick(Url: String) {
        val uri = Uri.parse(Url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}