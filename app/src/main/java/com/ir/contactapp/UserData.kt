package com.ir.contactapp

class UserData{
    var id: Int? = null
    var name: String? = null
    var number: String? = null

    constructor()

    constructor(id: Int?, name: String?, number: String?) {
        this.id = id
        this.name = name
        this.number = number
    }

    constructor(name: String?, number: String?) {
        this.name = name
        this.number = number
    }
}


