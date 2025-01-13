package com.plangrid.android.mvi

import android.os.Looper
import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

/**
 * [Action]s are inputs into Mobius.
 *
 * [Effect]s are asynchronous tasks that are automatically run on a bg thread. Upon completion of the [Effect], the [Processor] should
 * emit an [Action] that will either modify the [State] or emit an [Event] (or, perhaps do nothing if the Action is no-op).
 *
 * [Event]s are a way to synchronously (on UI thread) communicate transient information that shouldn't be kept as part of the [State] or
 * a way for Child [Mobius] to communicate with a Parent [Mobius].
 *   For example, navigation events are an ideal use-case for this.
 *
 * [State] is a private bundle of data that represents the current state of this [Mobius].
 *
 * [RenderData] is a way to not leak data (that should be private to this [Mobius]) to the consumers.
 *   For example, it might not make sense to expose some properties/fields that are used internally in [Mobius] to derive [State]. Use
 *   [StateMapper] to convert [State] into [RenderData].
 *
 */
abstract class MobiusViewModel<Action : Any, Effect : Any, State : Any, RenderData : Any, Event : Any>
constructor(
    override val initialState: State,
    override val initialEffects: Set<Effect> = emptySet(),
    override val updater: NextUpdater<State, Action, Effect, Event>,
    override val processor: Processor<Effect, Action>? = null,
    final override val stateMapper: StateMapper<State, RenderData> = { state: State ->
        @Suppress("UNCHECKED_CAST")
        state as? RenderData
            ?: throw IllegalStateException(
                "Unable to map State -> RenderData without a StateMapper. Ensure StateMapper is set during construction"
            )
    },
    override val uiScheduler: Scheduler = AndroidSchedulers.mainThread(),
    override val effectsScheduler: Scheduler = Schedulers.io(),
    // allow thread checking to be overridden for junit tests since Looper is stubbed
    override val mainThreadCheck: (Thread) -> Boolean = { it == Looper.getMainLooper().thread },
    logger: StateLogger<State>? = { _ -> Unit },
    events: Relay<Event> = PublishRelay.create()
) : ViewModel(), Mobius<Action, Effect, State, RenderData, Event> {

    /**
     * This is used so that effects resulting from update() can be processed in action stream
     */
    override val effectRelay: Relay<Effect> = PublishRelay.create()

    override val actionRelay: Relay<Action> = PublishRelay.create()

    override val eventRelay: Relay<Event> = events

    final override val disposables: CompositeDisposable = CompositeDisposable()

    final override val renderableStream: Observable<out Renderable<RenderData, Action>> by lazy { createRenderableStream(logger) }

    final override val renderDataStream: Observable<out RenderData> by lazy { renderableStream.map { it.data } }

    /**
     *  The current state. Note: It is guaranteed that there's a value for currentState, since we start with an
     *  initial value. The value is available synchronously and is guaranteed to return without blocking anything.
     */
    val currentState: RenderData get() = renderDataStream.blockingFirst()

    fun action(action: Action) {
        actionRelay.accept(action)
    }

    fun effect(effect: Effect) {
        effectRelay.accept(effect)
    }

    fun event(event: Event) {
        eventRelay.accept(event)
    }

    init {
        // Primary importance of this is to resolve the lazy state stream immediately, so that
        // Actions from external hot sources are not dropped on the floor.
        renderableStream
            .subscribe()
            .addTo(disposables)
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}
