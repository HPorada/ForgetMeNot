package com.example.forgetmenot.note

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.forgetmenot.MainActivity
import com.example.forgetmenot.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class EditNote : AppCompatActivity() {
    lateinit var data: Intent
    lateinit var editNoteTitle: EditText
    lateinit var editNoteContent: EditText
    var fStore: FirebaseFirestore? = null
    lateinit var spinner: ProgressBar
    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        fStore = FirebaseFirestore.getInstance()
        spinner = findViewById(R.id.progressBar2)
        user = FirebaseAuth.getInstance().currentUser
        data = intent
        editNoteContent = findViewById(R.id.editNoteContent)
        editNoteTitle = findViewById(R.id.editNoteTitle)

        val noteTitle = data.getStringExtra("title")
        val noteContent = data.getStringExtra("content")

        editNoteTitle.setText(noteTitle)
        editNoteContent.setText(noteContent)

        val fab = findViewById<FloatingActionButton>(R.id.saveEditedNote)
        fab.setOnClickListener(View.OnClickListener {
            val nTitle = editNoteTitle.text.toString()
            val nContent = editNoteContent.text.toString()

            if (nTitle.isEmpty() || nContent.isEmpty()) {
                Toast.makeText(
                    this@EditNote,
                    "Can not save a note with an empty field.",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            spinner.visibility = View.VISIBLE

            // save note
            val docref = fStore!!.collection("notes").document(
                user!!.uid
            ).collection("myNotes").document(data.getStringExtra("noteId")!!)

            val note: MutableMap<String, Any> = HashMap()

            note["title"] = nTitle
            note["content"] = nContent

            docref.update(note).addOnSuccessListener {
                Toast.makeText(this@EditNote, "Note Saved.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }.addOnFailureListener {
                Toast.makeText(this@EditNote, "Error, Try again.", Toast.LENGTH_SHORT).show()
                spinner.visibility = View.VISIBLE
            }
        })
    }
}