package com.example.finder.Remote

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.finder.R

class CustomAdapter(val context: Activity, val element: Array<String>, val img: Array<Int>) : BaseAdapter(){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list, null, true)

        val elementText = rowView.findViewById(R.id.textView) as TextView
        val imgText = rowView.findViewById(R.id.icon) as ImageView

        elementText.setText(element[position])
        imgText.setImageResource(img[position])

        return rowView

    }

    override fun getItem(position: Int): Any {
        return element[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return element.size
    }

}