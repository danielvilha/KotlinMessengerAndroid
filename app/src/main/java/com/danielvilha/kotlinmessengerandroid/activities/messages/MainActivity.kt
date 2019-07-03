package com.danielvilha.kotlinmessengerandroid.activities.messages

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.danielvilha.kotlinmessengerandroid.R
import com.danielvilha.kotlinmessengerandroid.activities.registerlogin.LoginActivity
import com.danielvilha.kotlinmessengerandroid.context.LatestMessageFragment
import com.danielvilha.kotlinmessengerandroid.context.NewMessageFragment
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        verifyUserIsLogged()
    }

    private fun verifyUserIsLogged() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, LatestMessageFragment.newInstance())
                .commit()
        }
    }

    private val clickNegativeButton = { dialog: DialogInterface, _: Int -> dialog.dismiss() }

    private val clickPositiveRemoveButton = { _: DialogInterface, _: Int ->
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
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
                supportFragmentManager.beginTransaction()
                    .add(R.id.container, NewMessageFragment.newInstance())
                    .commit()
            }

            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
