package com.example.notes.model

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R

class NoteAdapter(
    private val onNoteClick: (NoteData) -> Unit,
    private val onNoteLongClick: (NoteData) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private val notes = mutableListOf<NoteData>()

    //View Holder
    class NoteViewHolder(
        noteView: View,
        private val onNoteClick: (NoteData) -> Unit,
        private val onNoteLongClick: (NoteData) -> Unit
    ): RecyclerView.ViewHolder(noteView) {

        private val titleText = itemView.findViewById<TextView>(R.id.title)
        private val contentText = itemView.findViewById<TextView>(R.id.content)

        fun bind(note: NoteData) {
            titleText.text = note.title
            contentText.text = note.content
            itemView.setOnClickListener { onNoteClick(note) }
            itemView.setOnLongClickListener { onNoteLongClick(note); true }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_entry, parent, false)
        return NoteViewHolder(view, onNoteClick, onNoteLongClick)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)
    }

    override fun getItemCount(): Int = notes.size

    // Method to update the data in the adapter
    fun submitList(newItems: List<NoteData>) {
        notes.clear()
        notes.addAll(newItems)
        notifyDataSetChanged()
    }
}