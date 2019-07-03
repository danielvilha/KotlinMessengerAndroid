package com.danielvilha.kotlinmessengerandroid.activities.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.danielvilha.kotlinmessengerandroid.R
import com.danielvilha.kotlinmessengerandroid.activities.messages.LatestMessageActivity
import com.danielvilha.kotlinmessengerandroid.views.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

import kotlinx.android.synthetic.main.activity_register.*
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    private var selectedUriPhoto: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.title = getString(R.string.title_activity_create_account)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        btn_login.setOnClickListener {
            val email = edt_email.text.toString()
            val password = edt_password.text.toString()

            createAccount(email, password)
        }

        btn_image.setOnClickListener {
            // Image picture
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        tvw_create_account.setOnClickListener {
            // Start LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedUriPhoto = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedUriPhoto)

            img_circle_image.setImageBitmap(bitmap)
            btn_image.alpha = 0f
        }
    }

    private fun createAccount(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }

        progress(true)

        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    uploadImageToFirebaseStorage()
                }
            }
            .addOnFailureListener {
                Log.w(TAG, "createUserWithEmail:failure", it)
                progress()
                Snackbar.make(edt_name, "Authentication failed.", Snackbar.LENGTH_LONG).show()
            }
        // [END create_user_with_email]
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedUriPhoto == null) return

        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")

        ref.putFile(selectedUriPhoto!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfuly upload image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File location: $it")
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnCanceledListener {
                Log.d(TAG, "Error: ")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user =
            User(uid, edt_name.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase Database")

                val intent = Intent(this, LatestMessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                progress()
                Snackbar.make(edt_name, "User don't save: ${it.message}", Snackbar.LENGTH_LONG).show()
                Log.d(TAG, "User don't save to Firebase Database: ${it.message}")
            }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = edt_email.text.toString()
        if (TextUtils.isEmpty(email) && isEmailValid(email)) {
            edt_email.error = "Required."
            valid = false
        } else {
            edt_email.error = null
        }

        val password = edt_password.text.toString()
        if (TextUtils.isEmpty(password) || password.length < 6 && isPasswordValid(password)) {
            edt_password.error = "Required."
            valid = false
        } else {
            edt_password.error = null
        }

        return valid
    }

    private fun isPasswordValid(password: String): Boolean {

        val pattern: Pattern
        val matcher: Matcher
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(passwordPattern)
        matcher = pattern.matcher(password)

        return matcher.matches()
    }

    private fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)

        return matcher.matches()
    }

    private fun progress(boolean: Boolean = false) {
        when (boolean) {
            true -> {
                scrollView.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            }

            false -> {
                scrollView.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }
}