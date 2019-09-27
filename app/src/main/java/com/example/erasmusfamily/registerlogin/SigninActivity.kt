package com.example.erasmusfamily.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signin.*
import android.util.Log.d
import android.widget.Toast
import com.example.erasmusfamily.R
import com.example.erasmusfamily.messages.MessagesActivity
import com.example.erasmusfamily.models.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class SigninActivity: AppCompatActivity(){

    private val mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        registrati_signin.setOnClickListener{
            performRegister()
        }

        account_signin.setOnClickListener{
            d("MainActivity", "Try to show signin activity")

            //start signin activity
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        selectphoto_button_register.setOnClickListener{
            d("SigninActivity", "Try to show photo select")

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
            d("SigninActivity","Photo was selected")

            selectedPhotoUri = data.data

            val bitmap= MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            circle_imageview_signin.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f

           // val bitmapDrawable = BitmapDrawable(bitmap)
           // selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun performRegister(){
        val email= email_signin.text.toString()
        val password= password_signin.text.toString()

        if( name_signin.text.toString().isEmpty()){

            Toast.makeText(this, "Inserire il nome", Toast.LENGTH_SHORT).show()
            return
        }

        if( surname_signin.text.toString().isEmpty()){

            Toast.makeText(this, "Inserire il cognome", Toast.LENGTH_SHORT).show()
            return
        }

        if( email.isEmpty() || password.isEmpty()){

            Toast.makeText(this, "Email e password non possono essere vuoti", Toast.LENGTH_SHORT).show()
            return
        }

        if( radioGroup_signin.checkedRadioButtonId == -1){
            Toast.makeText(this, "Nessuna selezione effettuata, si prega di selezionare un'opzione", Toast.LENGTH_SHORT).show()
            return
        }

        d("MainActivity", "Email is: " + email)
        d("MainActivity", "Password: $password")

        //Firebase authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener

                //else if successful
                d("Main", "Successfully created user with uid: ${it.result?.user!!.uid} ")

                uploadImageToFirbaseStorage()
            }
            .addOnFailureListener{
                Log.d("Main", "Failed to create user: ${it.message}")
                Toast.makeText(this, "\"Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirbaseStorage(){

        if(selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                d("SigninActivity", "Successfully upload image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    d("SigninActivity", "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener{

            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        var andrà = false
        var andato = false

       if(non_andato_signin.isChecked)
            andrà = true

       if(andato_signin.isChecked)
            andato = true


        val user = User(
            uid,
            name_signin.text.toString(),
            surname_signin.text.toString(),
            email_signin.text.toString(),
            password_signin.text.toString(),
            profileImageUrl,
            andrà,
            andato
        )

        ref.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Account creato con successo", Toast.LENGTH_SHORT).show()
                d("SigninActivity", "Saved the user to Firebase Database")

                val intent = Intent(this, MessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                Toast.makeText(this, "Qualcosa è andato storto durante la registrazione, riprova", Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }
}

