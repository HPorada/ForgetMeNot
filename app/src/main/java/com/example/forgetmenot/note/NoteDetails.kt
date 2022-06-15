package com.example.forgetmenot.note

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.forgetmenot.MainActivity
import com.example.forgetmenot.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NoteDetails : AppCompatActivity() {
    lateinit var data: Intent

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        data = intent
        val content = findViewById<TextView>(R.id.noteDetailsContent)
        val title = findViewById<TextView>(R.id.noteDetailsTitle)
        val date = findViewById<TextView>(R.id.tvDate)

        content.movementMethod = ScrollingMovementMethod()
        content.text = data.getStringExtra("content")
        title.text = data.getStringExtra("title")
        date.text = "Unlocked: " + data.getStringExtra("date")


//        content.setBackgroundColor(resources.getColor(data.getIntExtra("code", 0), null))
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view ->
            val i = Intent(view.context, EditNote::class.java)
            i.putExtra("title", data.getStringExtra("title"))
            i.putExtra("content", data.getStringExtra("content"))
            i.putExtra("date", data.getStringExtra("date"))
            i.putExtra("noteId", data.getStringExtra("noteId"))
            startActivity(i)
        }

        val fStore = FirebaseFirestore.getInstance()
        val fAuth = FirebaseAuth.getInstance()
        val user = fAuth.currentUser

        val fabDelete = findViewById<FloatingActionButton>(R.id.fabDelete)
        fabDelete.setOnClickListener {
            val docref = data.getStringExtra("noteId")?.let {
                fStore.collection("notes").document(
                    user!!.uid
                ).collection("myNotes").document(it)
            }
            docref?.delete()?.addOnSuccessListener {
                Toast.makeText(
                    this@NoteDetails,
                    "Note deleted.",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@NoteDetails, MainActivity::class.java))
            }?.addOnFailureListener {
                Toast.makeText(
                    this@NoteDetails,
                    "Error in deleting the note",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}