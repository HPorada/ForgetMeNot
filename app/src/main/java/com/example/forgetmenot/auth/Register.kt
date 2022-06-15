package com.example.forgetmenot.auth

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.forgetmenot.MainActivity
import com.example.forgetmenot.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class Register : AppCompatActivity() {
    lateinit var username: EditText
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var confirmPass: EditText
    lateinit var btnRegister: Button
    lateinit var btnLogin: Button
    lateinit var spinner: ProgressBar
    var fAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        username = findViewById(R.id.edtName_reg)
        email = findViewById(R.id.edtEmail_reg)
        password = findViewById(R.id.edtPassword_reg)
        confirmPass = findViewById(R.id.edtCnfPassword_reg)
        btnRegister = findViewById(R.id.btnRegister_reg)
        btnLogin = findViewById(R.id.btnLogin_reg)
        spinner = findViewById(R.id.pbRegister)
        fAuth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    Login::class.java
                )
            )
        })

        btnRegister.setOnClickListener(View.OnClickListener {
            val uUsername = username.text.toString()
            val uUserEmail = email.text.toString()
            val uUserPass = password.text.toString()
            val uConfPass = confirmPass.text.toString()

            if (uUsername.isNotEmpty() && uUserEmail.isNotEmpty() && uUserPass.isNotEmpty() && uConfPass.isNotEmpty()) {
                if (uUserEmail.matches(Regex("[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                    if (uUserPass.length >= 5) {
                        if (uUserPass == uConfPass) {
                            firebaseSignUp()
                        } else {
                            confirmPass.error = "Passwords do not match."
                            Toast.makeText(
                                this@Register,
                                "Passwords didn't match.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@Register,
                            "Please enter a password with at least 5 characters.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@Register,
                        "Please enter a valid email address.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this@Register, "All fields are required.", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
        })
    }

    private fun firebaseSignUp() {
        spinner.visibility = View.VISIBLE

        val credential =
            EmailAuthProvider.getCredential(email.text.toString(), password.text.toString())

        fAuth!!.currentUser!!.linkWithCredential(credential).addOnSuccessListener {
            Toast.makeText(this@Register, "Notes are synced.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(applicationContext, MainActivity::class.java))

            val usr = fAuth!!.currentUser
            val request = UserProfileChangeRequest.Builder()
                .setDisplayName(username.text.toString())
                .build()
            usr!!.updateProfile(request)
            startActivity(Intent(applicationContext, MainActivity::class.java))

        }.addOnFailureListener { e ->
            Toast.makeText(
                this@Register,
                "Failed to register. " + e.message,
                Toast.LENGTH_SHORT
            ).show()
            spinner.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        return super.onOptionsItemSelected(item)
    }
}