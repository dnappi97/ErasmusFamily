package com.example.erasmusfamily.view

import com.example.erasmusfamily.R
import com.example.erasmusfamily.models.ChatMessage
import com.example.erasmusfamily.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.messages_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>() {
    var chatPartnerUser: User? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.latestmessage_messages_row.text = chatMessage.text

        val chatPartenerId: String

        if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
            chatPartenerId = chatMessage.toId
        } else {
            chatPartenerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartenerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.name_user_messages_row.text = chatPartnerUser?.name+" "+chatPartnerUser?.surname

                val targetImageView = viewHolder.itemView.imageview_messages_row
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.messages_row
    }
}