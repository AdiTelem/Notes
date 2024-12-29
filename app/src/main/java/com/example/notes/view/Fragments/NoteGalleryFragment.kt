package com.example.notes.view.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.notes.R
import com.example.notes.model.NoteAdapter
import com.example.notes.model.NoteData
import com.example.notes.viewmodel.Fragments.NotesGalleryViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NoteGalleryFragment : Fragment() {
    private lateinit var viewModel: NotesGalleryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_note_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(
            requireActivity(),
            NotesGalleryViewModel.Factory
        )[
            NotesGalleryViewModel::class.java
        ]

        //recycler view
        val recyclerView = view.findViewById<RecyclerView>(R.id.noteList)
        val adapter = NoteAdapter (
            onNoteClick = { selectedNote ->

                val noteEditFragment = NoteEditFragment()

                val bundle = Bundle()
                bundle.putInt("note_id", selectedNote.id)
                noteEditFragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .add(R.id.mainContainer, noteEditFragment)
                    .hide(this)
                    .addToBackStack(null)
                    .commit()
            },
            onNoteLongClick = { selectedNote ->
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder
                    .setMessage("Are you sure you want to delete ${selectedNote.title}")
                    .setTitle("Delete")
                    .setPositiveButton("Yes") { dialog, which ->
                        viewModel.deleteNote(selectedNote.id)
                    }
                    .setNegativeButton("cancel") { dialog, which ->}
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // buttons
        val refreshButton = view.findViewById<Button>(R.id.refresh_button)
        refreshButton.setOnClickListener {
            viewModel.onRefresh()
        }

        val newNoteButton = view.findViewById<Button>(R.id.newNote)
        newNoteButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .add(R.id.mainContainer, NoteEditFragment())
                .hide(this)
                .addToBackStack(null)
                .commit()
        }

        viewModel.notes.observe(viewLifecycleOwner) { noteList ->
            adapter.submitList(noteList)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            Log.d("debug update note", "gallery view unhidden")
            viewModel.readAllNotes()
        }
    }
}