package com.cmag704.CompSci335A1.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cmag704.CompSci335A1.*
import kotlinx.coroutines.*


class ShopFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v = inflater!!.inflate(R.layout.fragment_shop, container, false)

        print(savedInstanceState)

        val shopItems = ArrayList<ShopItem>()
        val viewManager = LinearLayoutManager(context)
        recyclerView = v.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.apply {
            layoutManager = viewManager
            adapter = ShopRecyclerAdapter(shopItems)
        }

        val swipeRefresh = v.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener {
            recyclerView.adapter?.notifyDataSetChanged()
            swipeRefresh.isRefreshing = false
        }




        GlobalScope.launch {
            getShopItems(shopItems)
            withContext(Dispatchers.Main) {
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }

        return v
    }

    companion object {
        fun newInstance(): ShopFragment = ShopFragment()
    }
}