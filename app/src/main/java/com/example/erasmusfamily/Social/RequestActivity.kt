package com.example.erasmusfamily.Social

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.erasmusfamily.R
import com.example.erasmusfamily.messages.ChatLogActivity
import com.example.erasmusfamily.messages.MessagesActivity
import com.example.erasmusfamily.models.Request
import com.example.erasmusfamily.models.User
import com.example.erasmusfamily.registerlogin.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_request_compile.*

class RequestActivity: AppCompatActivity(){

    companion object {
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_compile)

        supportActionBar?.title ="Fai una domanda!"

        fetchCurrentUser()

        buttonrequest_request_copile.setOnClickListener {
            addRequest()
        }


    }

    private fun addRequest(){

        val titleRequest = rquesttitle_request_compile.text.toString()
        val textRequest = textrequest_rquest_compile.text.toString()


        if(titleRequest.isEmpty() || titleRequest.length < 3){
            rquesttitle_request_compile.setError("Il campo non rispetta i paramentri di dimensione. Almeno 4 caratteri")
            rquesttitle_request_compile.requestFocus()
            return
        }

        if(textRequest.isEmpty() || textRequest.length < 10){
            textrequest_rquest_compile.setError("Il campo non rispetta i paramentri di dimensione. Almeno 10 caratteri")
            textrequest_rquest_compile.requestFocus()
            return
        }

        //firebase
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("request/$uid")

        val request = currentUser?.let {
            Request(currentUser?.name+" "+ currentUser?.surname,titleRequest, textRequest, it)
        }
        ref.setValue(request).addOnSuccessListener {
           val intent = Intent(this, RequestLogActivity::class.java )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }



    }


    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("LatestMessages", "Current user ${MessagesActivity.currentUser?.profileImageUrl}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


}