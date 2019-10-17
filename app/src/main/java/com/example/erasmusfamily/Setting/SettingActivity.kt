package com.example.erasmusfamily.Setting

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.erasmusfamily.R
import com.example.erasmusfamily.Social.FormLogActivity
import com.example.erasmusfamily.Social.RequestLogActivity
import com.example.erasmusfamily.messages.ChatLogActivity
import com.example.erasmusfamily.messages.MessagesActivity
import com.example.erasmusfamily.models.User
import com.example.erasmusfamily.registerlogin.MainActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_changepassword.*
import kotlinx.android.synthetic.main.activity_setting_item.*

class SettingActivity: AppCompatActivity(){

    companion object {
        lateinit var mPref: SharedPreferences
        private val KEY_USER_OBJECT = "USER"
        private val key_user = "USER"
        var currentUser: User? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_item)

        fetchUser()

        supportActionBar?.title = "User"
        supportActionBar?.setIcon(R.drawable.ic_settings)


        setSettingPage()

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

        val gson: Gson= Gson()
        val json = mPref.getString(key_user, "")
        currentUser= gson.fromJson(json, User::class.java)


    }

}

