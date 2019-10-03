package com.cmag704.CompSci335A1.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cmag704.CompSci335A1.Display

import com.cmag704.CompSci335A1.R
import com.cmag704.CompSci335A1.getDisplays
import com.cmag704.CompSci335A1.getNews
import kotlinx.coroutines.*
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory


class DisplaysFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var displays = ArrayList<Display>()
        val v = inflater!!.inflate(R.layout.fragment_displays, container, false)

        val viewManager = LinearLayoutManager(context)

        recyclerView = v.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.apply {
            layoutManager = viewManager
            adapter = DisplaysRecyclerAdapter(displays)

        }

        val swipeRefresh = v.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener {
            recyclerView.adapter?.notifyDataSetChanged()
            swipeRefresh.isRefreshing = false
        }

        GlobalScope.launch {
            getDisplays(displays)
            withContext(Dispatchers.Main){
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }

        return v
    }

    companion object {
        fun newInstance(): DisplaysFragment = DisplaysFragment()
    }
}