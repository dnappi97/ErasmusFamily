package com.example.erasmusfamily.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(var first: Boolean, val uid: String, val name: String, val surname: String, val email: String, val posImage: String, var profileImageUrl: String, val andato: Boolean): Parcelable {
    constructor() : this(true, "", "", "", "", "", "", false)

    override fun toString(): String {
        return "User(first=$first, uid='$uid', name='$name', surname='$surname', email='$email', posImage='$posImage', profileImageUrl='$profileImageUrl', andato=$andato)"
    }


}



