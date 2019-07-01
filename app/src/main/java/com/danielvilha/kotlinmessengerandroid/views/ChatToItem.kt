package com.danielvilha.kotlinmessengerandroid.views

import com.danielvilha.kotlinmessengerandroid.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_to_row.view.*

/**
 * Created by danielvilha on 2019-07-01
 */
class ChatToItem(val text: String, var user: User): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txv_chat_to.text = text

        val url = user.profileImageUrl
        val targetImageView = viewHolder.itemView.img_avatar_to
        Picasso.get().load(url).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}