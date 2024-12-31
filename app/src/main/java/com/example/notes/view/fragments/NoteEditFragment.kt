package com.example.notes.view.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.viewmodel.fragments.NotesEditViewModel

class NoteEditFragment : Fragment() {
    private lateinit var viewModel: NotesEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_note_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(
            requireActivity(),
            NotesEditViewModel.Factory
        )[
            NotesEditViewModel::class.java
        ]

        // setup text listeners
        val titleEditText = view.findViewById<EditText>(R.id.titleEditText)
        viewModel.title.observe(viewLifecycleOwner) { newText ->
            if (titleEditText.text.toString() != newText) {
                titleEditText.setText(newText)
            }
        }
        titleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.title.value = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val contentEditText = view.findViewById<EditText>(R.id.contentEditText)
        viewModel.content.observe(viewLifecycleOwner) { newText ->
            if (contentEditText.text.toString() != newText) {
                contentEditText.setText(newText)
            }
        }
        contentEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.content.value = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val backButton = view.findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        val doneButton = view.findViewById<Button>(R.id.doneButton)
        doneButton.setOnClickListener {
            viewModel.onSubmit()
            findNavController().popBackStack()
        }

        //setup edit
        arguments?.getInt("note_id")?.let {
            viewModel.id = it
            viewModel.getNote()
        }
    }
}
