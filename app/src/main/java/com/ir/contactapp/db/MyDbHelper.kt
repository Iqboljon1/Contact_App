package com.ir.contactapp.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.ir.contactapp.UserData
import com.ir.contactapp.db.Constants.DB_NAME
import com.ir.contactapp.db.Constants.DB_VERSION
import com.ir.contactapp.db.Constants.ID
import com.ir.contactapp.db.Constants.NAME
import com.ir.contactapp.db.Constants.NUMBER
import com.ir.contactapp.db.Constants.TABLE_NAME

class MyDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION),
    MyDbInterface {
    override fun onCreate(db: SQLiteDatabase?) {
        val query =
            "create table $TABLE_NAME ($ID integer not null primary key autoincrement unique , $NAME text not null , $NUMBER text not null)"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val query = "drop table if exists $TABLE_NAME"
        db?.execSQL(query)
        onCreate(db)
    }

    override fun addContact(userData: UserData) {
        val dataBase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NAME, userData.name)
        contentValues.put(NUMBER, userData.number)
        dataBase.insert(TABLE_NAME, null, contentValues)
        dataBase.close()
    }


    override fun updateContact(context: Context, userData: UserData) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NAME, userData.name)
        contentValues.put(NUMBER, userData.number)
        val result =
            database.update(TABLE_NAME, contentValues, "id=?", arrayOf(userData.id.toString()))
        if (result == -1) {
            Toast.makeText(context, "Failed to Update", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Successfully Updated!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun deleteContact(context: Context, id: Int) {
        val database = this.writableDatabase
        val result = database.delete(TABLE_NAME, "id=?", arrayOf(id.toString()))
        if (result == -1) {
            Toast.makeText(context, "Failed to Delete", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Successfully Delete!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun searchContact( context: Context , string: String): ArrayList<UserData> {
        val arrayList = ArrayList<UserData>()
        val query = "select * from $TABLE_NAME where $NAME like '$string%'"
        val database = this.readableDatabase
        val cursor = database.rawQuery(query, null)

        if (cursor.moveToFirst()){
            do {
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val number = cursor.getString(2)
                arrayList.add(UserData(id, name, number))
            }while (cursor.moveToNext())
        }

        return arrayList
    }

    override fun getContact(): ArrayList<UserData> {
        val arrayList = ArrayList<UserData>()
        val query = "select * from $TABLE_NAME"
        val dataBase = this.readableDatabase
        val cursor = dataBase.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val number = cursor.getString(2)
                arrayList.add(UserData(id, name, number))
            } while (cursor.moveToNext())
        }
        return arrayList
    }

}