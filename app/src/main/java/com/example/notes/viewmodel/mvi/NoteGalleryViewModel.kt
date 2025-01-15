package com.example.notes.viewmodel.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notes.model.NoteData
import com.example.notes.model.repository.rxjava.NoteRepositoryRXJ
import com.plangrid.android.mvi.MobiusViewModel
import com.plangrid.android.mvi.Next
import com.plangrid.android.mvi.NextUpdater
import com.plangrid.android.mvi.Processor
import io.reactivex.Observable
import io.reactivex.Scheduler
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class NoteGalleryViewModel constructor(
    uiScheduler: Scheduler,
    effectsScheduler: Scheduler,
    processor: NoteGalleryProcessor,
) : MobiusViewModel<
        NoteGalleryViewModel.Action,
        NoteGalleryViewModel.Effect,
        NoteGalleryViewModel.State,
        NoteGalleryViewModel.State,
        NoteGalleryViewModel.Event
        > (
    initialState = State.emptyState(),
    updater = NoteGalleryUpdater,
    initialEffects = setOf(Effect.FetchAllNotes),
    uiScheduler = uiScheduler,
    effectsScheduler = effectsScheduler,
    processor = processor
) {

    class Factory @Inject constructor(
        private val uiScheduler: Scheduler,
        private val effectsScheduler: Scheduler,
        private val processorFactory: NoteGalleryProcessor.Factory,
        private val repositoryRXJ: NoteRepositoryRXJ
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            NoteGalleryViewModel.repositoryRXJ = repositoryRXJ
            return NoteGalleryViewModel(
                uiScheduler = uiScheduler,
                effectsScheduler = effectsScheduler,
                processor = processorFactory.create()
            ) as T
        }
    }

    object NoteGalleryUpdater : NextUpdater<State, Action, Effect, Event> {

        private fun Next.Builder<State, Effect, Event>.deletions(action: Action.DeleteNote) : State {
            return when (action) {
                is Action.DeleteNote.Select -> {
                    addEvent(Event.ShowDialog(action.note))
                    currentState
                }

                is Action.DeleteNote.Confirm -> {
                    currentState.notes.forEach { note ->
                        if (note.id == action.noteId) {
                            addEffect(Effect.DeleteNote(note))
                        }
                    }
                    currentState
                }

                is Action.DeleteNote.Dismiss -> {
                    currentState
                }

                is Action.DeleteNote.Deleted -> {
                    currentState.copy(
                        notes = currentState.notes.filter { it.id != action.note.id }
                    )
                }
            }
        }

        private fun Next.Builder<State, Effect, Event>.toDetails(action: Action.ToDetails) : State {
            when (action) {
                is Action.ToDetails.New -> {
                    addEvent(Event.ToDetails.New)
                }
                is Action.ToDetails.Edit -> {
                    addEvent(Event.ToDetails.Edit(action.noteId))
                }
            }

            return currentState
        }

        override fun Next.Builder<State, Effect, Event>.next(action: Action): State {
            return when (action) {
                is Action.SyncList -> {
                    addEffect(Effect.FetchAllNotes)
                    currentState
                }
                is Action.UpdateNewList -> {
                    currentState.copy(
                        notes = action.noteList
                    )
                }

                is Action.ToDetails -> {
                    toDetails(action)
                }

                is Action.DeleteNote -> {
                    deletions(action)
                }
            }
        }
    }

    class NoteGalleryProcessor : Processor<Effect, Action> {
        class Factory @Inject constructor() {
            fun create(): NoteGalleryProcessor {
                return NoteGalleryProcessor()
            }
        }

        private val fetchAllData: (Observable<Effect>) -> Observable<Action> = { effects ->
                effects.ofType(Effect.FetchAllNotes::class.java)
                    .map {
                        val list = repositoryRXJ.readAllNote()
                            .blockingFirst()

                        Action.UpdateNewList(list)
                    }
            }

        private val deleteNote: (Observable<Effect>) -> Observable<Action> = { effects ->
                effects.ofType(Effect.DeleteNote::class.java)
                    .map { effect ->
                        repositoryRXJ.removeNote(effect.note)
                            .blockingAwait()

                        Action.DeleteNote.Deleted(effect.note)
                    }
            }

        private val effectToActionTransformers: List<(Observable<Effect>) -> Observable<Action>> =
            listOf(
                fetchAllData,
                deleteNote
            )

        override fun invoke(effectsOnBgThread: Observable<Effect>): Observable<out Action> {
            return effectsOnBgThread.publish { effects ->
                Observable.merge(effectToActionTransformers.map { effects.compose(it) })
            }
        }
    }


    sealed class Effect {
        data object FetchAllNotes : Effect()
        data class DeleteNote(val note: NoteData) : Effect()
    }

    sealed class Action {
        sealed class ToDetails : Action() {
            data class Edit(val noteId: Int) : ToDetails()
            data object New : ToDetails()
        }
        sealed class DeleteNote : Action() {
            data class Select(val note: NoteData) : DeleteNote()
            data class Confirm(val noteId: Int) : DeleteNote()
            data object Dismiss : DeleteNote()
            data class Deleted(val note: NoteData) : DeleteNote()
        }
        data object SyncList : Action()
        data class UpdateNewList(val noteList: List<NoteData>) : Action()
    }

    sealed class Event {
        sealed class ToDetails : Event() {
            data class Edit(val id: Int) : ToDetails()
            data object New : ToDetails()
        }
        data class ShowDialog(val noteData: NoteData) : Event()
    }

    data class State(
        val notes: List<NoteData>,
    ) {
        companion object {
            fun emptyState() = State(
                notes = mutableListOf()
            )
        }
    }

    companion object {
        lateinit var repositoryRXJ: NoteRepositoryRXJ
    }
}