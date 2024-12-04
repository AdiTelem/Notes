package com.example.notes

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.notes.ui.theme.NotesTheme
import com.example.notes.view.NotesAppNavigation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesTheme {
                NotesAppNavigation()
            }
        }
        Log.v("", "Create")
    }

    override fun onStart() {
        super.onStart()

        Log.v("", "Start")

    }

    override fun onResume() {
        super.onResume()

        Log.v("", "Resume")
    }

    override fun onPause() {
        super.onPause()

        Log.v("", "Pause")
    }

    override fun onStop() {
        super.onStop()

        Log.v("", "Stop")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.v("", "Destroy")
    }
}
