package com.saddam.asuriontest

import android.content.Context

import android.support.v7.widget.AppCompatImageView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView



class PetsListAdapter(val context: Context?, var   imgList: MutableMap<String, PetsModel>?) : BaseAdapter() {

    internal var adapterList = mutableMapOf<String, PetsModel>()



    init {

       adapterList = imgList!!

    }

    override fun getItem(i: Int): PetsModel {
        return adapterList.values.elementAt(i)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return adapterList.size
    }


    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View? {
        val view: View?
        val vh: ListRowHolder
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_pets, viewGroup, false)
            vh = ListRowHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ListRowHolder
        }

         vh.text_PetName.text =adapterList. values.elementAt(position).petTitle

        vh.img_PetPic.setImageBitmap(adapterList. values.elementAt(position).bm)


        return view
    }
}




class ListRowHolder(row: View?) {
    val text_PetName: TextView
    val img_PetPic: AppCompatImageView


    init {
        this.text_PetName = row?.findViewById(R.id.text_PetName) as TextView
        this.img_PetPic = row?.findViewById(R.id.img_PetPic) as AppCompatImageView


    }

}