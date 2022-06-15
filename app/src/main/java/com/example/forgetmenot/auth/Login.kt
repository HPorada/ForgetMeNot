package com.example.forgetmenot.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.forgetmenot.MainActivity
import com.example.forgetmenot.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var btnLogin: Button
    lateinit var btnRegister: Button
    var forgetPass: TextView? = null
    var fAuth: FirebaseAuth? = null
    var fStore: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.edtEmail_log)
        password = findViewById(R.id.edtPassword_log)
        btnLogin = findViewById(R.id.btnLogin_log)
        btnRegister = findViewById(R.id.btnRegister_log)
        forgetPass = findViewById(R.id.tvForgot)
        spinner = findViewById(R.id.pbLogin)

        user = FirebaseAuth.getInstance().currentUser
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        showWarning()

        btnLogin.setOnClickListener(View.OnClickListener {
            val mEmail = email.text.toString()
            val mPassword = password.text.toString()

            if (mEmail.isNotEmpty() && mPassword.isNotEmpty()) {
                if (mEmail.matches(Regex("[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                    if (mPassword.length >= 5) {
                        firebaseSignIn()
                    } else {
                        Toast.makeText(
                            this@Login,
                            "Please enter a password with at least 5 characters.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@Login,
                        "Please enter a valid email address.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this@Login, "All fields are required.", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
        })

        btnRegister.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    Register::class.java
                )
            )
        })
    }

    private fun firebaseSignIn() {
        spinner.visibility = View.VISIBLE
        fAuth!!.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnSuccessListener {
                deleteTemporaryData()

                Toast.makeText(this@Login, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }.addOnFailureListener { e ->
                Toast.makeText(this@Login, "Failed login attempt. " + e.message, Toast.LENGTH_SHORT)
                    .show()
                spinner.visibility = View.GONE
            }
    }

    private fun deleteTemporaryData() {
        //delete notes first
        if (fAuth!!.currentUser!!.isAnonymous) {
            val user = fAuth!!.currentUser
            fStore!!.collection("notes").document(user!!.uid).delete().addOnSuccessListener {
                Toast.makeText(
                    this@Login,
                    "Temporary user's notes were deleted.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // delete Temp user
            user.delete().addOnSuccessListener {
                Toast.makeText(
                    this@Login,
                    "Temporary user account was deleted.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun showWarning() {
        val warning = AlertDialog.Builder(this)
            .setTitle("Are you sure?")
            .setMessage("Linking existing account will cause deleting all the temporary user's notes. Create new account to save them.")
            .setPositiveButton(
                "Save notes"
            ) { dialog, which ->
                startActivity(Intent(applicationContext, Register::class.java))
                finish()
            }.setNegativeButton(
                "Delete notes"
            ) { dialog, which ->
                // do nothing
            }
        warning.show()
    }
}