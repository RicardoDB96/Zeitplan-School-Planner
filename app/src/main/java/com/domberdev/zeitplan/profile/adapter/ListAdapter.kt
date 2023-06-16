package com.domberdev.zeitplan.profile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.domberdev.zeitplan.R
import com.domberdev.zeitplan.profile.model.ListData

class ListAdapter(var context: Context, var data: ArrayList<ListData>): BaseAdapter(){

    override fun getCount(): Int {
        return data.count()
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View?
        val viewHolder: ViewHolder

        if (convertView == null){
            val layout = LayoutInflater.from(context)
            view = layout.inflate(R.layout.profile_options_list_subtitle, convertView, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        val data: ListData = getItem(position) as ListData
        viewHolder.title.text = data.title
        viewHolder.subtitle.text = data.subtitle
        viewHolder.icon.setImageDrawable(data.icon)

        return view as View
    }

    private class ViewHolder(row: View?){
        var title: TextView = row?.findViewById(R.id.profileOptionsTitle) as TextView
        var subtitle: TextView = row?.findViewById(R.id.profileOptionsSubtitle) as TextView
        var icon: ImageView = row?.findViewById(R.id.profileOptionsIcon) as ImageView
    }
}