package com.example.notes.view.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.notes.NotesApplication
import com.example.notes.R
import com.example.notes.viewmodel.mvi.NoteEditViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class NoteEditFragment : Fragment() {

    @Inject
    lateinit var factory: NoteEditViewModel.Factory
    private val viewModel: NoteEditViewModel by viewModels { factory }

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        this.activity?.let { (it.application as NotesApplication).notesComponent.inject(this) }
        viewModel.action(NoteEditViewModel.Action.Setup.FetchNoteByID(arguments?.getInt("note_id") ?: 0))
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_note_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val titleEditText = view.findViewById<EditText>(R.id.titleEditText)
        val contentEditText = view.findViewById<EditText>(R.id.contentEditText)

        compositeDisposable.add(
            viewModel.renderableStream
                .subscribe { state ->
                    if (titleEditText.text.toString() != state.data.noteData.title) {
                        titleEditText.setText(state.data.noteData.title)
                    }

                    if (contentEditText.text.toString() != state.data.noteData.content) {
                        contentEditText.setText(state.data.noteData.content)
                    }
                }
        )

        compositeDisposable.add(
            viewModel.eventRelay.
            subscribe { event ->
                when (event) {
                    is NoteEditViewModel.Event.ToGallery -> {
                        findNavController().popBackStack()
                    }
                }
            }
        )

        titleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.action(NoteEditViewModel.Action.TextChanged.Title(s.toString()))
            }
        })

        contentEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.action(NoteEditViewModel.Action.TextChanged.Content(s.toString()))
            }
        })

        val backButton = view.findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            viewModel.action(NoteEditViewModel.Action.Back)
        }

        val doneButton = view.findViewById<Button>(R.id.doneButton)
        doneButton.setOnClickListener {
            viewModel.action(NoteEditViewModel.Action.Submission.DoneClicked)
        }
    }
}
