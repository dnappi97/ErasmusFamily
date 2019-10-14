package com.example.erasmusfamily.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.erasmusfamily.R
import com.example.erasmusfamily.Setting.SettingActivity
import com.example.erasmusfamily.Social.FormActivity
import com.example.erasmusfamily.Social.FormLogActivity
import com.example.erasmusfamily.Social.RequestLogActivity
import com.example.erasmusfamily.models.ChatMessage
import com.example.erasmusfamily.models.User
import com.example.erasmusfamily.registerlogin.MainActivity
import com.example.erasmusfamily.view.LatestMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_messages.*

class MessagesActivity : AppCompatActivity() {

    companion object {
        val NAME_KEY = "NAME_KEY"
        var currentUser: User? = null
        val TAG = "MessagesActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        supportActionBar?.title ="Chat"

        recycleview_messages_activity.adapter = adapter
        recycleview_messages_activity.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener{ item, _ ->
            Log.d(TAG, "click user")
            val intent = Intent(this, ChatLogActivity::class.java)

            val row = item as LatestMessageRow

           intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        listenForLatestMessages()

        fetchCurrentUser()

        verifyUserIsLoggedIn()

        indirizzaUserFirst()
    }

    private fun indirizzaUserFirst(){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        if(currentUser == null ){
            Toast.makeText(this, "Ops, si è verificato un problema. Riprova ad effettuare l'accesso", Toast.LENGTH_LONG).show()
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this, MainActivity::class.java )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            if(currentUser!!.first){
                val name = currentUser!!.name+" "+ currentUser!!.surname

                if(currentUser!!.andato){



                    intent.putExtra(NAME_KEY, name)

                    Toast.makeText(this, "Benvenuto "+name+", compila questo form in modo da aiutare altri ragazzi in questa esperienza!", Toast.LENGTH_LONG).show()

                    val intent = Intent(this, FormActivity::class.java )
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {

                    currentUser!!.first= false
                    ref.setValue(currentUser)

                    Toast.makeText(this, "Benvenuto "+name+", dai un'occhiata e contatta qualcuno se può esserti utile!", Toast.LENGTH_LONG).show()

                    val intent = Intent(this, FormLogActivity::class.java )
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }



    }

    val latestMessagesMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    val adapter = GroupAdapter<ViewHolder>()

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("LatestMessages", "Current user ${currentUser?.profileImageUrl}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
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

            R.id.navigation_form -> {
                val intent = Intent(this, FormLogActivity::class.java)
                startActivity(intent)
            }
            R.id.navigation_request -> {
                val intent = Intent(this, RequestLogActivity::class.java)
                startActivity(intent)
            } R.id.navigation_Setting -> {
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
        menuInflater.inflate(R.menu.navigationmenu_messages, menu)
        return super.onCreateOptionsMenu(menu)
    }


}