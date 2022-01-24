package com.ir.contactapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import com.ir.contactapp.Adapter.MyAdapter
import com.ir.contactapp.Myshare.MyShare
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var arrayListContacts: ArrayList<UserData>
    lateinit var myAdapter: MyAdapter
    lateinit var dialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MyShare.init(this)
        BuildRecyclerView()

        image_add_contact.setOnClickListener {
            BuildAlertDialog()
            dialog.show()
        }

        editTextSearch.addTextChangedListener {
            SearchContact(editTextSearch.text.toString())
        }
    }

    private fun SearchContact(stringName: String) {
        val arrayList = ArrayList<UserData>()
        for (i in 0 until  arrayListContacts.size) {
            if (arrayListContacts[i].name.toString().uppercase() == stringName.uppercase() ){
                arrayList.add(arrayListContacts[i])
            }
        }
        val adapter = MyAdapter(this , arrayList)
        recycler.adapter = adapter
        println(arrayList)
        println(stringName)
    }

    private fun BuildAlertDialog() {
        val alertDialog = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_contact, null, false)
        val editTextName = view.findViewById<AppCompatEditText>(R.id.editText_name)
        val editTextNumber = view.findViewById<AppCompatEditText>(R.id.editText_number)
        val btnCardSave = view.findViewById<CardView>(R.id.btn_card_save)
        btnCardSave.setOnClickListener {
            dialog.cancel()
            SaveContact(
                editTextName.text.toString().trim(),
                0,
                editTextNumber.text.toString().trim()
            )
        }
        alertDialog.setView(view)
        dialog = alertDialog.create()
        dialog.window!!.attributes.windowAnimations = R.style.MyAnimation
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun SaveContact(
        stringName: String,
        intImage: Int,
        stringNumber: String
    ) {
        arrayListContacts.add(UserData(stringName,intImage, stringNumber))
        MyShare.dataList = arrayListContacts
        myAdapter.notifyItemChanged(arrayListContacts.size)
    }

    private fun BuildRecyclerView() {
        arrayListContacts = ArrayList()
        arrayListContacts = MyShare.dataList!!
        myAdapter = MyAdapter(this, arrayListContacts)
        recycler.adapter = myAdapter
    }

}