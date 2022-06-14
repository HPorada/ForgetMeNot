package com.example.forgetmenot.note

import android.app.AlertDialog.THEME_HOLO_LIGHT
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.forgetmenot.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.HashMap


class AddNote : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    var fStore: FirebaseFirestore? = null
    lateinit var noteTitle: EditText
    lateinit var noteContent: EditText
    lateinit var noteEnd: String
    lateinit var progressBarSave: ProgressBar
    var user: FirebaseUser? = null
    lateinit var btnDate: Button

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

        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        noteEnd = "$day/$month/$year"

        noteTitle.setText(noteEnd)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener(View.OnClickListener {
            val nTitle = noteTitle.text.toString()
            val nContent = noteContent.text.toString()

            if (nTitle.isEmpty() || nContent.isEmpty()) {
                Toast.makeText(
                    this@AddNote,
                    "Can not save note with an empty field.",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            progressBarSave.visibility = View.VISIBLE

            // save note
            val docref = fStore!!.collection("notes").document(
                user!!.uid
            ).collection("myNotes").document()

            val note: MutableMap<String, Any> = HashMap()

            note["title"] = nTitle
            note["content"] = nContent
            note["endDate"] = noteEnd

            docref.set(note).addOnSuccessListener {
                Toast.makeText(this@AddNote, "Note successfully added.", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }.addOnFailureListener {
                Toast.makeText(this@AddNote, "Error, try again.", Toast.LENGTH_SHORT).show()
                progressBarSave.visibility = View.VISIBLE
            }
        })

        btnDate = findViewById<Button>(R.id.btnDate)
        btnDate.setOnClickListener(View.OnClickListener {
            showDatePickerDialog()
        })
    }

    fun showDatePickerDialog() {
        val datePickerDialog: DatePickerDialog = DatePickerDialog(
            this@AddNote,
            this@AddNote,
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.close_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.close) {
            Toast.makeText(this, "Note not saved.", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        val mon = month + 1

        noteEnd = "$day/$mon/$year"
        btnDate.text = noteEnd
    }
}