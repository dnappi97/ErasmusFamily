package com.example.erasmusfamily.models

class Request (val name: String, val title: String, val text: String, val user: User){
    constructor(): this("","", "", User())
}