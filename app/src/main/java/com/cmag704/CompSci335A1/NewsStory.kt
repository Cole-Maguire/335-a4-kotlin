package com.cmag704.CompSci335A1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import kotlinx.android.synthetic.main.activity_news_story.*

import kotlinx.android.synthetic.main.content_news_story.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Intent
import android.net.Uri
import android.view.View


class NewsStory : AppCompatActivity() {
    lateinit var storyUrl: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_story)
        setSupportActionBar(toolbar)
        val args= intent.extras

        if (args!=null){
            news_title.text = args.getString("title")
            date.text = args.getString("date")
            description.text = args.getString("description")
            storyUrl = args.getString("storyUrl")!!


            GlobalScope.launch(Dispatchers.Main) {
                val bmp = withContext(Dispatchers.IO) {
                    args?.getString("imageUrl")?.let { getImage(it) }
                }
                imageView.setImageBitmap(bmp)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun launchBrowser(v: View){
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(storyUrl)))
    }

}
