package com.example.erasmusfamily.Social

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.erasmusfamily.R
import com.example.erasmusfamily.messages.MessagesActivity
import com.example.erasmusfamily.models.Form
import com.example.erasmusfamily.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_form_compile.*

class FormActivity: AppCompatActivity(){

    companion object {
        var currentUser: User? = null
    }

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

        fetchCurrentUser()

        button_form_row.setOnClickListener{
            addForm()
        }
    }

    @SuppressLint("ResourceType")
    private fun addForm(){

         uni_ospitante = universitàospitante_form_row.text.toString()
         facoltà = facoltà_form_row.text.toString()
         permanenza = Integer.parseInt(mesipermanenza_form_row.text.toString())
         uni_partenza = universitàpartenza_form_row.text.toString()
         note = note_form_row.text.toString()

        if(uni_ospitante.isEmpty() || uni_ospitante.length < 3){
            universitàospitante_form_row.setError("Il campo non rispetta i paramentri di dimensione. Almeno 4 caratteri")
            universitàospitante_form_row.requestFocus()
            return
        }

        if(facoltà.isEmpty() || facoltà.length < 3){
            facoltà_form_row.setError("Il campo non rispetta i paramentri di dimensione. Almeno 4 caratteri")
            facoltà_form_row.requestFocus()
            return
        }

        if(permanenza.toString().isEmpty() || permanenza.toString().length > 2 || permanenza > 12 || permanenza < 1){
            mesipermanenza_form_row.setError("Il campo non rispetta i paramentri di dimensione. I mesi devono essere compresi tra 1 e 12")
            mesipermanenza_form_row.requestFocus()
            return
        }

        if(uni_partenza.isEmpty() || uni_partenza.length < 3){
            universitàpartenza_form_row.setError("Il campo non rispetta i paramentri di dimensione. Almeno 4 caratteri")
            universitàpartenza_form_row.requestFocus()
            return
        }

        if(note.isEmpty() || note.length < 20){
            note_form_row.setError("Il campo non rispetta i paramentri di dimensione. Almeno 20 caratteri")
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

            val intent = Intent(this, FormLogActivity::class.java )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }


    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("LatestMessages", "Current user ${MessagesActivity.currentUser?.profileImageUrl}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}