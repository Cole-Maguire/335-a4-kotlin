package com.cmag704.CompSci335A1

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.cmag704.CompSci335A1.ui.main.FragmentAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        print(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = FragmentAdapter(supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 1
        viewPager.isSaveFromParentEnabled = false


        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

    }

    fun shopButtonClick(v: View) {

        val p = v.parent as View
        val itemId = p.findViewById<TextView>(R.id.itemId).text

        if (!isLoggedIn(this)) {
            Toast.makeText(
                this@MainActivity,
                "ERROR: Please login and try again",
                Toast.LENGTH_LONG
            ).show()
            return
        } else {
            val ha1 = getFileContents(this, LogFiles.HA1)
            val username = getFileContents(this, LogFiles.USERNAME)

            GlobalScope.launch {
                val (_, messageXml, _) = secureRequest(
                    "buy?id=$itemId",
                    username = username,
                    ha1In = ha1
                )

                val message = readXml(messageXml).getElementsByTagName("string").item(0).textContent

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


        }
    }

}