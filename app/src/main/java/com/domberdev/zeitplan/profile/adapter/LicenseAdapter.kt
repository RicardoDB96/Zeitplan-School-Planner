package com.domberdev.zeitplan.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.profile.model.Licenses
import kotlinx.android.synthetic.main.open_source_license_list.view.*

class LicenseAdapter(private val licenses: List<Licenses>, private val onItemClickListener: OnLicenseListener): RecyclerView.Adapter<LicenseAdapter.ViewHolder>() {

    interface OnLicenseListener{
        fun onLicenseClick(Url: String)
        fun onWebsiteClick(Url: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.open_source_license_list, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(licenses[position])
    }

    override fun getItemCount(): Int {
        return licenses.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindData(licenses: Licenses){
            itemView.openSourceLibraryTitle.text = licenses.licensesTitle
            itemView.licenseButton.setOnClickListener { onItemClickListener.onLicenseClick(licenses.licensesUrl) }
            itemView.websiteButton.setOnClickListener { onItemClickListener.onWebsiteClick(licenses.licensesWebsite) }
        }
    }
}