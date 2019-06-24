package com.danielvilha.kotlinmessengerandroid

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

import kotlinx.android.synthetic.main.activity_create_account.*
import java.util.*

class CreateAccountActivity : AppCompatActivity() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    private var selectedUriPhoto: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        btn_register.setOnClickListener {
            val name = edt_name.text.toString()
            val email = edt_email.text.toString()
            val password = edt_password.text.toString()

            createAccount(name, email, password)
        }

        btn_image.setOnClickListener {
            // Image picture
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        tvw_alredy_have_account.setOnClickListener {
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

    private fun createAccount(name: String, email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }

        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser

                    updateUI(user)
                }
            }
            .addOnFailureListener {
                Log.w(TAG, "createUserWithEmail:failure", it)
                Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
        // [END create_user_with_email]
    }

    private fun updateUI(user: FirebaseUser?) {

        uploadImageToFirebaseStorage()
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedUriPhoto == null) return

        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")

        ref.putFile(selectedUriPhoto!!)
            .addOnSuccessListener {
                Log.d("CreateAccountActivity", "Successfuly upload image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("CreateAccountActivity", "File location: $it")
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, edt_name.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("CreateAccountActivity", "Finally we saved the user to Firebase Database")
            }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = edt_email.text.toString()
        if (TextUtils.isEmpty(email)) {
            edt_email.error = "Required."
            valid = false
        } else {
            edt_email.error = null
        }

        val password = edt_password.text.toString()
        if (TextUtils.isEmpty(password)) {
            edt_password.error = "Required."
            valid = false
        } else {
            edt_password.error = null
        }

        return valid
    }

    companion object {
        private const val TAG = "CreateAccountActivity"
    }
}

class User(val uid: String, val username: String, val profileImageUrl: String)
