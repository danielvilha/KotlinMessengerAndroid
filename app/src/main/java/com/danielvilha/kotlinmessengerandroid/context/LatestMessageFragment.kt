package com.danielvilha.kotlinmessengerandroid.context

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.danielvilha.kotlinmessengerandroid.R
import com.danielvilha.kotlinmessengerandroid.views.ChatMessage
import com.danielvilha.kotlinmessengerandroid.views.LatestMessageRow
import com.danielvilha.kotlinmessengerandroid.views.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

import kotlinx.android.synthetic.main.fragment_latest_message.*

/**
 * A simple [Fragment] subclass.
 * Use the [LatestMessageFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class LatestMessageFragment : Fragment() {
    private var adapter = GroupAdapter<ViewHolder>()
    private val latestMessageMap = HashMap<String, ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_latest_message, container, false)
    }

    override fun onStart() {
        super.onStart()

        recycler.adapter = adapter

        adapter.setOnItemClickListener { item, _ ->
            Log.d(TAG, "Clicked user: $item")
            val  row = item as LatestMessageRow
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, ChatMessageFragment.newInstance(row.chatPartnerUser!!))
                ?.commit()
        }

        fetchCurrentUser()

        listenForLatestMessages()
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val reference = FirebaseDatabase.getInstance().getReference("/users/$uid")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
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

    companion object {
        var currentUser: User? = null
        const val TAG = "LatestMessageFragment"

        /**
         * @return A new instance of fragment LatestMessageFragment.
         */
        @JvmStatic
        fun newInstance() = LatestMessageFragment().apply { }
    }
}
