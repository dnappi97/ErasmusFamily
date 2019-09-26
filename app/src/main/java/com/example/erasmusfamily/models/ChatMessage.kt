package com.example.erasmusfamily.models

class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String, val TimeStamp: Long){
    constructor() : this("", "", "", "", -1)
}
