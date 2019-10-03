package com.cmag704.CompSci335A1.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmag704.CompSci335A1.*
import java.util.ArrayList
import kotlinx.coroutines.*


class ShopRecyclerAdapter(private val mDataList: ArrayList<ShopItem>) :

    RecyclerView.Adapter<ShopRecyclerAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_shop, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = mDataList[position].title
        holder.description.text = mDataList[position].description
        holder.itemId.text = mDataList[position].itemId

        val itemId = mDataList[position].itemId
        GlobalScope.launch(Dispatchers.Main) {
            val bmp = withContext(Dispatchers.IO) {
                print("$BASEURL/shopimg?id=$itemId")
                getImage("$BASEURL/shopimg?id=$itemId")
            }
            holder.img.setImageBitmap(bmp)
        }

    }

    override fun getItemCount(): Int {
        return mDataList.size
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var title: TextView
        internal var description: TextView
        internal var img: ImageView
        internal var itemId: TextView

        init {
            title = itemView.findViewById<View>(R.id.shop_title) as TextView
            description = itemView.findViewById<View>(R.id.description) as TextView
            img = itemView.findViewById<View>(R.id.thumbnail) as ImageView
            itemId = itemView.findViewById<View>(R.id.itemId) as TextView
        }
    }
}