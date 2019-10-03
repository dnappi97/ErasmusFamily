package com.example.erasmusfamily.view

import com.example.erasmusfamily.R
import com.example.erasmusfamily.models.User
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_form_item.view.*

class FormItem(val name: String, val uni_ospitante: String, val nazionalità: String, val facoltà: String, val permanenza: Int, val uni_partenza: String, val note: String, val user: User):
    Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.namestudent_form_item.text = name
        viewHolder.itemView.uniospitante_form_item.text = uni_ospitante
        viewHolder.itemView.nazione_form_item.text = nazionalità
        viewHolder.itemView.facoltà_form_item.text = facoltà
        viewHolder.itemView.permanenza_form_item.text = permanenza.toString()
        viewHolder.itemView.unipartenza_form_item.text = uni_partenza
        viewHolder.itemView.note_form_item.text = note
    }

    override fun getLayout(): Int {
        return R.layout.activity_form_item
    }

}