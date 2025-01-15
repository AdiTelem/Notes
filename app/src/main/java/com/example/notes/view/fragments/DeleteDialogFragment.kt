package com.example.notes.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.notes.NotesApplication

class DeleteDialogFragment : DialogFragment() {

    companion object {
        const val IS_CONFIRMED_KEY = "IS_CONFIRMED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        this.activity?.let { (it.application as NotesApplication).notesComponent.inject(this) }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete this note")
            .setPositiveButton("Yes") { _, _ ->
                setFragmentResult("refreshKey", bundleOf(IS_CONFIRMED_KEY to true))
                findNavController().popBackStack()
            }
            .setNegativeButton("cancel") { _, _ ->
                setFragmentResult("refreshKey", bundleOf(IS_CONFIRMED_KEY to false))
                findNavController().popBackStack()
            }
            .create()
    }
}