package com.danielvilha.kotlinmessengerandroid.activities.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.danielvilha.kotlinmessengerandroid.R
import com.danielvilha.kotlinmessengerandroid.common.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val user = intent.getParcelableExtra<User>(NewMessageActivity.TAG)
        supportActionBar?.title = user.username

        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())

        recycler.adapter = adapter
    }
}

class ChatFromItem: Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem: Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
