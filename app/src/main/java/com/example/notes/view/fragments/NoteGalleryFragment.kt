package com.example.notes.view.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.model.NoteAdapter
import com.example.notes.viewmodel.mvi.NoteGalleryViewModel
import com.example.notes.NotesApplication
import com.example.notes.model.NoteAdapterCallbacks
import com.example.notes.model.NoteData
import io.reactivex.disposables.CompositeDisposable
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

class NoteGalleryFragment : Fragment() {

    @Inject
    lateinit var factory: NoteGalleryViewModel.Factory
    val viewModel: NoteGalleryViewModel by viewModels { factory }

    private val adapter = NoteAdapter( object : NoteAdapterCallbacks {
        override val onNoteClick: (NoteData) -> Unit = { noteData ->
            viewModel.action(NoteGalleryViewModel.Action.ToDetails.Edit(noteData.id))
        }
        override val onNoteLongClick: (NoteData) -> Unit = { noteData ->
                viewModel.action(NoteGalleryViewModel.Action.DeleteNote.Select(noteData))
        }
    })

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_note_gallery, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        this.activity?.let { (it.application as NotesApplication).notesComponent.inject(this) }
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        compositeDisposable.add(
            viewModel.renderableStream
                .subscribe { state ->
                    adapter.submitList(state.data.notes)
                }
        )

        compositeDisposable.add(
            viewModel.eventRelay.
            subscribe { event ->
                when (event) {
                    is NoteGalleryViewModel.Event.ToDetails.New -> {
                        val bundle = Bundle()
                        bundle.putInt("note_id", 0)
                        findNavController().navigate(R.id.start_to_edit, bundle)
                    }
                    is NoteGalleryViewModel.Event.ToDetails.Edit -> {
                        val bundle = Bundle()
                        bundle.putInt("note_id", event.id)
                        findNavController().navigate(R.id.start_to_edit, bundle)
                    }

                    is NoteGalleryViewModel.Event.ShowDialog -> {
                        showDeleteDialog(event.noteData)
                    }
                }
            }
        )

        viewModel.action(NoteGalleryViewModel.Action.SyncList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //recycler view
        val recyclerView = view.findViewById<RecyclerView>(R.id.noteList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // buttons
        val refreshButton = view.findViewById<Button>(R.id.refresh_button)
        refreshButton.setOnClickListener {
            viewModel.action(NoteGalleryViewModel.Action.SyncList)
        }

        val fAB = view.findViewById<FloatingActionButton>(R.id.fab)
        fAB.setOnClickListener {
            viewModel.action(NoteGalleryViewModel.Action.ToDetails.New)
        }
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }

    private fun showDeleteDialog(noteData: NoteData) {
        val action = NoteGalleryFragmentDirections.deleteDialog()
        findNavController().navigate(action)
        setFragmentResultListener("refreshKey") { _, bundle ->
            if (bundle.getBoolean(DeleteDialogFragment.IS_CONFIRMED_KEY)) {
                viewModel.action(NoteGalleryViewModel.Action.DeleteNote.Confirm(noteData.id))
            }
        }
    }
}
