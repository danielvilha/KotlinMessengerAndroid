package com.danielvilha.kotlinmessengerandroid.activities.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.danielvilha.kotlinmessengerandroid.R
import com.danielvilha.kotlinmessengerandroid.views.ChatFromItem
import com.danielvilha.kotlinmessengerandroid.views.ChatMessage
import com.danielvilha.kotlinmessengerandroid.views.ChatToItem
import com.danielvilha.kotlinmessengerandroid.views.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

import kotlinx.android.synthetic.main.activity_chat_message.*

/**
 * Created by danielvilha on 2019-07-01
 */
class ChatMessageActivity : AppCompatActivity() {
    private var toUser: User? = null
    private var adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_message)

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.TAG)
        supportActionBar?.title = toUser?.username

        recycler.adapter = adapter

        listenForMessages()

        btn_send.setOnClickListener {
            Log.d(TAG, "Attempt send new message ...")
            performSendMessage()
        }
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessageActivity.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text, currentUser))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }

                recycler.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onCancelled(p0: DatabaseError) { }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) { }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) { }

            override fun onChildRemoved(p0: DataSnapshot) { }
        })
    }

    private fun performSendMessage() {
        val text = edt_enter_message.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid

        if (fromId == null) return

        if (toId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val latestReference = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        val latestToReference = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                edt_enter_message.text.clear()
                recycler.scrollToPosition(adapter.itemCount - 1)
            }

        toReference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved to chat message: ${toReference.key}")
            }

        latestReference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved latest message: ${latestReference.key}")
            }

        latestToReference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved latest to message: ${latestToReference.key}")
            }
    }

    companion object {
        val TAG = "ChatMessageActivity"
    }
}






