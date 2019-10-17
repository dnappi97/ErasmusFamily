package com.example.erasmusfamily.Social

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.erasmusfamily.R
import com.example.erasmusfamily.messages.ChatLogActivity
import com.example.erasmusfamily.messages.MessagesActivity
import com.example.erasmusfamily.models.Form
import com.example.erasmusfamily.models.Request
import com.example.erasmusfamily.models.User
import com.example.erasmusfamily.registerlogin.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_form_compile.*
import kotlinx.android.synthetic.main.activity_request_compile.*

class RequestActivity: AppCompatActivity(){

    companion object {
        var currentRequest: Request?  = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_compile)

        supportActionBar?.title ="Fai una domanda!"

        buttonrequest_request_copile.setOnClickListener {
            addRequest()
        }


        fetchRequest()
    }

    private fun fetchRequest(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("request/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentRequest = p0.getValue(Request::class.java)

                if( currentRequest != null ){

                    rquesttitle_request_compile.setText(currentRequest?.title.toString())
                    textrequest_rquest_compile.setText(currentRequest?.text.toString())


                    buttonrequest_request_copile.setText("MODIFICA")

                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
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

        val request = MainActivity.currentUser?.let {
            Request(MainActivity.currentUser?.name+" "+ MainActivity.currentUser?.surname,titleRequest, textRequest, it)
        }
        ref.setValue(request).addOnSuccessListener {
           val intent = Intent(this, RequestLogActivity::class.java )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }



    }

    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(this, RequestLogActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }
}