package com.example.erasmusfamily.Setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.erasmusfamily.R
import com.example.erasmusfamily.registerlogin.MainActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_changepassword.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class ChangePasswordActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changepassword)

        verifyUserIsLoggedIn()
        changePassword()
    }

    private fun changePassword() {

        val vecchia = findViewById<TextView>(R.id.vecchiapassword_changepassword)
        val nuova = findViewById<TextView>(R.id.nuovapassword_changepassword)
        val conferma = findViewById<TextView>(R.id.confermapassword_changepassword)
        val changeButton = findViewById<Button>(R.id.changepassword_button_changepassword)

        changeButton.setOnClickListener {
            if (vecchiapassword_changepassword.text.toString().isEmpty()) {
                vecchiapassword_changepassword.setError("Compila il campo")
                vecchiapassword_changepassword.requestFocus()
            }

            else if (nuovapassword_changepassword.text.toString().isEmpty()) {
                nuovapassword_changepassword.setError("Compila il campo")
                nuovapassword_changepassword.requestFocus()
            }

            else if (conferma.text.toString().isEmpty()) {
                confermapassword_changepassword.setError("Compila il campo")
                confermapassword_changepassword.requestFocus()
            }

            else if(!isValidPassword(nuovapassword_changepassword.text.toString())){

                nuovapassword_changepassword.setError("La password inserita non rispetta i parametri.\nLa password deve contenere minimo 8 caratteri e massimo 20, tra cui almeno un numero ed una lettera maiuscola.")
                nuovapassword_changepassword.requestFocus()
            }

            else if ( nuovapassword_changepassword.text.toString().equals(confermapassword_changepassword.text.toString()) ) {

                val user = FirebaseAuth.getInstance().currentUser

                if (user != null && user.email != null) {

                    val credential = EmailAuthProvider
                        .getCredential(user.email!!, vecchia.text.toString())

                    progressBar_changepassword.visibility = View.VISIBLE

                    user.reauthenticate(credential)
                        .addOnCompleteListener {

                            if (it.isSuccessful) {

                                user.updatePassword(nuova.text.toString())
                                    .addOnCompleteListener { task ->

                                        progressBar_changepassword.visibility = View.GONE

                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                this,
                                                "Password modificata con successo",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            val intent = Intent(this, SettingActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(intent)
                                        }
                                    }
                            } else {
                                progressBar_changepassword.visibility = View.GONE

                                Toast.makeText(
                                    this,
                                    "Assicurati di aver scritto la tua password corrente correttamente",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(
                    this,
                    "Le password non coincidono, riscrivere correttamente i campi",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    protected fun isValidPassword(password: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val Password_Pattern = "(?=^.{8,20}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"
        pattern = Pattern.compile(Password_Pattern)
        matcher = pattern.matcher(password)

        return matcher.matches()
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
}