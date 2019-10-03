package com.cmag704.CompSci335A1.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cmag704.CompSci335A1.News

import com.cmag704.CompSci335A1.R
import com.cmag704.CompSci335A1.getNews
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.cmag704.CompSci335A1.NewsStory
import kotlinx.coroutines.*


class NewsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater!!.inflate(R.layout.fragment_news, container, false)

        val news = ArrayList<News>()

        print(savedInstanceState)

        val viewManager = LinearLayoutManager(context)
        recyclerView = v.findViewById<RecyclerView>(R.id.recyclerView)
        val c = v.findViewById<CardView>(R.id.card_view)
        val onItemClickListener = object : NewsRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                println("A")
                val intent = Intent(activity?.applicationContext, NewsStory::class.java)
                val b = Bundle()
                b.putString("title", news[position].title)
                b.putString("description", news[position].description)
                b.putString("imageUrl", news[position].imageUrl)
                b.putString("storyUrl", news[position].storyUrl)
                b.putString("date", news[position].date)
                intent.putExtras(b) //Put your id to your next Intent
                startActivity(intent)
            }
        }

        recyclerView.apply {
            val a = NewsRecyclerAdapter(news)
            a.setOnItemClickListener(onItemClickListener)

            layoutManager = viewManager
            adapter = a
        }

        val swipeRefresh = v.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener {
            recyclerView.adapter?.notifyDataSetChanged()
            swipeRefresh.isRefreshing = false
        }

            GlobalScope.launch {
                getNews(news)
                withContext(Dispatchers.Main){
                    recyclerView.adapter?.notifyDataSetChanged()
                }
            }

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(): NewsFragment = NewsFragment()
    }
}