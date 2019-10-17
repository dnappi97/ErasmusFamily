package com.example.erasmusfamily.registerlogin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import android.util.Log.d
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.erasmusfamily.R
import com.example.erasmusfamily.Social.FormActivity
import com.example.erasmusfamily.Social.FormLogActivity
import com.example.erasmusfamily.messages.MessagesActivity
import com.example.erasmusfamily.messages.NewMessageActivity
import com.example.erasmusfamily.models.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class RegisterActivity: AppCompatActivity(){

    private val KEY_USER_OBJECT = "USER"
    lateinit var mPref: SharedPreferences
    private var user: User ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.title ="Registrazione"

        mPref = getSharedPreferences(KEY_USER_OBJECT, Context.MODE_PRIVATE)

        registrati_signin.setOnClickListener{
            performRegister()
        }

        account_signin.setOnClickListener{
            d("MainActivity", "Try to show signin activity")

            //start signin activity
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        selectphoto_button_register.setOnClickListener{
            d("RegisterActivity", "Try to show photo select")

            val intent=Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== 0 && resultCode == Activity.RESULT_OK && data != null){
            //foto selezionata
            d("RegisterActivity","Photo was selected")

            selectedPhotoUri = data.data

            val bitmap= MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            circle_imageview_signin.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f

        }
    }

    private fun performRegister(){
        val email= email_signin.text.toString()
        val password= password_signin.text.toString()

        val progressbar = findViewById<ProgressBar>(R.id.progressBar_register)

        if( name_signin.text.toString().isEmpty() || name_signin.text.length < 2){
            name_signin.setError("Il campo non rispetta i paramentri di dimensione. Almeno 3 caratteri")
            name_signin.requestFocus()
            return
        }

        if( surname_signin.text.toString().isEmpty() || surname_signin.text.length < 2){

            surname_signin.setError("Il campo non rispetta i paramentri di dimensione. Almeno 3 caratteri")
            surname_signin.requestFocus()
            return
        }

        if(password.isEmpty() || !isValidPassword(password)){
            password_signin.setError("La password deve contenere minimo 8 caratteri e massimo 20, tra cui almeno un numero ed una lettera maiuscola.")
            password_signin.requestFocus()
            return
        }

        if( email.isEmpty() || !isValidEmail(email)){
            email_signin.setError("La mail deve essere verificata, inserirne una veritiera")
            email_signin.requestFocus()
            return
        }

        if( radioGroup_signin.checkedRadioButtonId == -1){
            radioGroup_signin.requestFocus()
            Toast.makeText(this, "Nessuna selezione effettuata, si prega di selezionare un'opzione", Toast.LENGTH_SHORT).show()
            return
        }

        if(selectedPhotoUri == null) {
            Toast.makeText(this, "Inserisci un'immagine profilo.", Toast.LENGTH_LONG).show()
            return
        }

        d("MainActivity", "Email is: " + email)
        d("MainActivity", "Password: $password")

        progressbar.visibility = View.VISIBLE

        //Firebase authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                progressbar.visibility = View.GONE

                if(!it.isSuccessful) return@addOnCompleteListener

                if(it.isSuccessful) {
//                    Toast.makeText(this, "Clicca su 'Verifica Email' per completare la registrazione", Toast.LENGTH_LONG).show()
//                    scrollview_registration.fullScroll(View.FOCUS_DOWN)
//                    verificationemail_signin.setError("Clicca qui!")
//                    verificationemail_signin.requestFocus()
                    sendEmailVerification()
                }

                //else if successful
                d("Main", "Successfully created user with uid: ${it.result?.user!!.uid} ")

                uploadImageToFirbaseStorage()
            }
            .addOnFailureListener{
                Log.d("Main", "Failed to create user: ${it.message}")
                progressbar.visibility = View.GONE
                Toast.makeText(this, "\"Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }




    }

    private fun sendEmailVerification(){
        val user = FirebaseAuth.getInstance().currentUser
        if( user != null ){
            val email= email_signin.text.toString()
            if(!email.isEmpty()){

                user.sendEmailVerification().addOnSuccessListener {
                    Toast.makeText(this, "Verifica la tua email per completare la registrazione", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }

            } else {
                Toast.makeText(this, "Campo 'email' vuoto, impossibile verificare la email", Toast.LENGTH_LONG).show()
                email_signin.setError("Inserire una email per verificarla")
                email_signin.requestFocus()
            }

        } else {
            Toast.makeText(this, "Devi prima procedere con la registrazione", Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadImageToFirbaseStorage() {

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                d("RegisterActivity", "Successfully upload image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    d("RegisterActivity", "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener{

            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        var andato = false



       if(andato_signin.isChecked)
            andato = true


        user = User(
            true,
            uid,
            name_signin.text.toString(),
            surname_signin.text.toString(),
            email_signin.text.toString(),
            password_signin.text.toString(),
            profileImageUrl,
            andato
        )

        ref.setValue(user)
            .addOnSuccessListener {
                d("RegisterActivity", "Saved the user to Firebase Database")

                val gson = Gson()
                val userObject = gson.toJson(user)
                mPref.edit().putString("Utente",userObject)
                mPref.edit().apply()

            }
            .addOnFailureListener{
                Toast.makeText(this, "Qualcosa è andato storto durante la registrazione, riprova", Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }

    //VALIDATION PASSWORD
    protected fun isValidPassword(password: String): Boolean {

        val pattern: Pattern
        val matcher: Matcher
        val Password_Pattern = "(?=^.{8,20}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"
        pattern = Pattern.compile(Password_Pattern)
        matcher = pattern.matcher(password)

        return matcher.matches()

    }

    protected fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

