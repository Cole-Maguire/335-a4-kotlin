package com.cmag704.CompSci335A1.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView

import com.cmag704.CompSci335A1.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cmag704.CompSci335A1.BASEURL
import com.cmag704.CompSci335A1.CommentPost



class CommentsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(R.layout.fragment_comment, container, false)
        val mWebView = v.findViewById<WebView>(R.id.commentWebView)
        mWebView.loadUrl("$BASEURL/htmlcomments")


        val swipeRefresh = v.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        swipeRefresh.apply {
            setOnRefreshListener {
                mWebView.reload()
                mWebView.scrollY=0
                swipeRefresh.isRefreshing = false
            }
        }
        val fab: FloatingActionButton = v.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(activity?.applicationContext, CommentPost::class.java)
            startActivity(intent)
        }

        return v
    }

    companion object {
        fun newInstance(): CommentsFragment = CommentsFragment()
    }


}