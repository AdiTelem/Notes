package com.example.notes.view.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.model.NoteAdapter
import com.example.notes.viewmodel.fragments.NotesGalleryViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.notes.model.NoteAdapterCallbacks
import com.example.notes.model.NoteData
import com.example.notes.viewmodel.fragments.NotesEditViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NoteGalleryFragment : Fragment() {
    private val viewModel: NotesGalleryViewModel by viewModels { NotesGalleryViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_note_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.isDeleteDialogShown.value?.let {
            if (it) {
                showDeleteDialog()
            }
        }

        //recycler view
        val recyclerView = view.findViewById<RecyclerView>(R.id.noteList)
        val adapter = NoteAdapter ( object : NoteAdapterCallbacks {
            override val onNoteClick: (NoteData) -> Unit = { noteData ->
                val bundle = Bundle()
                bundle.putInt("note_id", noteData.id)
                findNavController().navigate(R.id.start_to_edit, bundle)
            }
            override val onNoteLongClick: (NoteData) -> Unit = { noteData ->
                viewModel.showDeleteDialog(noteData)
                showDeleteDialog()
            }
        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // buttons
        val refreshButton = view.findViewById<Button>(R.id.refresh_button)
        refreshButton.setOnClickListener {
            viewModel.onRefresh()
        }

        val fAB = view.findViewById<FloatingActionButton>(R.id.fab)
        fAB.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("note_id", 0)
            findNavController().navigate(R.id.start_to_edit, bundle)
        }

        viewModel.notes.observe(viewLifecycleOwner) { noteList ->
            adapter.submitList(noteList)
        }

        viewModel.readAllNotes()
    }

    private fun showDeleteDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setMessage("Are you sure you want to delete this note")
            .setTitle("Delete")
            .setPositiveButton("Yes") { dialog, which ->
                viewModel.deleteSelectedNote()
                viewModel.hideDeleteDialog()
            }
            .setNegativeButton("cancel") { dialog, which ->
                viewModel.hideDeleteDialog()
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
