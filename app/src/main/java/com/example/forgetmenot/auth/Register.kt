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

//        getSupportActionBar().setTitle("Connect to FireNotes");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            if (uUserEmail.isEmpty() || uUsername.isEmpty() || uUserPass.isEmpty() || uConfPass.isEmpty()) {
                Toast.makeText(this@Register, "All fields are required.", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (uUserPass != uConfPass) {
                confirmPass.error = "Passwords do not match."
            }

            spinner.setVisibility(View.VISIBLE)

            val credential = EmailAuthProvider.getCredential(uUserEmail, uUserPass)
            fAuth!!.currentUser!!.linkWithCredential(credential).addOnSuccessListener {
                Toast.makeText(this@Register, "Notes are Synced.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                val usr = fAuth!!.currentUser
                val request = UserProfileChangeRequest.Builder()
                    .setDisplayName(uUsername)
                    .build()
                usr!!.updateProfile(request)
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }.addOnFailureListener {
                Toast.makeText(
                    this@Register,
                    "Failed to Connect. Try Again.",
                    Toast.LENGTH_SHORT
                ).show()
                spinner.setVisibility(View.VISIBLE)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        return super.onOptionsItemSelected(item)
    }
}