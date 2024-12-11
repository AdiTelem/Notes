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
import com.example.notes.model.NoteRepository
import com.example.notes.model.NoteRepositoryWithService
import com.example.notes.ui.theme.NotesTheme
import com.example.notes.view.NotesAppNavigation

class MainActivity : ComponentActivity() {

    private var service: WebService? = null
    private var repository = mutableStateOf<NoteRepository>(NoteRepositoryWithService(null))

    private var bound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            val localBinder = binder as WebService.LocalBinder
            service = localBinder.getService()
            repository.value = NoteRepositoryWithService(service)
            bound = true
            if (service == null) {
                Log.d("service_debug", "service bind failed")
            } else {
                Log.d("service_debug", "service bind connected")
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
                NotesAppNavigation(repository.value)
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
