package com.example.forgetmenot

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.forgetmenot.auth.Login
import com.example.forgetmenot.auth.Register
import com.example.forgetmenot.model.Note
import com.example.forgetmenot.note.AddNote
import com.example.forgetmenot.note.EditNote
import com.example.forgetmenot.note.NoteDetails
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var drawerLayout: DrawerLayout
    var toggle: ActionBarDrawerToggle? = null
    lateinit var nav_view: NavigationView
    lateinit var noteLists: RecyclerView
    var fStore: FirebaseFirestore? = null
    lateinit var noteAdapter: FirestoreRecyclerAdapter<Note, NoteViewHolder>
    var user: FirebaseUser? = null
    var fAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        fStore = FirebaseFirestore.getInstance()
        fAuth = FirebaseAuth.getInstance()
        user = fAuth!!.currentUser

        // prepare query to fetch all user's notes from the database
        val query = fStore!!.collection("notes").document(
            user!!.uid
        ).collection("myNotes").orderBy("endDate", Query.Direction.DESCENDING)

        // get all user's notes from the database
        val allNotes: FirestoreRecyclerOptions<Note> = FirestoreRecyclerOptions.Builder<Note>()
            .setQuery(query, Note::class.java)
            .build()

        noteAdapter = object : FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {

            @RequiresApi(api = Build.VERSION_CODES.M)
            protected override fun onBindViewHolder(
                noteViewHolder: NoteViewHolder,
                @SuppressLint("RecyclerView") i: Int,
                note: Note
            ) {
                val check = checkDate(note.endDate)

                if (check) {
                    noteViewHolder.noteTitle.text = note.title
                    noteViewHolder.noteContent.text = note.content

                    val lock = noteViewHolder.view.findViewById<ImageView>(R.id.ivLock) as ImageView
                    lock.setImageResource(R.drawable.ic_baseline_lock_open_24)

                } else {
                    noteViewHolder.noteTitle.text = note.title
                    noteViewHolder.noteContent.text = "Memory unlocks on ${note.endDate}"

                    val lock = noteViewHolder.view.findViewById<ImageView>(R.id.ivLock) as ImageView
                    lock.setImageResource(R.drawable.ic_baseline_lock_24)
                }

                val docId: String = noteAdapter.snapshots.getSnapshot(i).id
                noteViewHolder.view.setOnClickListener { v ->

                    if (check) {
                        val i = Intent(v.context, NoteDetails::class.java)
                        i.putExtra("title", note.title)
                        i.putExtra("content", note.content)
                        i.putExtra("date", note.endDate)
                        i.putExtra("noteId", docId)
                        v.context.startActivity(i)
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "The memory will unlock on ${note.endDate}.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                // Menu at every note: edit and delete
                val menuIcon = noteViewHolder.view.findViewById<ImageView>(R.id.menuIcon)
                menuIcon.setOnClickListener { view ->
                    val docId: String = noteAdapter.snapshots.getSnapshot(i).id
                    val menu = PopupMenu(view.context, view)
                    menu.gravity = Gravity.END
                    menu.menu.add("Edit").setOnMenuItemClickListener {
                        val i = Intent(view.context, EditNote::class.java)
                        i.putExtra("title", note.title)
                        i.putExtra("content", note.content)
                        i.putExtra("date", note.endDate)
                        i.putExtra("noteId", docId)
                        startActivity(i)
                        false
                    }

                    menu.menu.add("Delete").setOnMenuItemClickListener {
                        val docref = fStore!!.collection("notes").document(
                            user!!.uid
                        ).collection("myNotes").document(docId)
                        docref.delete().addOnSuccessListener { }.addOnFailureListener {
                            Toast.makeText(
                                this@MainActivity,
                                "Error in deleting the note",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        false
                    }
                    menu.show()
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.note_view_layout, parent, false)
                return NoteViewHolder(view)
            }
        }
        noteLists = findViewById(R.id.noteList)
        drawerLayout = findViewById(R.id.drawer)
        nav_view = findViewById(R.id.nav_view)

        nav_view.setNavigationItemSelectedListener(this)
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle!!)
        toggle!!.isDrawerIndicatorEnabled = true
        toggle!!.syncState()
        noteLists.itemAnimator = null
        noteLists.layoutManager = StaggeredGridLayoutManager(
            2, StaggeredGridLayoutManager.VERTICAL
        )
        noteLists.adapter = noteAdapter

        val headerView = nav_view.getHeaderView(0)

        val username = headerView.findViewById<TextView>(R.id.userDisplayName)
        val userEmail = headerView.findViewById<TextView>(R.id.userDisplayEmail)

        if (user!!.isAnonymous) {
            userEmail.visibility = View.GONE
            username.text = "Temporary User"
        } else {
            userEmail.text = user!!.email
            username.text = user!!.displayName
        }

        val fab = findViewById<FloatingActionButton>(R.id.addNoteFloat)
        fab.setOnClickListener { view -> startActivity(Intent(view.context, AddNote::class.java)) }

        val fabPhoto = findViewById<FloatingActionButton>(R.id.addPhotoFloat)
        fabPhoto.setOnClickListener { view ->
            startActivity(
                Intent(
                    view.context,
                    AddImageActivity::class.java
                )
            )
        }
    }

    private fun checkDate(endDate: String?): Boolean {

        if (endDate != null) {
            val year = Calendar.getInstance().get(Calendar.YEAR)
            var month = Calendar.getInstance().get(Calendar.MONTH)
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

            month += 1

            val curDate = "$year/$month/$day"

            val sdf = SimpleDateFormat("yyyy/MM/dd")

            val cur: Date = sdf.parse(curDate) as Date
            val end: Date = endDate?.let { sdf.parse(it) } as Date

            val cmp = cur.compareTo(end)

            return when {
                cmp > 0 -> {
                    // Cur is after End
                    true
                }
                cmp < 0 -> {
                    // Cur is before End
                    false
                }
                else -> {
                    // Dates are equal
                    true
                }
            }
        } else {
            return true
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)
        when (item.itemId) {
            R.id.notes -> drawerLayout.closeDrawer(GravityCompat.START)

            R.id.addNote -> startActivity(Intent(this, AddNote::class.java))

            R.id.addImage -> startActivity(Intent(this, AddImageActivity::class.java))

            R.id.login -> if (user!!.isAnonymous) {
                startActivity(Intent(this, Login::class.java))
            } else {
                Toast.makeText(this, "You are logged in.", Toast.LENGTH_SHORT).show()
            }

            R.id.register -> if (user!!.isAnonymous) {
                startActivity(Intent(this, Register::class.java))
            } else {
                Toast.makeText(this, "You are registered.", Toast.LENGTH_SHORT).show()
            }

            R.id.logout ->
                checkUser()
            else -> Toast.makeText(this, "Coming soon.", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    private fun checkUser() {
        if (user!!.isAnonymous) {
            displayAlert()
        } else {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, Splash::class.java))
            finish()
        }
    }

    private fun displayAlert() {
        val warning = AlertDialog.Builder(this)
            .setTitle("Are you sure?")
            .setMessage("You are logged in with temporary account. Logging out will result in deleting all notes.")
            .setPositiveButton(
                "Register"
            ) { dialogInterface, i ->
                startActivity(Intent(applicationContext, Register::class.java))
                finish()
            }.setNegativeButton(
                "Logout"
            ) { dialogInterface, i -> //delete user's notes

                //delete user
                user!!.delete().addOnSuccessListener {
                    startActivity(Intent(applicationContext, Splash::class.java))
                    finish()
                }
            }
        warning.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings) {
            Toast.makeText(this, "Settings menu was clicked.", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var noteTitle: TextView
        var noteContent: TextView
        var view: View
        var mCardView: CardView

        init {
            noteTitle = itemView.findViewById(R.id.titles)
            noteContent = itemView.findViewById(R.id.content)
            mCardView = itemView.findViewById(R.id.noteCard)
            view = itemView
        }
    }

    override fun onStart() {
        super.onStart()
        noteAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (noteAdapter != null) {
            noteAdapter.stopListening()
        }
    }
}