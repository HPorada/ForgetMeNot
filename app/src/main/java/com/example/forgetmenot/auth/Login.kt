package com.example.forgetmenot.auth

import android.content.Intent
import android.os.Bundle
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

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Login to FireNotes");
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
            if (mEmail.isEmpty() || mPassword.isEmpty()) {
                Toast.makeText(this@Login, "All fields are required.", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            // delete notes first
            spinner.setVisibility(View.VISIBLE)
            if (fAuth!!.currentUser!!.isAnonymous) {
                val user = fAuth!!.currentUser
                fStore!!.collection("notes").document(user!!.uid).delete().addOnSuccessListener {
                    Toast.makeText(
                        this@Login,
                        "All Temp Notes are Deleted.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // delete Temp user
                user.delete().addOnSuccessListener {
                    Toast.makeText(
                        this@Login,
                        "Temp user Deleted.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            fAuth!!.signInWithEmailAndPassword(mEmail, mPassword).addOnSuccessListener {
                Toast.makeText(this@Login, "Success !", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }.addOnFailureListener { e ->
                Toast.makeText(this@Login, "Login Failed. " + e.message, Toast.LENGTH_SHORT)
                    .show()
                spinner.setVisibility(View.GONE)
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

    private fun showWarning() {
        val warning = AlertDialog.Builder(this)
            .setTitle("Are you sure?")
            .setMessage("Linking existing account Will delete all the temp notes. Create New Account To Save them.")
            .setPositiveButton(
                "Save notes"
            ) { dialog, which ->
                startActivity(Intent(applicationContext, Register::class.java))
                finish()
            }.setNegativeButton(
                "It's Ok"
            ) { dialog, which ->
                // do nothing
            }
        warning.show()
    }
}