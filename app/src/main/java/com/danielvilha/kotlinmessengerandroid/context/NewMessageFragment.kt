package com.danielvilha.kotlinmessengerandroid.context

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.danielvilha.kotlinmessengerandroid.R
import com.danielvilha.kotlinmessengerandroid.views.User
import com.danielvilha.kotlinmessengerandroid.views.UserItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.fragment_new_message.*

private const val ARG_PARAM = "param"

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the interface.
 */
class NewMessageFragment : Fragment() {
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getParcelable(ARG_PARAM)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_message, container, false)
    }

    override fun onStart() {
        super.onStart()

        activity?.title = getString(R.string.select_user)

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

                adapter.setOnItemClickListener { item, _ ->
                    val userItem = item as UserItem
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.container, ChatMessageFragment.newInstance(userItem.user))
                        ?.commit()
                }

                recycler.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) { }
        })
    }

    companion object {
        private const val TAG = "NewMessageFragment"

        @JvmStatic
        fun newInstance(user: User) =
            NewMessageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM, user)
                }
            }

        @JvmStatic
        fun newInstance() = NewMessageFragment().apply {  }
    }
}
