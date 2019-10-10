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


fun  User.searchUsers( uid: String): ArrayList<User> {
    val users = ArrayList<User>()
    val ref = FirebaseDatabase.getInstance().getReference("/users")
    ref.addListenerForSingleValueEvent(object: ValueEventListener {

        override fun onDataChange(p0: DataSnapshot) {
            p0.children.forEach{
                Log.d("NewMessage", it.toString())
                val user = it.getValue(User::class.java)
                if(user != null){
                    users.add(user)
                }
            }
        }
        override fun onCancelled(p0: DatabaseError) {

        }
    })

    return users
}
