package com.ir.contactapp.Adapter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.telephony.SmsManager
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.ir.contactapp.Interface.MyOnClickListener
import com.ir.contactapp.Interface.MyOnClickListenerFromDelete
import com.ir.contactapp.R
import com.ir.contactapp.UserData
import kotlinx.android.synthetic.main.item_contact.view.*

class MyAdapter(
    private val context: Context,
    private val arrayListContacts: ArrayList<UserData>,
    var myOnClickListener: MyOnClickListener,
    var myOnClickListenerFromDelete: MyOnClickListenerFromDelete
) :
    RecyclerView.Adapter<MyAdapter.VH>() {

    private lateinit var dialog: AlertDialog
    lateinit var menuBuilder: MenuBuilder
    var booleanAntiBag = true


    inner class VH(var itemRv: View) : RecyclerView.ViewHolder(itemRv) {
        fun onBind(userData: UserData) {
            itemRv.tv_name.text = userData.name
            itemRv.tv_number.text = userData.number
            itemRv.btn_card_call.setOnClickListener {
                makeCall(userData.number.toString())
            }
            itemRv.btn_card_sms.setOnClickListener {
                sendSMS(userData.name.toString(), userData.number.toString())
            }
            itemRv.image_more.setOnClickListener {
                if (booleanAntiBag){
                    popupMenuBuild(itemRv.image_more,
                        userData.name.toString(),
                        userData.number.toString() , userData.id!!)
                    booleanAntiBag = false
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun popupMenuBuild(
        view: View,
        stringName: String,
        stringNumber: String,
        id: Int
    ) {
        menuBuilder = MenuBuilder(context)
        val menuInflater = MenuInflater(context)
        menuInflater.inflate(R.menu.popup_menu, menuBuilder)
        val menuPopupHelper = MenuPopupHelper(context, menuBuilder, view)
        menuPopupHelper.setForceShowIcon(true)
        menuBuilder.setCallback(object : MenuBuilder.Callback {
            override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.menu_edit -> {
                        myOnClickListener.onClick(stringName,
                            stringNumber,
                            "Update Contact",
                            "Update Photo" , id)
                    }

                    R.id.menu_delete -> {
                        myOnClickListenerFromDelete.onClickDelete(id)
                    }
                }
                return true
            }

            override fun onMenuModeChange(menu: MenuBuilder) {

            }
        })

        menuPopupHelper.setOnDismissListener {
            booleanAntiBag = true
        }

        menuPopupHelper.show()
    }


    private fun makeCall(stringNumber: String) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(context as Activity,
                arrayOf(Manifest.permission.CALL_PHONE),
                101)
        } else {
            if (stringNumber.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:${stringNumber}")
                context.startActivity(intent)
            }
        }
    }

    private fun sendSMS(stringName: String, stringNumber: String) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(context as Activity,
                arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS),
                111)
        } else {
            if (booleanAntiBag) {
                buildDialogSendMessage(stringName, stringNumber)
                dialog.show()
                booleanAntiBag = false
            }
        }
    }

    private fun buildDialogSendMessage(stringName: String, stringNumber: String) {
        val alertDialog = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_send_message, null, false)
        val contactName = view.findViewById<TextView>(R.id.tv_name)
        val contactNumber = view.findViewById<TextView>(R.id.tv_number)
        val edtTextMessage = view.findViewById<AppCompatEditText>(R.id.editText_message)
        val btnCardSend = view.findViewById<CardView>(R.id.btn_card_send)

        btnCardSend.setOnClickListener {
            sendMessage(stringNumber, edtTextMessage.text.toString().trim())
        }

        alertDialog.setOnCancelListener {
            booleanAntiBag = true
        }

        contactName.text = stringName
        contactNumber.text = stringNumber
        alertDialog.setView(view)
        dialog = alertDialog.create()
        dialog.window!!.attributes.windowAnimations = R.style.MyAnimation
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun sendMessage(number: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(number, "ME", message, null, null)
            Toast.makeText(context, "Message is sent", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to send message", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(arrayListContacts[position])
    }

    override fun getItemCount(): Int = arrayListContacts.size
}