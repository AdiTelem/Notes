package com.example.notes.viewmodel.mvi

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notes.model.NoteData
import com.example.notes.model.repository.rxjava.NoteRepositoryRXJ
import com.plangrid.android.mvi.MobiusTemplate
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
        NoteGalleryViewModel.NoteGalleryAction,
        NoteGalleryViewModel.NoteGalleryEffect,
        NoteGalleryViewModel.NoteGalleryState,
        NoteGalleryViewModel.NoteGalleryState,
        NoteGalleryViewModel.NoteGalleryEvent
        > (
    initialState = NoteGalleryState.emptyState(),
    updater = NoteGalleryUpdater,
    initialEffects = setOf(NoteGalleryEffect.FetchAllNotes),
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

    object NoteGalleryUpdater : NextUpdater<NoteGalleryState, NoteGalleryAction, NoteGalleryEffect, NoteGalleryEvent> {
        override fun Next.Builder<NoteGalleryState, NoteGalleryEffect, NoteGalleryEvent>.next(action: NoteGalleryAction): NoteGalleryState {
            return when (action) {
                is NoteGalleryAction.Sync -> {
                    addEffect(NoteGalleryEffect.FetchAllNotes)
                    currentState
                }
                is NoteGalleryAction.UpdateNoteList -> {
                    currentState.copy(
                        notes = action.noteList
                    )
                }

                //TO DETAILS
                is NoteGalleryAction.ToDetails.New -> {
                    addEvent(NoteGalleryEvent.ToDetails.New)
                    currentState
                }
                is NoteGalleryAction.ToDetails.Edit -> {
                    addEvent(NoteGalleryEvent.ToDetails.Edit(action.id))
                    currentState
                }

                //DELETIONS
                is NoteGalleryAction.DeleteNote.Select -> {
                    currentState.copy (
                        selectedNote = action.note,
                        isDeleteDialogShown = true
                    )
                }
                is NoteGalleryAction.DeleteNote.Confirm -> {
                    addEffect(NoteGalleryEffect.DeleteNote(currentState.selectedNote))
                    currentState.copy (
                        selectedNote = NoteData.emptyNote,
                        isDeleteDialogShown = false
                    )
                }
                is NoteGalleryAction.DeleteNote.Dismiss -> {
                    currentState.copy (
                        selectedNote = NoteData.emptyNote,
                        isDeleteDialogShown = false
                    )
                }
                is NoteGalleryAction.DeleteNote.Deleted -> {
                    currentState.copy (
                        notes = currentState.notes.filter { it.id != action.note.id }
                    )
                }
            }
        }
    }

    class NoteGalleryProcessor : Processor<NoteGalleryEffect, NoteGalleryAction> {
        class Factory @Inject constructor() {
            fun create(): NoteGalleryProcessor {
                return NoteGalleryProcessor()
            }
        }

        private val fetchAllData: (Observable<NoteGalleryEffect>) -> Observable<NoteGalleryAction> =
            { effects ->
                effects.ofType(NoteGalleryEffect.FetchAllNotes::class.java)
                    .map {
                        val list = repositoryRXJ.readAllNote()
                            .blockingFirst()

                        NoteGalleryAction.UpdateNoteList(list)
                    }
            }

        private val deleteNote: (Observable<NoteGalleryEffect>) -> Observable<NoteGalleryAction> =
            { effects ->
                effects.ofType(NoteGalleryEffect.DeleteNote::class.java)
                    .map { effect ->
                        repositoryRXJ.removeNote(effect.note)
                            .blockingAwait()

                        NoteGalleryAction.DeleteNote.Deleted(effect.note)
                    }
            }

        private val effectToActionTransformers: List<(Observable<NoteGalleryEffect>) -> Observable<NoteGalleryAction>> =
            listOf(
                fetchAllData,
                deleteNote
            )

        override fun invoke(effectsOnBgThread: Observable<NoteGalleryEffect>): Observable<out NoteGalleryAction> {
            return effectsOnBgThread.publish { effects ->
                Observable.merge(effectToActionTransformers.map { effects.compose(it) })
            }
        }
    }


    sealed class NoteGalleryEffect {
        data object FetchAllNotes : NoteGalleryEffect()
        data class DeleteNote(val note: NoteData) : NoteGalleryEffect()
    }

    sealed class NoteGalleryAction {
        sealed class ToDetails : NoteGalleryAction() {
            data class Edit(val id: Int) : ToDetails()
            data object New : ToDetails()
        }
        sealed class DeleteNote : NoteGalleryAction() {
            data class Select(val note: NoteData) : DeleteNote()
            data object Confirm : DeleteNote()
            data object Dismiss : DeleteNote()
            data class Deleted(val note: NoteData) : DeleteNote()
        }
        data object Sync : NoteGalleryAction()
        data class UpdateNoteList(val noteList: List<NoteData>) : NoteGalleryAction()
    }

    sealed class NoteGalleryEvent {
        sealed class ToDetails : NoteGalleryEvent() {
            data class Edit(val id: Int) : ToDetails()
            data object New : ToDetails()
        }
    }

    data class NoteGalleryState(
        val notes: List<NoteData>,
        val selectedNote: NoteData,
        val isDeleteDialogShown: Boolean
    ) {
        companion object {
            fun emptyState() = NoteGalleryState(
                notes = mutableListOf(),
                selectedNote = NoteData.emptyNote,
                isDeleteDialogShown = false
            )
        }
    }

    companion object {
        lateinit var repositoryRXJ: NoteRepositoryRXJ
    }
}