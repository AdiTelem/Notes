package com.example.notes

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notes.model.NoteRepository
import com.example.notes.model.NoteRepositoryWithService
import com.example.notes.ui.theme.NotesTheme
import com.example.notes.view.NotesAppNavigation
import com.example.notes.viewmodel.NavigationViewModel

class MainActivity : ComponentActivity() {

    private var service: WebService? = null
    private var bound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            val localBinder = binder as WebService.LocalBinder
            service = localBinder.getService()
            service?.let {
                nonNullService ->
                bound = true
                val notesApplication = application as NotesApplication
                val serviceRepository = notesApplication.container.noteRepository as NoteRepositoryWithService
                serviceRepository.setService(nonNullService)
                Log.d("service_debug", "service bind success")
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
            Log.d("service_debug", "service disconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            NotesTheme {
                var navigationViewModel: NavigationViewModel = viewModel(factory = NavigationViewModel.Factory)
                NotesAppNavigation(navigationViewModel)
            }
        }
        Log.d("", "Create")
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(this, WebService::class.java)
        val success = bindService(intent, serviceConnection, BIND_AUTO_CREATE)

        Log.v("", "Start")
        Log.d("service_debug", "start = $success")

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

        if (bound) {
            unbindService(serviceConnection)
            bound = false
            Log.d("service_debug", "service unbound")
        }

        Log.v("", "Stop")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.v("", "Destroy")
    }
}
