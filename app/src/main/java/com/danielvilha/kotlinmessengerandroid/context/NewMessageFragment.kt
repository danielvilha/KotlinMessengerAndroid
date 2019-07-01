package com.danielvilha.kotlinmessengerandroid.context

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.danielvilha.kotlinmessengerandroid.R
import com.danielvilha.kotlinmessengerandroid.views.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

import kotlinx.android.synthetic.main.fragment_new_message.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the interface.
 */
class NewMessageFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_message, container, false)
    }

    override fun onStart() {
        super.onStart()

        fetchUsers()
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach {
                    Log.d(TAG, "")
                    val user = it.getValue(User::class.java)
                    adapter.add(UserItem())
                }

                recycler.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    companion object {
        private const val TAG = "NewMessageFragment"
        const val ARG_PARAM = "arg_param"

        @JvmStatic
        fun newInstance(param: Int) =
            NewMessageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM, param)
                }
            }
    }
}

class UserItem: Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLayout(): Int {
        return R.layout.user_new_message_row
    }

}
