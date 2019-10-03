package com.cmag704.CompSci335A1.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmag704.CompSci335A1.R
import java.util.ArrayList
import com.cmag704.CompSci335A1.News
import com.cmag704.CompSci335A1.getImage
import kotlinx.android.synthetic.main.card_news.view.*
import kotlinx.coroutines.*


class NewsRecyclerAdapter(private val mDataList: ArrayList<News>):
    RecyclerView.Adapter<NewsRecyclerAdapter.MyViewHolder>() {
    lateinit var itemClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_news, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = mDataList[position].title

        GlobalScope.launch(Dispatchers.Main) {
            val bmp = withContext(Dispatchers.IO) {
                getImage(mDataList[position].imageUrl)
            }
            holder.img.setImageBitmap(bmp)
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var title: TextView
        internal var img: ImageView

        override fun onClick(v: View?) {
            itemClickListener.onItemClick(itemView, adapterPosition)
        }

        init {
            title = itemView.findViewById<View>(R.id.news_title) as TextView
            img = itemView.findViewById<View>(R.id.thumbnail) as ImageView
            itemView.card_view.setOnClickListener(this)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}