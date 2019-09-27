package com.example.erasmusfamily.registerlogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.erasmusfamily.R
import com.example.erasmusfamily.messages.MessagesActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

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
            val intent= Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }
    }


    private fun performLogin() {
        val email= email_login.text.toString()
        val password= password_login.text.toString()

        if( email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Inserisci le credenziali d'accesso", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d("Login", "Successfully logged in: ${it.result?.user?.uid}")

                val intent = Intent(this, MessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
