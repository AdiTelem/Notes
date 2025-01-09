/*
 * Copyright (c) 2019 PlanGrid, Inc. All rights reserved.
 */

package com.plangrid.android.mvi

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.plangrid.android.mvi.MobiusViewModelTest.TestMobius.Action
import com.plangrid.android.mvi.MobiusViewModelTest.TestMobius.Effect
import com.plangrid.android.mvi.MobiusViewModelTest.TestMobius.Event
import com.plangrid.android.mvi.MobiusViewModelTest.TestMobius.State
import io.kotest.core.spec.style.FreeSpec
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.observers.TestObserver
import io.reactivex.rxkotlin.ofType
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import java.util.concurrent.TimeUnit

class MobiusViewModelTest : FreeSpec({

    "Given Mobius" - {
        val initialValue = -5
        val initialState = State(initialValue)

        "Initial State is emitted right away" {
            val testMobius = TestMobius(initialState = initialState)

            testMobius
                .renderDataStream
                .test()
                .assertValueAt(0) {
                    it == State(initialValue)
                }
        }

        "Effect is delayed" {
            val effectsScheduler = TestScheduler()
            val testMobius = TestMobius(
                initialState = initialState,
                effectsScheduler = effectsScheduler
            )

            val testObserver = testMobius
                .renderDataStream
                .test()

            testObserver.assertValueCount(1)

            val immediateChange = 13
            val asyncEffectChange = 7

            testMobius.userAction(Action.IncrementWithEffect(immediateChange, asyncEffectChange))

            effectsScheduler.advanceTimeBy(DELAY_TIME_MS - (DELAY_TIME_MS / 2), TimeUnit.MILLISECONDS)

            testObserver
                .assertValueCount(2)
                .assertValueAt(1) {
                    it == State(initialValue + immediateChange)
                }

            effectsScheduler.advanceTimeBy(DELAY_TIME_MS / 2, TimeUnit.MILLISECONDS)

            testObserver
                .assertValueCount(3)
                .assertValueAt(2) {
                    it == State(initialValue + immediateChange + asyncEffectChange)
                }
        }

        "UIEffects are synchronously received" {
            val effectsScheduler = TestScheduler()

            val testMobius = TestMobius(
                initialState = initialState,
                effectsScheduler = effectsScheduler
            )

            val testObserver = testMobius
                .renderDataStream
                .test()

            testObserver.assertValueCount(1)

            val actionChange = 13
            val effectChange = 7

            testMobius.userAction(Action.ChangeByWithUIEffect(actionChange, effectChange))
            testObserver
                .assertValueCount(3) // 3 because Action + Effect happen synchronously
                .assertValueAt(1) {
                    it == State(initialValue + actionChange)
                }
                .assertValueAt(2) {
                    it == State(initialValue + actionChange + effectChange)
                }
        }

        "Events are synchronously received" {
            val effectsScheduler = TestScheduler()
            val testMobius = TestMobius(
                initialState = initialState,
                effectsScheduler = effectsScheduler
            )

            val eventsObserver = testMobius.eventRelay.test()

            eventsObserver.assertEmpty()

            val stateObserver: TestObserver<out State> = testMobius
                .renderDataStream
                .test()

            stateObserver.assertValueCount(1)
            stateObserver.assertValueAt(0) {
                it == initialState
            }
            // now that we've received the initial state, make sure that no Events have been fired
            eventsObserver.assertEmpty()

            val increaseValue = 10
            testMobius.userAction(Action.ChangeByWithEvent(increaseValue))

            stateObserver.assertValueCount(2)
            stateObserver.assertValueAt(1) {
                it.counter == initialState.counter + increaseValue
            }

            eventsObserver.assertValueAt(0) {
                when (it) {
                    is Event.StringEvent -> false
                    is Event.IntEvent -> {
                        it.number == increaseValue
                    }
                }
            }

            testMobius.userAction(Action.Say("hello", emitEvent = true))
            // no State emissions, since we don't store the string in the State
            stateObserver.assertValueCount(2)

            eventsObserver.assertValueAt(1) {
                when (it) {
                    is Event.StringEvent -> {
                        it.string == "hello"
                    }
                    is Event.IntEvent -> false
                }
            }

            testMobius.userAction(
                Action.IncrementWithEffectAndEvent(
                    immediateChange = 10,
                    effectChange = 22,
                    emitEvent = true
                )
            )

            eventsObserver.assertValueAt(2) {
                when (it) {
                    is Event.StringEvent -> false
                    is Event.IntEvent -> {
                        it.number == 10
                    }
                }
            }

            // Effects are done on a bg thread. Once the Effect is completed, it emits a new Action, which can emit an Event
            effectsScheduler.advanceTimeBy(DELAY_TIME_MS * 2, TimeUnit.MILLISECONDS)
            eventsObserver.assertValueAt(3) {
                when (it) {
                    is Event.StringEvent -> false
                    is Event.IntEvent -> it.number == 22
                }
            }
        }
    }
}) {
    companion object {
        const val DELAY_TIME_MS = 2000L
    }

    class TestMobiusProcessor(
        private val effectsScheduler: Scheduler
    ) : Processor<Effect, Action> {

        override fun invoke(effects: Observable<Effect>): Observable<out Action> {
            return Observable.mergeArray(
                effects
                    .ofType<Effect.ChangeAsync>()
                    .flatMap { effect ->
                        Observable
                            .just(Action.ChangeBy(effect.changeBy, effect.emitEvent))
                            .delay(effect.delay, TimeUnit.MILLISECONDS, effectsScheduler)
                    },
                effects
                    .ofType<Effect.UI.ChangeSync>()
                    .flatMap { effect ->
                        Observable.just(Action.ChangeBy(effect.changeBy))
                    }
            )
        }
    }

    class TestMobius(
        initialState: State,
        events: Relay<Event> = PublishRelay.create(),
        initialEffects: Set<Effect> = emptySet(),
        effectsScheduler: Scheduler = Schedulers.trampoline(),
        testThread: Thread = Thread.currentThread(),
        mainThreadCheck: (Thread) -> Boolean = { it == testThread }
    ) :
        MobiusViewModel<Action, Effect, State, State, Event>(
            initialState,
            events = events,
            initialEffects = initialEffects,
            processor = TestMobiusProcessor(effectsScheduler),
            updater = { state: State, action: Action ->
                when (action) {
                    is Action.Say -> if (action.emitEvent) {
                        Next(
                            state = state,
                            events = setOf(Event.StringEvent(action.message))
                        )
                    } else {
                        Next(state = state)
                    }
                    is Action.ChangeBy -> {
                        val newState = state.copy(counter = state.counter + action.value)
                        if (action.emitEvent) {
                            Next(newState, events = setOf(Event.IntEvent(action.value)))
                        } else {
                            Next(newState)
                        }
                    }
                    is Action.IncrementWithEffect -> Next(
                        state.copy(counter = state.counter + action.immediateChange),
                        Effect.ChangeAsync(DELAY_TIME_MS, action.effectChange)
                    )
                    is Action.ChangeByWithEvent -> {
                        Next(
                            state = state.copy(counter = state.counter + action.value),
                            events = setOf(Event.IntEvent(action.value))
                        )
                    }
                    is Action.IncrementWithEffectAndEvent -> {
                        val newState = state.copy(counter = state.counter + action.immediateChange)
                        val effects = setOf(Effect.ChangeAsync(DELAY_TIME_MS, action.effectChange, emitEvent = true))

                        if (action.emitEvent) {
                            Next(
                                state = newState,
                                effects = effects,
                                events = setOf(Event.IntEvent(action.immediateChange))
                            )
                        } else {
                            Next(
                                state = newState,
                                effects = effects
                            )
                        }
                    }
                    is Action.ChangeByWithUIEffect -> {
                        Next(
                            state = state.copy(counter = state.counter + action.actionChange),
                            effect = Effect.UI.ChangeSync(action.effectChange)
                        )
                    }
                }
            },
            effectsScheduler = effectsScheduler,
            uiScheduler = Schedulers.trampoline(),
            mainThreadCheck = mainThreadCheck
        ) {

        fun userAction(action: Action) {
            action(action)
        }

        sealed class Action {
            data class Say(val message: String, val emitEvent: Boolean) : Action()
            data class ChangeBy(val value: Int, val emitEvent: Boolean = false) : Action()
            data class IncrementWithEffect(val immediateChange: Int, val effectChange: Int) : Action()
            data class ChangeByWithEvent(val value: Int) : Action()
            data class IncrementWithEffectAndEvent(val immediateChange: Int, val effectChange: Int, val emitEvent: Boolean) : Action()
            data class ChangeByWithUIEffect(val actionChange: Int, val effectChange: Int) : Action()
        }

        sealed class Effect {
            data class ChangeAsync(val delay: Long, val changeBy: Int, val emitEvent: Boolean = false) : Effect()
            sealed class UI : Effect(), UIEffect {
                data class ChangeSync(val changeBy: Int) : UI()
            }
        }

        data class State(val counter: Int)

        sealed class Event {
            data class StringEvent(val string: String) : Event()
            data class IntEvent(val number: Int) : Event()
        }
    }
}
