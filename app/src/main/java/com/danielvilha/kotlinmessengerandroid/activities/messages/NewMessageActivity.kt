package com.danielvilha.kotlinmessengerandroid.activities.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.danielvilha.kotlinmessengerandroid.R
import com.danielvilha.kotlinmessengerandroid.views.User
import com.danielvilha.kotlinmessengerandroid.views.UserItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_new_message_row.view.*

/**
 * Created by danielvilha on 2019-07-01
 */
class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = getString(R.string.select_user)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fetchUsers()
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach {
                    Log.d(TAG, it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null)
                        adapter.add(UserItem(user))
                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatMessageActivity::class.java)
                    intent.putExtra(TAG, userItem.user)
                    startActivity(intent)
                }

                recycler.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) { }
        })
    }

    companion object {
        const val TAG = "NewMessageActivity"
    }
}
