package com.example.forgetmenot.note

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.forgetmenot.MainActivity
import com.example.forgetmenot.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.common.primitives.UnsignedBytes.toInt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.HashMap

class EditNote : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    lateinit var data: Intent
    lateinit var editNoteTitle: EditText
    lateinit var editNoteContent: EditText
    lateinit var noteEnd: String
    var fStore: FirebaseFirestore? = null
    lateinit var spinner: ProgressBar
    var user: FirebaseUser? = null
    lateinit var btnDate: Button

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
        btnDate = findViewById(R.id.btnDateEdit)

        val noteTitle = data.getStringExtra("title")
        val noteContent = data.getStringExtra("content")
        val noteEndDate = data.getStringExtra("date")

        editNoteTitle.setText(noteTitle)
        editNoteContent.setText(noteContent)
        btnDate.text = noteEndDate

        if (noteEndDate != null) {
            noteEnd = noteEndDate
        } else {
            val year = Calendar.getInstance().get(Calendar.YEAR)
            val month = Calendar.getInstance().get(Calendar.MONTH)
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            noteEnd = "$day/$month/$year"
        }

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
            note["endDate"] = noteEnd

            docref.update(note).addOnSuccessListener {
                Toast.makeText(this@EditNote, "Note Saved.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }.addOnFailureListener {
                Toast.makeText(this@EditNote, "Error, try again.", Toast.LENGTH_SHORT).show()
                spinner.visibility = View.VISIBLE
            }
        })

        val list = noteEnd.split("/")

        btnDate = findViewById<Button>(R.id.btnDateEdit)
        btnDate.setOnClickListener(View.OnClickListener {
            showDatePickerDialog(list)
        })
    }

    fun showDatePickerDialog(list: List<String>) {
        val datePickerDialog: DatePickerDialog = DatePickerDialog(
            this@EditNote,
            this@EditNote,
            Integer.parseInt(list[2]),
            Integer.parseInt(list[1]),
            Integer.parseInt(list[0])
        )
        datePickerDialog.show()
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        val mon = month + 1

        noteEnd = "$day/$mon/$year"
        btnDate.text = noteEnd
    }
}