package com.danielvilha.kotlinmessengerandroid.activities.messages

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.danielvilha.kotlinmessengerandroid.R
import com.danielvilha.kotlinmessengerandroid.activities.registerlogin.LoginActivity
import com.danielvilha.kotlinmessengerandroid.views.ChatMessage
import com.danielvilha.kotlinmessengerandroid.views.LatestMessageRow
import com.danielvilha.kotlinmessengerandroid.views.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

import kotlinx.android.synthetic.main.activity_latest_message.*

/**
 * Created by danielvilha on 2019-07-01
 */
class LatestMessageActivity : AppCompatActivity() {

    private var adapter = GroupAdapter<ViewHolder>()
    private val latestMessageMap = HashMap<String, ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_message)

        recycler.adapter = adapter

        adapter.setOnItemClickListener { item, _ ->
            Log.d(TAG, "Clicked user: $item")
            val intent = Intent(this, ChatMessageActivity::class.java)
            val  row = item as LatestMessageRow
            intent.putExtra(NewMessageActivity.TAG, row.chatPartnerUser)
            startActivity(intent)
        }

        verifyUserIsLogged()

        fetchCurrentUser()

        listenForLatestMessages()
    }

    private fun verifyUserIsLogged() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val reference = FirebaseDatabase.getInstance().getReference("/users/$uid")
        reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d(TAG, "Current user: ${currentUser?.username}")
            }

            override fun onCancelled(p0: DatabaseError) { }
        })
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val reference = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return

                latestMessageMap[p0.key!!] = chatMessage
                refreshMessageMap()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return

                latestMessageMap[p0.key!!] = chatMessage
                refreshMessageMap()
            }

            override fun onCancelled(p0: DatabaseError) { }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) { }
            override fun onChildRemoved(p0: DataSnapshot) { }
        })
    }

    private fun refreshMessageMap() {
        adapter.clear()
        latestMessageMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private val clickNegativeButton = { dialog: DialogInterface, _: Int -> dialog.dismiss() }

    private val clickPositiveRemoveButton = { _: DialogInterface, _: Int ->
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_sign_out -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.exit)
                    .setMessage(R.string.do_you_want_to_exit)
                    .setCancelable(true)
                    .setPositiveButton(R.string.ok, DialogInterface.OnClickListener(function = clickPositiveRemoveButton))
                    .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener(function = clickNegativeButton))
                    .show()
            }

            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    companion object {
        var currentUser: User? = null
        val TAG = "LatestMessageActivity"
    }
}