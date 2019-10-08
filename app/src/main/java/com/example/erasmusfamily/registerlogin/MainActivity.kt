package com.example.erasmusfamily.registerlogin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.erasmusfamily.R
import com.example.erasmusfamily.messages.MessagesActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_request_compile.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        button_login.setOnClickListener{
            performLogin()
        }

        no_account_login.setOnClickListener{
            Log.d("MainActivity", "Try to show signin activity")

            //start signin activity
            val intent= Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


    @SuppressLint("ResourceType")
    private fun performLogin() {
        val email= email_login.text.toString()
        val password= password_login.text.toString()

        val progressbar = findViewById<ProgressBar>(R.id.progressBar_login)


        if( email.isEmpty() || password.isEmpty()){
            email_login.setError("Il campo non rispetta i paramentri di dimensione. Non può essere vuoto")
            email_login.requestFocus()
            password_login.setError("Il campo non rispetta i paramentri di dimensione. Non può essere vuoto")
            password_login.requestFocus()
            return
        }


        progressbar.visibility = View.VISIBLE

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                progressbar.visibility = View.GONE

                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d("Login", "Successfully logged in: ${it.result?.user?.uid}")

                val intent = Intent(this, MessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {

                progressbar.visibility = View.GONE
                Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
