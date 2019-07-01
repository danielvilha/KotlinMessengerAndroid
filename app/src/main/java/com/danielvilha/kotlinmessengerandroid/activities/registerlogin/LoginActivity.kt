package com.danielvilha.kotlinmessengerandroid.activities.registerlogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.danielvilha.kotlinmessengerandroid.R
import com.danielvilha.kotlinmessengerandroid.activities.messages.LatestMessageActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.title = getString(R.string.title_activity_login)

        btn_login.setOnClickListener {
            performLogin()
        }

        tvw_create_account.setOnClickListener {
            // Start RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun performLogin() {
        val email = edt_email.text.toString()
        val password = edt_password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(edt_email, R.string.login_error, Snackbar.LENGTH_LONG).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d(TAG, "Successfully logged in: ${it.result?.user?.uid}")

                val intent = Intent(this, LatestMessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Snackbar.make(edt_email, "Failed to login: ${it.message}", Snackbar.LENGTH_LONG).show()
            }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
