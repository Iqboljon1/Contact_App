package com.ir.contactapp.Adapter

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.ir.contactapp.R
import com.ir.contactapp.UserData
import kotlinx.android.synthetic.main.item_contact.view.*
import java.net.URI

class MyAdapter(
    private val context: Context,
    private val arrayListContacts: ArrayList<UserData>,
) :
    RecyclerView.Adapter<MyAdapter.VH>() {

    lateinit var dialog: AlertDialog

    inner class VH(var itemRv: View) : RecyclerView.ViewHolder(itemRv) {
        fun onBind(userData: UserData) {
            itemRv.tv_name.text = userData.name
            itemRv.tv_number.text = userData.number
            if (userData.image != 0) {
                itemRv.image.setImageResource(userData.image!!)
            }
            itemRv.btn_card_call.setOnClickListener {
                makeCall(userData.number.toString())
            }
            itemRv.btn_card_sms.setOnClickListener {
                sendSMS(userData.name.toString(), userData.number.toString())
            }
        }
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
            buildDialogSendMessage(stringName, stringNumber)
            dialog.show()
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