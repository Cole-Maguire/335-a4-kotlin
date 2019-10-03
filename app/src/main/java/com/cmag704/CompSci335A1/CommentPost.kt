package com.cmag704.CompSci335A1

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_comment_post.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.google.android.material.snackbar.Snackbar



class CommentPost : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_post)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun postButton(v: View) {
        val p = v.parent as View
        val name = p.findViewById<EditText>(R.id.comment_name).text.toString()
        val comment = p.findViewById<EditText>(R.id.comment_text).text.toString()
        GlobalScope.launch {
            val response = postCommentRequest(name, comment)
            if (response != null){
                val snackbar = Snackbar.make(p, response, Snackbar.LENGTH_LONG)
                snackbar.show()
            }else{
                this@CommentPost.finish()
            }
        }
    }
}
