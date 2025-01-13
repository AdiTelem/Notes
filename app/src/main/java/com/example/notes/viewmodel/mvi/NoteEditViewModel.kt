package com.example.notes.viewmodel.mvi

import android.app.Application
import android.util.Log
import com.plangrid.android.mvi.MobiusViewModel
import com.plangrid.android.mvi.Next
import com.plangrid.android.mvi.NextUpdater
import com.plangrid.android.mvi.Processor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notes.NotesApplication
import com.example.notes.model.NoteData
import com.example.notes.model.SharedPref
import com.example.notes.model.repository.rxjava.NoteRepositoryRXJ
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
class NoteEditViewModel constructor(
    uiScheduler: Scheduler,
    effectsScheduler: Scheduler,
    processor: NoteEditProcessor,
) : MobiusViewModel<
        NoteEditViewModel.Action,
        NoteEditViewModel.Effect,
        NoteEditViewModel.State,
        NoteEditViewModel.State,
        NoteEditViewModel.Event
        > (
    initialState = State.emptyState(),
    updater = Updater,
    initialEffects = emptySet(),
    uiScheduler = uiScheduler,
    effectsScheduler = effectsScheduler,
    processor = processor
) {

    class Factory @Inject constructor(
        private val uiScheduler: Scheduler,
        private val effectsScheduler: Scheduler,
        private val processorFactory: NoteEditProcessor.Factory,
        private val repositoryRXJ: NoteRepositoryRXJ,
        private val application: Application
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            NoteEditViewModel.application = application as NotesApplication
            NoteEditViewModel.repositoryRXJ = repositoryRXJ
            return NoteEditViewModel(
                uiScheduler = uiScheduler,
                effectsScheduler = effectsScheduler,
                processor = processorFactory.create(),
            ) as T
        }
    }

    object Updater : NextUpdater<State, Action, Effect, Event> {
        private fun Next.Builder<State, Effect, Event>.textChanged(action: Action.TextChanged): State {
            return when (action) {
                is Action.TextChanged.Title -> {
                    currentState.copy(
                        noteData = currentState.noteData.copy(
                            title = action.newString.trim().take(TITLE_LIMIT)
                        )
                    )
                }

                is Action.TextChanged.Content -> {
                    currentState.copy(
                        noteData = currentState.noteData.copy(
                            content = action.newString.trim().take(CONTENT_LIMIT)
                        )
                    )
                }
            }
        }

        private fun Next.Builder<State, Effect, Event>.submission(action: Action.Submission): State {
            return when (action) {
                is Action.Submission.DoneClicked -> {
                    addEffect(Effect.SaveNote(currentState.noteData))
                    currentState
                }

                is Action.Submission.NoteSubmitted -> {
                    addEvent(Event.ToGallery)
                    currentState
                }
            }
        }

        private fun Next.Builder<State, Effect, Event>.setup(action: Action.Setup): State {
            return when (action) {
                is Action.Setup.FetchNoteByID -> {
                    addEffect(Effect.FetchNoteByID(action.id))
                    currentState
                }

                is Action.Setup.NoteFetched -> {
                    currentState.copy(
                        noteData = action.noteData
                    )
                }
            }
        }

        override fun Next.Builder<State, Effect, Event>.next(action: Action): State {
            return when (action) {
                is Action.TextChanged -> {
                    textChanged(action)
                }

                is Action.Submission -> {
                    submission(action)
                }

                is Action.Back -> {
                    addEvent(Event.ToGallery)
                    currentState
                }

                is Action.Setup -> {
                    setup(action)
                }
            }
        }
    }

    class NoteEditProcessor : Processor<Effect, Action> {
        class Factory @Inject constructor() {
            fun create(): NoteEditProcessor {
                return NoteEditProcessor()
            }
        }

        private val fetchNoteByID: (Observable<Effect>) -> Observable<Action> = { effects ->
            effects.ofType(Effect.FetchNoteByID::class.java).map { effect ->
                val note = repositoryRXJ.readOneNote(effect.id).blockingGet() ?: NoteData.EmptyNote()

                Action.Setup.NoteFetched(note)
            }
        }

        private val saveNote: (Observable<Effect>) -> Observable<Action> = { effects ->
            effects.ofType(Effect.SaveNote::class.java).map { effect ->
                if (effect.noteData.title.isNotEmpty()) {
                    if (effect.noteData.id == 0) {
                        createNote(effect.noteData)
                    } else {
                        updateNote(effect.noteData)
                    }
                }

                Action.Submission.NoteSubmitted
            }
        }

        private fun createNote(noteData: NoteData) {
            val context = application.applicationContext

            val newCounter = SharedPref.getNoteCounter(context) + 1
            noteData.id = newCounter
            noteData.updateTime()

            repositoryRXJ.insertNote(noteData).blockingAwait()

            SharedPref.setNoteCounter(context, newCounter)
        }

        private fun updateNote(noteData: NoteData) {
            repositoryRXJ.updateNote(noteData).blockingAwait()
        }

        private val effectToActionTransformers: List<(Observable<Effect>) -> Observable<Action>> =
            listOf(
                saveNote,
                fetchNoteByID
            )

        override fun invoke(effectsOnBgThread: Observable<Effect>): Observable<out Action> {
            return effectsOnBgThread.publish { effects ->
                Observable.merge(effectToActionTransformers.map { effects.compose(it) })
            }
        }
    }

    sealed class Effect {
        data class SaveNote(val noteData: NoteData) : Effect()
        data class FetchNoteByID(val id: Int) : Effect()
    }

    sealed class Action {

        sealed class TextChanged : Action() {
            data class Title (val newString: String) : TextChanged()
            data class Content (val newString: String) : TextChanged()
        }

        sealed class Submission : Action() {
            data object DoneClicked : Submission()
            data object NoteSubmitted : Submission()
        }

        data object Back : Action()

        sealed class Setup : Action() {
            data class FetchNoteByID(val id: Int) : Setup()
            data class NoteFetched(val noteData: NoteData) : Setup()
        }
    }

    sealed class Event {
        data object ToGallery : Event()
    }

    data class State(
        val noteData: NoteData
    ) {
        companion object {
            fun emptyState() = State(
                noteData = NoteData.EmptyNote()
            )
        }
    }

    companion object {
        lateinit var application: NotesApplication
        lateinit var repositoryRXJ: NoteRepositoryRXJ
        const val TITLE_LIMIT: Int = 20
        const val CONTENT_LIMIT: Int = 500
    }
}
