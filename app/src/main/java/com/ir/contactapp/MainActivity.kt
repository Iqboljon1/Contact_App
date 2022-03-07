package com.ir.contactapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import com.ir.contactapp.Adapter.MyAdapter
import com.ir.contactapp.Interface.MyOnClickListener
import com.ir.contactapp.Interface.MyOnClickListenerFromDelete
import com.ir.contactapp.db.MyDbHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var dialog: AlertDialog
    private lateinit var myDbHelper: MyDbHelper

    private var booleanAntiBag = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myDbHelper = MyDbHelper(this)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 123)
        }

        fab.supportImageTintList = ContextCompat.getColorStateList(this, R.color.white)
        fab.setOnClickListener {
            if (booleanAntiBag) {
                buildAlertDialog("", "", "Create Contact", "Add Photo", 0)
                dialog.show()
                booleanAntiBag = false
            }
        }
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                var string = s
                searchContact(string.toString())
            }
        })
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

        tvAddPhoto.setOnClickListener {
            ImagePicker.with(this).galleryOnly().galleryMimeTypes(arrayOf("image/*")).crop()
                .maxResultSize(400, 400).start()

        }

        alertDialog.setView(view)
        dialog = alertDialog.create()
        dialog.window!!.attributes.windowAnimations = R.style.MyAnimation
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun searchContact(string: String) {
        val arrayList = myDbHelper.searchContact(this, string)
        val myAdapter = MyAdapter(this, arrayList, object : MyOnClickListener {
            override fun onClick(
                stringName: String,
                stringNumber: String,
                stringCreate: String,
                stringPhoto: String,
                id: Int,
            ) {
                if (booleanAntiBag) {
                    buildAlertDialog(stringName, stringNumber, stringCreate, stringPhoto, id)
                    dialog.show()
                    booleanAntiBag = false
                }
            }
        }, object : MyOnClickListenerFromDelete {
            override fun onClickDelete(id: Int) {
                deleteContact(id)
            }
        })
        recycler.adapter = myAdapter
    }

    private fun updateContact(userData: UserData) {
        myDbHelper = MyDbHelper(this)
        myDbHelper.updateContact(this, userData)
        onResume()
    }

    private fun deleteContact(int: Int) {
        myDbHelper = MyDbHelper(this)
        myDbHelper.deleteContact(this, int)
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
        val arrayListContact = myDbHelper.getContact()

        val myAdapter =
            MyAdapter(this, arrayListContact, object : MyOnClickListener {
                override fun onClick(
                    stringName: String,
                    stringNumber: String,
                    stringCreate: String,
                    stringPhoto: String,
                    id: Int,
                ) {
                    if (booleanAntiBag) {
                        buildAlertDialog(stringName, stringNumber, stringCreate, stringPhoto, id)
                        dialog.show()
                        booleanAntiBag = false
                    }
                }
            }, object : MyOnClickListenerFromDelete {
                override fun onClickDelete(id: Int) {
                    deleteContact(id)
                }
            })
        recycler.adapter = myAdapter

    }

    @SuppressLint("Recycle", "Range")
    private fun loadContactFromDevice() {
        val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null)
        if (cursor!!.moveToFirst()) {
            do {
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val userData = UserData(name, number)
                myDbHelper.addContact(userData)
            } while (cursor.moveToNext())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val view = layoutInflater.inflate(R.layout.dialog_add_contact, null, false)
        val imageView = view.findViewById<ImageView>(R.id.imageContact)
        if (resultCode == Activity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE) {
            imageView.setImageURI(data!!.data)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                if (!checkFirstTime()) {
                    firstTime()
                }
            }
        }
    }

    private fun checkFirstTime(): Boolean {
        val sharedPreferences = this.getSharedPreferences("firstTime", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("firstTime", false)
    }

    @SuppressLint("CommitPrefEdits")
    private fun firstTime() {
        val sharedPreferences = this.getSharedPreferences("firstTime", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("firstTime", true)
        editor.apply()
        loadContactFromDevice()
    }

}