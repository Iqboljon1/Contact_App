package com.ir.contactapp

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.ir.contactapp.Adapter.MyAdapter
import com.ir.contactapp.Interface.MyOnClickListener
import com.ir.contactapp.Interface.MyOnClickListenerFromDelete
import com.ir.contactapp.db.MyDbHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var dialog: AlertDialog
    private lateinit var myDbHelper: MyDbHelper

    var booleanAntiBag = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myDbHelper = MyDbHelper(this)

        fab.supportImageTintList = ContextCompat.getColorStateList(this, R.color.white)

        fab.setOnClickListener {
            if (booleanAntiBag) {
                buildAlertDialog("", "", "Create Contact", "Add Photo", 0)
                dialog.show()
                booleanAntiBag = false
            }
        }

    }


    private fun buildAlertDialog(
        stringName: String,
        stringNumber: String,
        stringCreate: String,
        stringPhoto: String,
        id: Int,
    ) {
        val alertDialog = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_contact, null, false)
        val editTextName = view.findViewById<AppCompatEditText>(R.id.editText_name)
        val editTextNumber = view.findViewById<AppCompatEditText>(R.id.editText_number)
        val btnCardSave = view.findViewById<CardView>(R.id.btn_card_save)
        val tvCardSave = view.findViewById<TextView>(R.id.tvCreateContact)
        val tvAddPhoto = view.findViewById<TextView>(R.id.tv_addPhoto)

        tvCardSave.text = stringCreate
        tvAddPhoto.text = stringPhoto
        editTextName.setText(stringName)
        editTextNumber.setText(stringNumber)

        btnCardSave.setOnClickListener {
            dialog.cancel()
            if (stringCreate == "Create Contact") {
                saveContact(
                    editTextName.text.toString().trim(),
                    editTextNumber.text.toString().trim()
                )
            } else {
                updateContact(UserData(id,
                    editTextName.text.toString().trim(),
                    editTextNumber.text.toString().trim()))
            }
        }

        alertDialog.setOnCancelListener {
            booleanAntiBag = true
        }

        alertDialog.setView(view)
        dialog = alertDialog.create()
        dialog.window!!.attributes.windowAnimations = R.style.MyAnimation
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun updateContact(userData: UserData) {
        myDbHelper = MyDbHelper(this)
        myDbHelper.updateContact(this, userData)
        onResume()
    }

    private fun deleteContact(int: Int){
        myDbHelper = MyDbHelper(this)
        myDbHelper.deleteContact(this , int)
        onResume()
    }

    private fun saveContact(
        stringName: String,
        stringNumber: String,
    ) {
        myDbHelper.addContact(UserData(stringName, stringNumber))
        Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show()
        onResume()
    }

    override fun onResume() {
        super.onResume()
        val arrayListContact = ArrayList<UserData>()
        arrayListContact.addAll(myDbHelper.getContact())

        val myAdapter =
            MyAdapter( this, arrayListContact, object : MyOnClickListener {
                override fun onClick(
                    stringName: String,
                    stringNumber: String,
                    stringCreate: String,
                    stringPhoto: String,
                    id: Int,
                ) {
                    buildAlertDialog(stringName, stringNumber, stringCreate, stringPhoto, id)
                    dialog.show()
                }
            } , object : MyOnClickListenerFromDelete{
                override fun onClickDelete(id: Int) {
                    deleteContact(id)
                }
            })
        recycler.adapter = myAdapter
    }
}