package com.example.erasmusfamily.Social

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.erasmusfamily.R
import com.example.erasmusfamily.Setting.SettingActivity
import com.example.erasmusfamily.messages.MessagesActivity
import com.example.erasmusfamily.models.Form
import com.example.erasmusfamily.models.User
import com.example.erasmusfamily.registerlogin.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_form_compile.*
import kotlinx.android.synthetic.main.activity_form_item.*

class FormActivity: AppCompatActivity(){

    companion object {
        var currentForm: Form?  = null
        var currentUser: User? = null
    }

    private val KEY_USER_OBJECT = "USER"
    private val key_user = "USER"
    lateinit var mPref: SharedPreferences

    private var list_of_nationalities= arrayOf("Austria", "Belgio", "Bulgaria", "Cechia", "Cipro", "Croazia", "Danimarca", " Estonia",
        "Finlandia", "Francia", "Germania", "Grecia", "Irlanda", "Italia", "lettonia", "Lituania", "Lussemburgo",
        "Malta", "Paesi Bassi", "Polonia", "Portogallo", "Regno Unito", "Romania", "Slovacchia", "Slovenia",
        "Spagna", "Svezia", "Ungheria")

    private var uni_ospitante = ""
    private var nazione = ""
    private var facoltà = ""
    private var permanenza = -1
    private var uni_partenza = ""
    private var note = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_compile)

        supportActionBar?.title ="Diventa un Big Brother!"

        //Settings spinner
        var spin: Spinner = findViewById(R.id.spinnernazionalità_from_row) as Spinner
        var aa: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, list_of_nationalities)

        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spin.adapter = aa
        spin.prompt = "Select a nationality"
        spin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                nazione = spin.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        verifyUserIsLoggedIn()
        fetchUser()
        fetchCurrentForm()


        button_form_row.setOnClickListener{
            addForm()
        }
    }


    private fun fetchCurrentForm(){

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("form/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentForm = p0.getValue(Form::class.java)

                if( currentForm != null ){

                    universitàospitante_form_row.setText(currentForm?.uni_ospitante)
                    facoltà_form_row.setText(currentForm?.facoltà)
                    mesipermanenza_form_row.setText(currentForm?.permanenza.toString())
                    universitàpartenza_form_row.setText(currentForm?.uni_partenza)
                    note_form_row.setText(currentForm?.note)

                    val myAdapter : ArrayAdapter<String>
                    val spin = spinnernazionalità_from_row

                    myAdapter = spin.adapter as ArrayAdapter<String>

                    val position = myAdapter.getPosition(currentForm?.nazione)
                    spin.setSelection(position)

                    button_form_row.text = "MODIFCA"

                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


    }

    @SuppressLint("ResourceType")
    private fun addForm(){

         uni_ospitante = universitàospitante_form_row.text.toString()
         facoltà = facoltà_form_row.text.toString()
         permanenza = Integer.parseInt(mesipermanenza_form_row.text.toString())
         uni_partenza = universitàpartenza_form_row.text.toString()
         note = note_form_row.text.toString()

        if(uni_ospitante.isEmpty() || uni_ospitante.length < 3){
            universitàospitante_form_row.error = "Il campo non rispetta i paramentri di dimensione. Almeno 4 caratteri"
            universitàospitante_form_row.requestFocus()
            return
        }

        if(facoltà.isEmpty() || facoltà.length < 3){
            facoltà_form_row.error = "Il campo non rispetta i paramentri di dimensione. Almeno 4 caratteri"
            facoltà_form_row.requestFocus()
            return
        }

        if(permanenza.toString().isEmpty() || permanenza.toString().length > 2 || permanenza > 12 || permanenza < 1){
            mesipermanenza_form_row.error = "Il campo non rispetta i paramentri di dimensione. I mesi devono essere compresi tra 1 e 12"
            mesipermanenza_form_row.requestFocus()
            return
        }

        if(uni_partenza.isEmpty() || uni_partenza.length < 3){
            universitàpartenza_form_row.error = "Il campo non rispetta i paramentri di dimensione. Almeno 4 caratteri"
            universitàpartenza_form_row.requestFocus()
            return
        }

        if(note.isEmpty() || note.length < 20 || note.length > 500){
            note_form_row.error = "Il campo non rispetta i paramentri di dimensione. Almeno 20 caratteri e massimo 500"
            note_form_row.requestFocus()
            return
        }


        //firebase
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("form/$uid")


        val form = currentUser?.let {
            Form(currentUser?.name+" "+ currentUser?.surname, uni_ospitante, nazione, facoltà, permanenza, uni_partenza, note,
                it
            )
        }
        ref.setValue(form).addOnSuccessListener {

            val refUs = FirebaseDatabase.getInstance().getReference("/users/$uid")
            currentUser!!.first = false
            refUs.setValue(currentUser)
            updateUser()

            val intent = Intent(this, FormLogActivity::class.java )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }


    }

    /*//Quando l'activity viene chiusa
    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(this, FormLogActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }*/

    //Fetch User
    private fun fetchUser(){
        mPref= getSharedPreferences(KEY_USER_OBJECT,MODE_PRIVATE)

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