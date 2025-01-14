package com.plangrid.android.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.ofType
import javax.inject.Inject

/**
 * Template of Mobius view holder, with all relevant classes and patterns
 */
@Suppress("UNCHECKED_CAST")
class MobiusTemplate constructor(
    uiScheduler: Scheduler,
    effectsScheduler: Scheduler,
    processor: TemplateProcessor,
) : MobiusViewModel<MobiusTemplate.Action, MobiusTemplate.Effect, MobiusTemplate.State, MobiusTemplate.State, Nothing>(
    initialState = State.emptyState(),
    updater = Updater,
    initialEffects = setOf(Effect.FetchInitialData),
    uiScheduler = uiScheduler,
    effectsScheduler = effectsScheduler,
    processor = processor
) {

    /**
     * Use @UiScheduler/@IoScheduler when in feature context
     */
    class Factory @Inject constructor(
        /*@UiScheduler*/
        private val uiScheduler: Scheduler,
        /*@IoScheduler*/
        private val effectsScheduler: Scheduler,
        private val processorFactory: TemplateProcessor.Factory,
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MobiusTemplate(
                uiScheduler = uiScheduler,
                effectsScheduler = effectsScheduler,
                processor = processorFactory.create(),
            ) as T
        }
    }

    object Updater : NextUpdater<State, Action, Effect, Nothing> {
        override fun Next.Builder<State, Effect, Nothing>.next(action: Action): State {
            return when (action) {
                is Action.Data -> currentState
            }
        }
    }

    class TemplateProcessor() : Processor<Effect, Action> {
        class Factory @Inject constructor() {
            fun create(): TemplateProcessor {
                return TemplateProcessor()
            }
        }

        private val fetchInitialData: (Observable<Effect>) -> Observable<Action> = { effects ->
            effects.ofType<Effect.FetchInitialData>().map {
                Action.Data(id = "", list = emptyList())
            }
        }
        private val effectToActionTransformers: List<(Observable<Effect>) -> Observable<Action>> =
            listOf(
                fetchInitialData,
            )

        override fun invoke(effectsOnBgThread: Observable<Effect>): Observable<out Action> {
            return effectsOnBgThread.publish { effects ->
                Observable.merge(effectToActionTransformers.map { effects.compose(it) })
            }
        }
    }

    sealed class Effect {
        object FetchInitialData : Effect()
    }

    sealed class Action {
        data class Data(val id: String, val list: List<String>) : Action()
    }

    data class State(
        val id: String,
        val list: List<String>
    ) {

        companion object {
            fun emptyState() = State(
                id = "",
                list = emptyList()
            )
        }
    }
}
