package com.example.erasmusfamily.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(var first: Boolean, val uid: String, val name: String, val surname: String, val email: String, val password: String, val profileImageUrl: String, val andato: Boolean): Parcelable {
    constructor() : this(true, "", "", "", "", "", "", false)

    override fun toString(): String {
        return "User(first=$first, uid='$uid', name='$name', surname='$surname', email='$email', password='$password', profileImageUrl='$profileImageUrl', andato=$andato)"
    }


}



