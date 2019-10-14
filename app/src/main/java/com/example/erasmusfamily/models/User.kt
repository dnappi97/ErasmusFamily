package com.example.erasmusfamily.models

import android.os.Parcelable
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(var first: Boolean, val uid: String, val name: String, val surname: String, val email: String, val password: String, val profileImageUrl: String, val andrà: Boolean, val andato: Boolean, val arrayUserFriends: ArrayList<User> ): Parcelable {
    constructor() : this(true, "", "", "", "", "", "",false, false, ArrayList<User>())

}



