package com.example.erasmusfamily.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val name: String, val surname: String, val email: String, val password: String, val profileImageUrl: String, val andrà: Boolean, val andato: Boolean ): Parcelable {
    constructor() : this("", "", "", "", "", "",false, false)
}