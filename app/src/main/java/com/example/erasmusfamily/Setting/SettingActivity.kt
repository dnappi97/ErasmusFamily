package com.example.erasmusfamily.Setting

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.erasmusfamily.R
import com.example.erasmusfamily.Social.FormActivity
import com.example.erasmusfamily.Social.FormLogActivity
import com.example.erasmusfamily.Social.RequestLogActivity
import com.example.erasmusfamily.messages.ChatLogActivity
import com.example.erasmusfamily.messages.MessagesActivity
import com.example.erasmusfamily.models.User
import com.example.erasmusfamily.registerlogin.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_setting_item.*
import java.util.*

class SettingActivity: AppCompatActivity(){

    companion object {
        lateinit var mPref: SharedPreferences
        var currentUser: User? = null
    }

    private val KEY_USER_OBJECT = "USER"
    private val key_user = "USER"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_item)

        verifyUserIsLoggedIn()
        fetchUser()

        supportActionBar?.title = "User"
        supportActionBar?.setIcon(R.drawable.ic_settings)


        setSettingPage()

        imageview_setting.setOnClickListener{

            pressChangeImage()
        }

        changepassword_button_setting.setOnClickListener{
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

    }




    @SuppressLint("SetTextI18n")
    private fun setSettingPage(){

        val user = currentUser

        if(user == null ){


            Toast.makeText(this, "Ops, si è verificato un problema durante il caricamento della pagina", Toast.LENGTH_LONG).show()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        } else {

            nameuser_setting.text = user.name+" "+ user.surname
            emailuser_setting.text = user.email
            Picasso.get().load(user.profileImageUrl).into(imageview_setting)

            if(user.andato){

                tipebrother_setting.text = "Big Brothers"
                iconetipe_setting.setImageResource(R.drawable.ic_bigbrothers)

            } else {

                tipebrother_setting.text = "Little Brothers"
                iconetipe_setting.setImageResource(R.drawable.ic_littlebrothers)
            }
        }
    }





    //Menu
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.navigation_message -> {
                val intent = Intent(this, MessagesActivity::class.java)
                startActivity(intent)
            }
            R.id.navigation_request -> {
                val intent = Intent(this, RequestLogActivity::class.java)
                startActivity(intent)
            }
            R.id.navigation_form -> {
                val intent = Intent(this, FormLogActivity::class.java)
                startActivity(intent)
            }
            R.id.navigation_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigationmenu_setting, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Fetch User
    private fun fetchUser(){
        mPref = getSharedPreferences(KEY_USER_OBJECT ,MODE_PRIVATE)

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

    //Change profile image
    var selectedPhotoUri: Uri? = null


    private fun pressChangeImage(){
        val dialogBulder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.activity_request_chat, null)
        val textDialog = dialogView.findViewById<TextView>(R.id.chatrequest_textview_request_chat)
        val buttonChange = dialogView.findViewById<Button>(R.id.buttonchat_request_chat)
        dialogBulder.setView(dialogView)
        dialogBulder.setCancelable(true)
        textDialog.text = "Vuoi modificare la tua immagine di profilo?"
        buttonChange.text = "MODIFICA"
        val dialog = dialogBulder.create()
        buttonChange.setOnClickListener{
            val intent=Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent, 0)

            dialog.dismiss()
        }


        dialog.show()
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== 0 && resultCode == Activity.RESULT_OK && data != null){


            selectedPhotoUri = data.data
            removeImageToFirbaseStorage()
            uploadImageToFirbaseStorage()

            val bitmap= MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            imageview_setting.setImageBitmap(bitmap)


        }
    }

    private fun removeImageToFirbaseStorage(){
        val ref = FirebaseStorage.getInstance().getReference("/images/${currentUser?.posImage}")
        ref.delete()
            .addOnSuccessListener {
                println("Image eliminata")
            }
            .addOnFailureListener{
                println("Image non eliminata")
            }

    }

    private fun uploadImageToFirbaseStorage() {

        text_progresbar_setting.visibility = View.VISIBLE
        progressBar_setting.visibility = View.VISIBLE
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {

                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                    updateUser()
                }
            }
            .addOnFailureListener{

            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        currentUser?.profileImageUrl = profileImageUrl

        ref.setValue(currentUser)
            .addOnSuccessListener {
                updateUser()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Si è verificato un errore durante il caricamento dell'immagine, riprova", Toast.LENGTH_LONG).show()
            }
    }


    //Aggiorna dati user
    private fun updateUser(){
        mPref = getSharedPreferences(KEY_USER_OBJECT ,MODE_PRIVATE)

        if(currentUser != null){

            val gson = Gson()
            val json = gson.toJson(currentUser, User::class.java)
            mPref.edit().putString(key_user, json).apply()
            println(currentUser.toString())

        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        text_progresbar_setting.visibility = View.GONE
        progressBar_setting.visibility = View.GONE

    }
}

