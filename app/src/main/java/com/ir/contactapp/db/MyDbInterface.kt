package com.ir.contactapp.db

import android.content.Context
import com.ir.contactapp.UserData

interface MyDbInterface {
    fun addContact(userData: UserData)
    fun updateContact(context: Context, userData: UserData)
    fun deleteContact(context: Context, id: Int)
    fun searchContact(context: Context , string: String): ArrayList<UserData>
    fun getContact(): ArrayList<UserData>
}