package com.example.erasmusfamily.view

import com.example.erasmusfamily.R
import com.example.erasmusfamily.models.User
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_request_item.view.*

class RequestItem(val name: String, val title: String, val text: String, val user: User):
    Item<ViewHolder>() {


    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.namestudent_request_item.text = name
        viewHolder.itemView.titlerequest_request_item.text = title
        viewHolder.itemView.textrequest_request_item.text = text


    }

    override fun getLayout(): Int {
        return R.layout.activity_request_item
    }

}