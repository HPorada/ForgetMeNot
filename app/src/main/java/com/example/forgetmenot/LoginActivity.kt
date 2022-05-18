package com.example.forgetmenot

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        fAuth = Firebase.auth

        val currentUser = fAuth.currentUser

        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        username = findViewById<EditText>(R.id.edtEmail_log)
        password = findViewById<EditText>(R.id.edtPassword_log)
    }

    fun onLogClick_log(view: View) {
        validateLoginForm()
    }

    fun onRegClick_log(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun firebaseSignIn() {
        val button = findViewById<Button>(R.id.btn_login_reg)

        button?.isEnabled = false
        button?.alpha = 0.5f

        fAuth.signInWithEmailAndPassword(username.text.toString(), password.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    button?.isEnabled = true
                    button?.alpha = 1.0f
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun validateLoginForm() {
//        val icon: Drawable = resources.getDrawable(R.drawable.warning)
//        val icon = ContextCompat.getDrawable(this, R.drawable.warning)
//
//        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)

        when {
            TextUtils.isEmpty(username.text.toString().trim()) -> {
                username.setError("Please enter an email", null)
            }
            TextUtils.isEmpty(password.text.toString().trim()) -> {
                password.setError("Please enter a password", null)
            }

            username.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() -> {
                if (username.text.toString().matches(Regex("[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                    if (password.text.toString().length >= 5) {
                        firebaseSignIn()
                    } else {
                        password.setError(
                            "Please enter a password with at least 5 characters",
                            null
                        )
                    }
                } else {
                    username.setError("Please enter a valid email address", null)
                }
            }
        }
    }


}