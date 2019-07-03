package com.danielvilha.kotlinmessengerandroid.views

import com.danielvilha.kotlinmessengerandroid.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.user_new_message_row.view.*

/**
 * Created by danielvilha on 2019-07-01
 */
class UserItem(val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txv_name.text = user.username

        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.img_avatar)
    }

    override fun getLayout(): Int {
        return R.layout.user_new_message_row
    }
}