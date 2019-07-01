package com.danielvilha.kotlinmessengerandroid.views

import com.danielvilha.kotlinmessengerandroid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

import kotlinx.android.synthetic.main.latest_message_row.view.*

/**
 * Created by danielvilha on 2019-07-01
 */
class LatestMessageRow(private val chatMessage: ChatMessage): Item<ViewHolder>() {
    var chatPartnerUser: User? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val chatPartnerId = if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatMessage.toId
        } else {
            chatMessage.fromId
        }

        val reference = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java) ?: return
                viewHolder.itemView.txv_username.text = chatPartnerUser?.username

                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(viewHolder.itemView.img_avatar)
            }

            override fun onCancelled(p0: DatabaseError) { }
        })

        viewHolder.itemView.txv_last_message.text = chatMessage.text

    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}