package com.example.erasmusfamily.registerlogin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_request_compile.*
import java.lang.reflect.Type

class MainActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
    }

    private val KEY_USER_OBJECT = "USER"
    private val KEY_SAVED_OBJECT = "USER_CREDENTIAL"

    lateinit var mPref: SharedPreferences

    lateinit var email: String
    lateinit var password: String
    private val mAuth = FirebaseAuth.getInstance()

    private val key_email = "EMAIL_USER"
    private val key_pass = "PASSWORD_USER"
    private val key_user = "USER"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.title ="Erasmus Family"

        mPref = getSharedPreferences(KEY_SAVED_OBJECT, Context.MODE_PRIVATE)
        email = mPref.getString(key_email,"")
        password = mPref.getString(key_pass,"")

        if(!email.isEmpty() && !password.isEmpty()){
            email_login.setText(email)
            password_login.setText(password)
            ricordacredenziali_login!!.isChecked = true
        }

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

        email = email_login.text.toString().trim()
        password = password_login.text.toString().trim()

        val progressbar = findViewById<ProgressBar>(R.id.progressBar_login)


        if( email.isEmpty() ){

            email_login.error = "Il campo non rispetta i paramentri di dimensione. Non può essere vuoto"
            email_login.requestFocus()

            return
        } else if (password.isEmpty() ){

            password_login.error = "Il campo non rispetta i paramentri di dimensione. Non può essere vuoto"
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

                    rememberMe()

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



    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)

                rememberUser()

                Log.d("LatestMessages", "Current user ${currentUser?.profileImageUrl}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

    }

    private fun startActivity(){
        val intent = Intent(this, FormLogActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }


    //Ricorda credenziali

   private fun rememberMe(){

       mPref = getSharedPreferences(KEY_SAVED_OBJECT ,MODE_PRIVATE)

       if(ricordacredenziali_login.isChecked){
           mPref.edit()
               .putString(key_email, email)
               .putString(key_pass, password)
               .apply()

       } else {

           mPref.edit().remove(key_pass).apply()
           mPref.edit().remove(key_email).apply()
       }

       fetchCurrentUser()
   }

    //Ricorda user
    private fun rememberUser(){
        mPref = getSharedPreferences(KEY_USER_OBJECT ,MODE_PRIVATE)

        if(currentUser != null){

            val gson = Gson()
            val json = gson.toJson(currentUser, User::class.java)
            mPref.edit().putString(key_user, json).apply()
            startActivity()
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }

}
