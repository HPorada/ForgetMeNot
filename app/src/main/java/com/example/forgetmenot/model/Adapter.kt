package com.example.forgetmenot.model

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.forgetmenot.note.NoteDetails
import com.example.forgetmenot.R
import java.util.*

class Adapter(var titles: List<String>, var content: List<String>) :
    RecyclerView.Adapter<Adapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.note_view_layout, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.noteTitle.text = titles[position]
        holder.noteContent.text = content[position]

//        val code = randomColor
//        holder.mCardView.setCardBackgroundColor(holder.view.resources.getColor(code, null))
        holder.view.setOnClickListener { v ->
            val i = Intent(v.context, NoteDetails::class.java)
            i.putExtra("title", titles[position])
            i.putExtra("content", content[position])
//            i.putExtra("code", code)
            v.context.startActivity(i)
        }
    }

//    private val randomColor: Int
//        private get() {
//            val colorCode: MutableList<Int> = ArrayList()
//            colorCode.add(R.color.blue)
//            colorCode.add(R.color.yellow)
//            colorCode.add(R.color.skyblue)
//            colorCode.add(R.color.lightPurple)
//            colorCode.add(R.color.lightGreen)
//            colorCode.add(R.color.gray)
//            colorCode.add(R.color.pink)
//            colorCode.add(R.color.red)
//            colorCode.add(R.color.greenlight)
//            colorCode.add(R.color.notgreen)
//            val randomColor = Random()
//            val number = randomColor.nextInt(colorCode.size)
//            return colorCode[number]
//        }

    override fun getItemCount(): Int {
        return titles.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
}