package com.ir.contactapp

class UserData {
    var name: String? = null
    var image: Int? = null
    var number: String? = null
    lateinit var arrayHistory : ArrayList<HistoryData>

    constructor(name: String?, image: Int?, number: String?) {
        this.name = name
        this.image = image
        this.number = number
    }
}