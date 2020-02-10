

package com.example.erasmusfamily.Social

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.erasmusfamily.R
import com.example.erasmusfamily.Setting.SettingActivity
import com.example.erasmusfamily.messages.MessagesActivity
import com.example.erasmusfamily.models.User
import com.example.erasmusfamily.registerlogin.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_check_document.*


class CheckDocumentActivity : AppCompatActivity() {

    companion object {
        val TAG = "MeteViewActivity"
        var currentUser: User? = null
    }

    lateinit var mPref: SharedPreferences
    private val KEY_USER_OBJECT = "USER"
    private val key_user = "USER"

    lateinit var checkPref: SharedPreferences

    private val KEY_CHECK = "CHECK"
    private val key_LearningAgreement = "LA"
    private val key_AttestatoDiPermanenza = "AP"
    private val key_TrascriptOfRecords = "TR"
    private val key_Riconoscimento= "R"
    private val key_OLS= "OLS"


    lateinit var test: String

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_document)

        checkPref = getSharedPreferences(KEY_CHECK, Context.MODE_PRIVATE)

        //CHECKBOX

        //LearingAgreement
        test = checkPref.getString(key_LearningAgreement, "")
        if(!test.isEmpty()){
            checkBox_LearningAgreement!!.isChecked = true
        }

        checkBox_LearningAgreement.setOnCheckedChangeListener { _, _ ->
            checked()
        }

        //Attestato di permanenza
        test = checkPref.getString(key_AttestatoDiPermanenza, "")
        if(!test.isEmpty()){
            checkBox_AttestatoDiPermanenza!!.isChecked = true
        }

        checkBox_AttestatoDiPermanenza.setOnCheckedChangeListener{ _, _ ->
            checked()
        }

        //Trascript Of Records
        test= checkPref.getString(key_TrascriptOfRecords, "")
        if(!test.isEmpty()){
            checkBox_TrascriptOfRecords!!.isChecked = true
        }

        checkBox_TrascriptOfRecords.setOnCheckedChangeListener{ _, _ ->
            checked()
        }

        //Riconoscimento attività formative
        test= checkPref.getString(key_Riconoscimento, "")
        if(!test.isEmpty()){
            checkBox_Riconoscimento!!.isChecked = true
        }

        checkBox_Riconoscimento.setOnCheckedChangeListener{ _, _ ->
            checked()
        }

        //OLS
        test= checkPref.getString(key_OLS, "")
        if(!test.isEmpty()){
            checkBox_OLS!!.isChecked = true
        }

        checkBox_OLS.setOnCheckedChangeListener{ _, _ ->
            checked()
        }


        //Impostazioni testuali
        setTextView()
    }

    fun setTextView(){

        //Testo cliccabile

        //TextView Info
        val styledString_info = SpannableString("Prima di partire assicurati di aver eseguito tutti i passaggi, in modo da non avere problemi durante la tua permanenza.\n" +
                "Tieni traccia dei passaggi effettuati.\nLa documentazione a cui si fa riferimento si trova qui.")

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val browserIntent=
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://web.unisa.it/unisa-rescue-page/dettaglio/id/662/module/180/row/3"))
                startActivity(browserIntent)
            }
        }

        styledString_info.setSpan(clickableSpan, 210, 213, 0)
        findViewById<TextView>(R.id.textSpiegazione_CheckDocument).movementMethod = LinkMovementMethod.getInstance()
        findViewById<TextView>(R.id.textSpiegazione_CheckDocument).text = styledString_info


        //TextView Learning Agreement
        val la = SpannableString("Il Learning Agreement è il documento principale per iniziare il tuo Erasmus.\nDescrive le attività formative che seguirai durante quest'esperienza, per saperne di più clicca qui.\nDeve essere firmato in originale dal tuo Docente Tutor e da quello della tua Istituzione ospitante.")

        val clicableLA_info = object : ClickableSpan(){
            override fun onClick(widget: View) {
                val browserIntent=
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://ec.europa.eu/programmes/erasmus-plus/resources/documents/applicants/learning-agreement_it"))
                startActivity(browserIntent)            }

        }
        val clicableLA = object : ClickableSpan(){
            override fun onClick(widget: View) {
                val browserIntent=
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://web.unisa.it/uploads/rescue/180/3/era+-learning-agreement-for-study.-doc-2.doc"))
                startActivity(browserIntent)            }

        }

        la.setSpan(clicableLA_info, 166, 176, 0)
        la.setSpan(clicableLA, 3, 22, 0)
        findViewById<TextView>(R.id.textView_LearningAgreement).movementMethod = LinkMovementMethod.getInstance()
        findViewById<TextView>(R.id.textView_LearningAgreement).text = la


        //TextView Attestato di permanenza
        val ap = SpannableString("L'Attestato di permanenza deve essere stampato e consegnato all'arrivo presso l'Istituzione ospitante per essere firmato, in modo da determinare la data d'arrivo.\nDeve essere firmato anche prima della partenza, in modo da determinare la data di ritorno.\nStabilisce la durata della permanenza, in base alla quale riceverai la borsa di studio corrispondente.")

        val clicabileAP = object: ClickableSpan(){
            override fun onClick(widget: View) {
                val browserIntent=
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://web.unisa.it/uploads/rescue/180/3/Attestato-di-permanenza---Certificate-of-Stay.pdf"))
                startActivity(browserIntent)               }

        }

        val clicabileAP_borsaDiStudio = object: ClickableSpan(){
            override fun onClick(widget: View) {
                val browserIntent=
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://web.unisa.it/uploads/rescue/164/2601/art.-20-tabella-fondi-1-.pdf"))
                startActivity(browserIntent)               }

        }

        ap.setSpan(clicabileAP, 2, 25, 0)
        ap.setSpan(clicabileAP_borsaDiStudio, 325, 340, 0)
        findViewById<TextView>(R.id.textView_AttestatoDiPermanenza).movementMethod = LinkMovementMethod.getInstance()
        findViewById<TextView>(R.id.textView_AttestatoDiPermanenza).text = ap

        //Riconoscimento delle attività formative
        val ra = SpannableString("Riconoscimento attività didattica mobilità per studio o mobilità per ricerca tesi.\nBisogna compilarlo al termine dell'esperienza Erasmus e presentarlo al Docente Tutor, in modo da chiudere la pratica.")

        val clicabileRA_studio = object : ClickableSpan(){
            override fun onClick(widget: View) {
                val browserIntent=
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://web.unisa.it/uploads/rescue/180/3/Riconoscimento-mobilit%C3%A0-per-studio.doc"))
                startActivity(browserIntent)             }

        }

        val clicabileRA_tesi = object : ClickableSpan(){
            override fun onClick(widget: View) {
                val browserIntent=
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://web.unisa.it/uploads/rescue/180/3/Riconoscimento-mobilit%C3%A0-per-tesi(1).doc"))
                startActivity(browserIntent)             }

        }
        ra.setSpan(clicabileRA_studio, 34, 53, 0)
        ra.setSpan(clicabileRA_tesi, 56, 81, 0)
        findViewById<TextView>(R.id.textView_Riconoscimento).movementMethod = LinkMovementMethod.getInstance()
        findViewById<TextView>(R.id.textView_Riconoscimento).text = ra

        //OLS
        val ols =  SpannableString("Il test di lingua OLS (Online Linguistic Support) deve essere sostenuto due volte, all'inizio e al termine dell'esperienza Erasmus, in modo da valutare il progresso linguistico.")

        val clicabileOLS = object: ClickableSpan(){
            override fun onClick(widget: View) {
                val browserIntent=
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://ec.europa.eu/programmes/erasmus-plus/resources/online-linguistic-support_it"))
                startActivity(browserIntent)             }

        }

        ols.setSpan(clicabileOLS, 18, 49, 0)
        findViewById<TextView>(R.id.textView_OLS).movementMethod = LinkMovementMethod.getInstance()
        findViewById<TextView>(R.id.textView_OLS).text = ols

    }

    fun checked(){
        checkPref = getSharedPreferences(KEY_CHECK, Context.MODE_PRIVATE)


        //LearningAgreement
       if(checkBox_LearningAgreement.isChecked){
           checkPref.edit()
               .putString(key_LearningAgreement, "OK")
               .apply()
       } else {
           checkPref.edit().remove(key_LearningAgreement).apply()
       }

        //Attestato di permanenza
        if(checkBox_AttestatoDiPermanenza.isChecked){
            checkPref.edit()
                .putString(key_AttestatoDiPermanenza, "OK")
                .apply()
        } else {
            checkPref.edit().remove(key_AttestatoDiPermanenza).apply()
        }

        //Trascript of Records
        if(checkBox_TrascriptOfRecords.isChecked){
            checkPref.edit()
                .putString(key_TrascriptOfRecords, "OK")
                .apply()
        } else {
            checkPref.edit().remove(key_TrascriptOfRecords).apply()
        }


        //Riconoscimeneto attività formative
        if(checkBox_Riconoscimento.isChecked){
            checkPref.edit()
                .putString(key_Riconoscimento, "OK")
                .apply()

        } else {
            checkPref.edit().remove(key_Riconoscimento).apply()
        }

        //OLS
        if(checkBox_OLS.isChecked){
            checkPref.edit()
                .putString(key_OLS, "OK")
                .apply()
        } else {
            checkPref.edit().remove(key_OLS).apply()
        }
    }


    //Menu
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.navigation_message -> {
                val intent = Intent(this, MessagesActivity::class.java)
                startActivity(intent)
            }
            R.id.navigation_form -> {
                val intent = Intent(this, FormLogActivity::class.java)
                startActivity(intent)
            }
            R.id.navigation_Setting -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
            }
            R.id.new_request -> {
                val intent = Intent(this, RequestActivity::class.java)
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
        menuInflater.inflate(R.menu.navigationmenu_documents, menu)
        return super.onCreateOptionsMenu(menu)
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


}
