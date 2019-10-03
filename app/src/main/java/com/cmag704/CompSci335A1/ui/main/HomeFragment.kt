package com.cmag704.CompSci335A1.ui.main

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.cmag704.CompSci335A1.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater!!.inflate(R.layout.fragment_home, container, false)
        val img = v.findViewById<ImageView>(R.id.img)
        GlobalScope.launch(Dispatchers.Main) {
            val bmp = withContext(Dispatchers.IO) {
                getImage("http://redsox.uoa.auckland.ac.nz/ms/logo-192x192.png")
            }
            img.setImageBitmap(bmp)
        }

        val button = v.findViewById<Button>(R.id.logButton)

        setButton((activity as MainActivity), this, button)


        return v
    }

    fun setButton(a: MainActivity, fragment: HomeFragment, button: Button) {
        if (isLoggedIn(a)) {
            button.text = "Logout"
        } else {
            button.text = "Login"
        }
        a.run {
            button.setOnClickListener {
                if (isLoggedIn(this)) {
                    clearCache(this)
                    (it as Button).text = "Login"

                    Toast.makeText(
                        context,
                        "Logged Out",
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    val dialog = LoginDialog(this, fragment, it as Button)
                    dialog.show(childFragmentManager, "LoginDialog")
                }
            }
        }
    }

    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
    }

    class LoginDialog(val c: Context, val fragment: HomeFragment, val button: Button) :
        DialogFragment() {
        //Holey moley, infinite recursion is almost certainly not the right way to do this
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                val builder = AlertDialog.Builder(it)
                // Get the layout inflater
                val inflater = requireActivity().layoutInflater;
                val v = inflater.inflate(R.layout.dialog_login, null)

                builder.setView(v)
                    // Add action buttons
                    .setPositiveButton("Login")
                    { dialog, id ->
                        checkAndLog(
                            c,
                            v.findViewById<EditText>(R.id.username).text.toString(),
                            v.findViewById<EditText>(R.id.password).text.toString(),
                            button
                        )

                        fragment.setButton((activity as MainActivity), fragment, button)

                    }
                    .setNegativeButton(
                        "Cancel"
                    ) { dialog, id ->
                        button.text = "Logout"
                        dialog.cancel()
                    }
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")

        }
    }

}