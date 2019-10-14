package com.example.erasmusfamily.Setting

import com.example.erasmusfamily.R
import com.example.erasmusfamily.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_setting_item.view.*

class SettingItem( val user: User): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.nameuser_setting.text = user.name+" "+user.surname

        viewHolder.itemView.emailuser_setting.text = user.email

        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageview_setting)

        if(user.andato){

            viewHolder.itemView.tipebrother_setting.text = "Big Brothers"
            viewHolder.itemView.iconetipe_setting.setImageResource(R.drawable.ic_bigbrothers)

        } else {

            viewHolder.itemView.tipebrother_setting.text = "Little Brothers"
            viewHolder.itemView.iconetipe_setting.setImageResource(R.drawable.ic_littlebrothers)
        }


    }

    override fun getLayout(): Int {
        return R.layout.activity_setting_item
    }
}