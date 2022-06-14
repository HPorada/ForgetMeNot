package com.example.forgetmenot

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var cnfPassword: EditText
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        username = findViewById<EditText>(R.id.edtEmail_reg)
        password = findViewById<EditText>(R.id.edtPassword_reg)
        cnfPassword = findViewById<EditText>(R.id.edtCnfPassword_reg)
        fAuth = Firebase.auth
    }

    fun onLogClick_reg(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun onRegClick_reg(view: View) {
        validateRegisterForm()
    }

    private fun firebaseSignUp() {
        val button = findViewById<Button>(R.id.btnRegister_reg)

        button?.isEnabled = false
        button?.alpha = 0.5f

        fAuth.createUserWithEmailAndPassword(username.text.toString(), password.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT)
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

    private fun validateRegisterForm() {
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
            TextUtils.isEmpty(cnfPassword.text.toString().trim()) -> {
                cnfPassword.setError("Please enter the password again", null)
            }

            username.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() &&
                    cnfPassword.text.toString().isNotEmpty() -> {
                if (username.text.toString().matches(Regex("[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                    if (password.text.toString().length >= 5) {
                        if (password.text.toString() == cnfPassword.text.toString()) {
                            firebaseSignUp()
                        } else {
                            cnfPassword.setError("Passwords didn't match", null)
                        }
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