package com.example.erasmusfamily.registerlogin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.erasmusfamily.R
import com.example.erasmusfamily.Social.FormLogActivity
import com.example.erasmusfamily.messages.MessagesActivity
import com.example.erasmusfamily.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_request_compile.*

class MainActivity : AppCompatActivity() {



    private var email= ""
    private var password= ""
    private val mAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.title ="Erasmus Family"


        button_login.setOnClickListener{
            performLogin()
        }

        no_account_login.setOnClickListener{
            Log.d("MainActivity", "Try to show signin activity")

            //start signin activity
            val intent= Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        passworddimenticata_login.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword(){
        email = email_login.text.toString()
        password = password_login.text.toString()

        if(email.isEmpty() ){
            email_login.setError("Devi inserire l'email per recuperare la password")
            email_login.requestFocus()
            return
        } else {
            mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(OnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this, "Email di recupero inviata", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Ops, c'è stato un errore durante l'invio della email. Riprova", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }

    @SuppressLint("ResourceType")
    private fun performLogin() {

        email = email_login.text.toString()
        password = password_login.text.toString()

        val progressbar = findViewById<ProgressBar>(R.id.progressBar_login)


        if( email.isEmpty() || password.isEmpty()){
            email_login.setError("Il campo non rispetta i paramentri di dimensione. Non può essere vuoto")
            email_login.requestFocus()
            password_login.setError("Il campo non rispetta i paramentri di dimensione. Non può essere vuoto")
            password_login.requestFocus()
            return
        }

        progressbar.visibility = View.VISIBLE

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                progressbar.visibility = View.GONE

                if (!it.isSuccessful) return@addOnCompleteListener

                val user = mAuth.currentUser
                if(user!!.isEmailVerified){
                    Log.d("Login", "Successfully logged in: ${it.result?.user?.uid}")


                    val intent = Intent(this, MessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Email non verificata, verifica prima la tua email per effettuare l'accesso", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }


            }
            .addOnFailureListener {

                progressbar.visibility = View.GONE
                Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }





}
