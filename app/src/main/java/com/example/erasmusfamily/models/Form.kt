package com.example.erasmusfamily.models


class Form (val name: String, val uni_ospitante: String, val nazione: String, val facoltà: String, val permanenza: Int, val uni_partenza: String, val note: String, val user: User){
    constructor(): this(" "," "," ", "", -1, "", "", User())
}