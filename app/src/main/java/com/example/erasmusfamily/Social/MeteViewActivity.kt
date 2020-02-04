package com.example.erasmusfamily.Social

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import com.example.erasmusfamily.R
import com.example.erasmusfamily.Setting.SettingActivity
import com.example.erasmusfamily.Setting.SettingActivity.Companion.mPref
import com.example.erasmusfamily.messages.MessagesActivity
import com.example.erasmusfamily.models.User
import com.example.erasmusfamily.registerlogin.MainActivity
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson

class MeteViewActivity : AppCompatActivity() {

    companion object {
        val TAG = "MeteViewActivity"
        var currentUser: User? = null
    }

    lateinit var mPref: SharedPreferences
    private val KEY_USER_OBJECT = "USER"
    private val key_user = "USER"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mete_view)

        var list_of_pdf = arrayOf("DIPARTIMENTO DI CHIMICA E BIOLOGIA", "DIPARTIMENTO DI FARMACIA", "DIPARTIMENTO DI FISICA", "DIPARTIMENTO DI INFORMATICA", "DIPARTIMENTO DI INGEGNERIA CIVILE",
            "DIPARTIMENTO DI INGEGNERIA DELL'INFORMAZIONE ED ELETTRICA E MATEMATICA APLLICATA", "DIPARTIMENTO DI INGEGNERIA INDUSTRIALE", "DIPARTIMENTO DI MATEMATICA",
            "DIPARTIMENTO DI MEDICINA", "DIPARTIMENTO DI SCIENZE AZIENDALI- MANAGEMENT E INNOVATION SYSTEMS", "DIPARTIMENTO DI SCIENZE DEL PATRIMONIO CULTURALE", "DIPARTIMENTO DI SCIENZE ECONOMICHE E STATISTICHE",
            "DIPARTIMENTO DI SCIENZE GIURIDICHE", "DIPARTIMENTO DI SCIENZE POLITICHE E DELLA COMUNICAZIONE", "DIPARTIMENTO DI SCIENZE UMANE, FILOSOFICHE E DELLA FORMAZIONE", "DIPARTIMENTO DI STUDI POLITICI E SOCIALI",
            "DIPARTIMENTO DI STUDI UMANISTICI")

        var facolta = ""

        var spin: Spinner= findViewById(R.id.spinner_pdf) as Spinner
        var aa: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, list_of_pdf)

        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spin.adapter = aa
        spin.prompt = "Seleziona una facoltà"
        spin.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                facolta = spin.getItemAtPosition(position).toString()
            }

        }

        val button = findViewById(R.id.button_pdf) as Button
        button.setOnClickListener{
            ViewPdf(facolta)
        }

    }


    private fun ViewPdf(name: String){

        val pdf = findViewById(R.id.pdf) as PDFView
        pdf.visibility = View.VISIBLE
        pdf.fromAsset(name+".pdf").load()
    }

    //Fetch User
    private fun fetchUser(){
        mPref = getSharedPreferences(KEY_USER_OBJECT ,MODE_PRIVATE)

        val gson: Gson= Gson()
        val json = mPref.getString(key_user, "")
        MessagesActivity.currentUser= gson.fromJson(json, User::class.java)


    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            Toast.makeText(this, "Ops. Si è verificato un problema, riprova l'accesso.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }


    //Menu
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.navigation_form -> {
                val intent = Intent(this, FormLogActivity::class.java)
                startActivity(intent)
            }
            R.id.navigation_message -> {
                val intent = Intent(this, MessagesActivity::class.java)
                startActivity(intent)
            }
            R.id.navigation_request -> {
                val intent = Intent(this, RequestLogActivity::class.java)
                startActivity(intent)
            }
            R.id.navigation_Setting -> {
            val intent = Intent(this, SettingActivity::class.java)
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
        menuInflater.inflate(R.menu.navigationmenu_mete, menu)
        return super.onCreateOptionsMenu(menu)
    }

}
