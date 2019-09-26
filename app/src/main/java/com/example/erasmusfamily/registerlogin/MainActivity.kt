package com.example.erasmusfamily.registerlogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.erasmusfamily.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        button_login.setOnClickListener{
            val email= email_login.text.toString()
            val password= password_login.text.toString()

            if( email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Inserisci le credenziali d'accesso", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("Login", "Attempt login with email/pw: $email/***")

            //Firebase authentication
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
            //    .addOnCompleteListener()

        }

        no_account_login.setOnClickListener{
            Log.d("MainActivity", "Try to show signin activity")

            //start signin activity
            val intent= Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }
    }
}
