package com.example.forgetmenot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Splash : AppCompatActivity() {

    var fAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        fAuth = FirebaseAuth.getInstance()
        val handler = Handler()

        handler.postDelayed({
            // check if there's a user logged in
            if (fAuth!!.currentUser != null) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
            } else {
                // if there's no logged in user, create new anonymous account
                fAuth!!.signInAnonymously().addOnSuccessListener {
                    Toast.makeText(
                        this@Splash,
                        "Logged in with a temporary account.",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }.addOnFailureListener { e ->
                    Toast.makeText(this@Splash,
                        "Error! " + e.message,
                        Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }, 2000)
    }
}