package com.example.erasmusfamily.Social

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.erasmusfamily.R
import com.example.erasmusfamily.Setting.SettingActivity
import com.example.erasmusfamily.messages.ChatLogActivity
import com.example.erasmusfamily.messages.MessagesActivity
import com.example.erasmusfamily.models.ChatMessage
import com.example.erasmusfamily.models.Request
import com.example.erasmusfamily.registerlogin.MainActivity
import com.example.erasmusfamily.view.RequestItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.activity_request_log.*

class RequestLogActivity: AppCompatActivity(){
    companion object{
        val USER_KEY = "USER_KEY"
    }

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_log)

        supportActionBar?.title ="Little Brothers"

        recycleview_request_log.adapter = adapter
        recycleview_request_log.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        verifyUserIsLoggedIn()

        firstAccess()
        searchRequest()
    }

    private fun firstAccess(){
        val ref = FirebaseDatabase.getInstance().getReference("request")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val request = p0.getValue(Request::class.java)
                if(request == null) {
                    norequest_request_log.visibility = View.VISIBLE
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun searchRequest(){

        val ref = FirebaseDatabase.getInstance().getReference("request")
        ref.addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val request = p0.getValue(Request::class.java) ?: return

                adapter.add(RequestItem(request.name, request.title, request.text, request.user))


            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val request = p0.getValue(Request::class.java) ?: return

                adapter.add(RequestItem(request.name, request.title, request.text, request.user))

            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                val request = p0.getValue(Request::class.java) ?: return

                adapter.add(RequestItem(request.name, request.title, request.text, request.user))

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        adapter.setOnItemClickListener{ item, view ->

            val requestItem = item as RequestItem

            if(FirebaseAuth.getInstance().uid == item.user.uid){

                val dialog = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.activity_elimination_request, null)
                val textViewChat = dialogView.findViewById<TextView>(R.id.textview_elimination_request)
                val buttonElimination = dialogView.findViewById<Button>(R.id.button_elimination_request)
                val buttonModifica = dialogView.findViewById<Button>(R.id.button_modifica_request)
                dialog.setView(dialogView)
                dialog.setCancelable(true)
                textViewChat.text = "Vuoi eliminare la tua domanda?"


                buttonElimination.setOnClickListener{
                    val uid = FirebaseAuth.getInstance().uid
                    val refReq = FirebaseDatabase.getInstance().getReference("request/$uid")
                    refReq.removeValue()

                    val intent = Intent(view.context, RequestLogActivity::class.java)

                    intent.putExtra(USER_KEY, requestItem.user)
                    startActivity(intent)
                }

                buttonModifica.setOnClickListener{

                    val intent = Intent(view.context, RequestActivity::class.java)

                    intent.putExtra(USER_KEY, requestItem.user)
                    startActivity(intent)

                }

                dialog.show()
            }
            else {

                val dialog = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.activity_request_chat, null)
                val textViewChat = dialogView.findViewById<TextView>(R.id.chatrequest_textview_request_chat)
                val buttonChat = dialogView.findViewById<Button>(R.id.buttonchat_request_chat)
                dialog.setView(dialogView)
                dialog.setCancelable(true)
                textViewChat.text = "Vuoi rispondere alla domanda di "+item.name+" ?"
                buttonChat.setOnClickListener{
                    val intent = Intent(view.context, ChatLogActivity::class.java)

                    intent.putExtra(USER_KEY, requestItem.user)
                    startActivity(intent)
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
            R.id.navigation_document ->{
                val intent = Intent(this, CheckDocumentActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigationmenu_littlebrothers, menu)
        return super.onCreateOptionsMenu(menu)
    }


}