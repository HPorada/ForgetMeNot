package com.example.forgetmenot.note

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.forgetmenot.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AddNote : AppCompatActivity() {
    var fStore: FirebaseFirestore? = null
    lateinit var noteTitle: EditText
    lateinit var noteContent: EditText
    lateinit var progressBarSave: ProgressBar
    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        fStore = FirebaseFirestore.getInstance()
        noteContent = findViewById(R.id.addNoteContent)
        noteTitle = findViewById(R.id.addNoteTitle)
        progressBarSave = findViewById(R.id.progressBar)
        user = FirebaseAuth.getInstance().currentUser
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener(View.OnClickListener {
            val nTitle = noteTitle.getText().toString()
            val nContent = noteContent.getText().toString()
            if (nTitle.isEmpty() || nContent.isEmpty()) {
                Toast.makeText(
                    this@AddNote,
                    "Can not Save note with Empty Field.",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            progressBarSave.setVisibility(View.VISIBLE)

            // save note
            val docref = fStore!!.collection("notes").document(
                user!!.uid
            ).collection("myNotes").document()
            val note: MutableMap<String, Any> = HashMap()
            note["title"] = nTitle
            note["content"] = nContent
            docref.set(note).addOnSuccessListener {
                Toast.makeText(this@AddNote, "Note Added.", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }.addOnFailureListener {
                Toast.makeText(this@AddNote, "Error, Try again.", Toast.LENGTH_SHORT).show()
                progressBarSave.setVisibility(View.VISIBLE)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.close_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.close) {
            Toast.makeText(this, "Not Saved.", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}