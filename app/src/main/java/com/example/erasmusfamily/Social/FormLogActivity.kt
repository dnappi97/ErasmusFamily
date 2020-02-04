package com.example.erasmusfamily.Social

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat

import androidx.recyclerview.widget.DividerItemDecoration
import com.example.erasmusfamily.R
import com.example.erasmusfamily.Setting.SettingActivity
import com.example.erasmusfamily.messages.ChatLogActivity
import com.example.erasmusfamily.messages.MessagesActivity
import com.example.erasmusfamily.models.Form
import com.example.erasmusfamily.models.User
import com.example.erasmusfamily.registerlogin.MainActivity
import com.example.erasmusfamily.view.FormItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_form_log.*

class FormLogActivity : AppCompatActivity() {


    companion object{
        val USER_KEY = "USER_KEY"
        var currentUser: User? = null
    }

    private val KEY_USER_OBJECT = "USER"
    private val key_user = "USER"
    lateinit var mPref: SharedPreferences

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_log)

        supportActionBar?.title ="Big Brothers"

        recycler_form_log.adapter = adapter
        recycler_form_log.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        verifyUserIsLoggedIn()
        fetchUser()
        indirizzaUserFirst()
        serchForm()
    }

    private fun indirizzaUserFirst(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        if(currentUser == null ){
            Toast.makeText(this, "Ops, si è verificato un problema. Non risulti essere un utente abilitato. Riprova l'accesso.", Toast.LENGTH_LONG).show()
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this, MainActivity::class.java )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            if(currentUser!!.first){
                val name = currentUser!!.name+" "+ currentUser!!.surname

                if(currentUser!!.andato){

                    Toast.makeText(this, "Benvenuto "+name+", compila questo form in modo da aiutare altri ragazzi in questa esperienza!", Toast.LENGTH_LONG).show()

                    val intent = Intent(this, FormActivity::class.java )
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {

                    currentUser!!.first= false
                    ref.setValue(currentUser)
                    updateUser()

                    Toast.makeText(this, "Benvenuto "+name+", dai un'occhiata e contatta qualcuno se può esserti utile!\nOppure visita la sezione 'Little Brothers' e fai una domanda!", Toast.LENGTH_LONG).show()

                    val intent = Intent(this, FormLogActivity::class.java )
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }



    }



    private fun serchForm(){
        val ref = FirebaseDatabase.getInstance().getReference("form")
        ref.addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val form = p0.getValue(Form::class.java) ?: return

                adapter.add(FormItem(form.name, form.uni_ospitante, form.nazione, form.facoltà, form.permanenza, form.uni_partenza, form.note, form.user))

            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val form = p0.getValue(Form::class.java) ?: return
                adapter.add(FormItem(form.name, form.uni_ospitante,form.nazione,form.facoltà,form.permanenza, form.uni_partenza, form.note, form.user))

            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        adapter.setOnItemClickListener{ item, view ->

            val formItem = item as FormItem

            if(FirebaseAuth.getInstance().uid == item.user.uid) {

                val dialogBulder = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.activity_request_chat, null)
                val textViewChat = dialogView.findViewById<TextView>(R.id.chatrequest_textview_request_chat)
                val buttonChat = dialogView.findViewById<Button>(R.id.buttonchat_request_chat)
                dialogBulder.setView(dialogView)
                dialogBulder.setCancelable(true)
                textViewChat.text = "Vuoi effettuare una modifica?"
                buttonChat.text = "MODIFICA"
                val dialog = dialogBulder.create()
                buttonChat.setOnClickListener{
                    val intent = Intent(view.context, FormActivity::class.java)

                    intent.putExtra(USER_KEY, formItem.user)
                    startActivity(intent)

                    dialog.dismiss()
                }



                dialog.show()
            }
            else {

                val dialogBulder = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.activity_request_chat, null)
                val textViewChat = dialogView.findViewById<TextView>(R.id.chatrequest_textview_request_chat)
                val buttonChat = dialogView.findViewById<Button>(R.id.buttonchat_request_chat)
                dialogBulder.setView(dialogView)
                dialogBulder.setCancelable(true)
                textViewChat.text = "Vuoi contattare "+item.name+" ?"
                val dialog = dialogBulder.create()
                buttonChat.setOnClickListener{
                    val intent = Intent(view.context, ChatLogActivity::class.java)

                    intent.putExtra(USER_KEY, formItem.user)
                    startActivity(intent)
                    dialog.dismiss()
                }

                dialog.show()

            }
        }
    }


    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
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
            } R.id.navigation_Setting -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
            }
            R.id.navigation_mete -> {
                val intent = Intent(this, MeteViewActivity::class.java)
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
        menuInflater.inflate(R.menu.navigationmenu_bigbrothers, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Fetch User
    private fun fetchUser(){
        mPref = getSharedPreferences(KEY_USER_OBJECT,MODE_PRIVATE)

        val gson = Gson()
        val json = mPref.getString(key_user, "")
        currentUser = gson.fromJson(json, User::class.java)
    }

    //Aggiorna dati user
    private fun updateUser(){
        mPref= getSharedPreferences(KEY_USER_OBJECT ,MODE_PRIVATE)

        if(currentUser != null){

            val gson = Gson()
            val json = gson.toJson(currentUser, User::class.java)
            mPref.edit().putString(key_user, json).apply()
            println(currentUser.toString())
        }


    }
}