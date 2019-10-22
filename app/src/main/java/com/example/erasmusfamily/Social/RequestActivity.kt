package com.example.erasmusfamily.Social

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.erasmusfamily.R
import com.example.erasmusfamily.models.Request
import com.example.erasmusfamily.models.User
import com.example.erasmusfamily.registerlogin.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_request_compile.*

class RequestActivity: AppCompatActivity(){

    companion object {
        var currentRequest: Request?  = null
        var currentUser: User? = null
    }

    private val KEY_USER_OBJECT = "USER"
    private val key_user = "USER"
    lateinit var mPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_compile)

        supportActionBar?.title ="Fai una domanda!"

        buttonrequest_request_copile.setOnClickListener {
            addRequest()
        }

        verifyUserIsLoggedIn()
        fetchUser()
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

                    buttonrequest_request_copile.text = "MODIFICA"


                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun addRequest(){

        val titleRequest = rquesttitle_request_compile.text.toString()
        val textRequest = textrequest_rquest_compile.text.toString()


        if(titleRequest.isEmpty() || titleRequest.length < 5 || titleRequest.length > 35){
            rquesttitle_request_compile.error = "Il campo non rispetta i paramentri di dimensione. Almeno 4 caratteri e massimo 35"
            rquesttitle_request_compile.requestFocus()
            return
        }

        if(textRequest.isEmpty() || textRequest.length < 10 || textRequest.length > 500){
            textrequest_rquest_compile.error = "Il campo non rispetta i paramentri di dimensione. Almeno 10 caratteri e massimo 500"
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

    /*//Azione eseguida al chiudersi dell'activity
    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(this, RequestLogActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }*/

    //Fetch User
    private fun fetchUser(){
        mPref= getSharedPreferences(KEY_USER_OBJECT,MODE_PRIVATE)

        val gson = Gson()
        val json = mPref.getString(key_user, "")
        currentUser = gson.fromJson(json, User::class.java)
    }

    //Verifica se l'utente è loggato
    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            Toast.makeText(this, "Ops. Si è verificato un problema, riprova l'accesso.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}