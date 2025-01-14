/*
 * Copyright (c) 2019 PlanGrid, Inc. All rights reserved.
 */

package com.plangrid.android.mvi

import io.reactivex.Observable
import io.reactivex.rxkotlin.ofType

inline fun <E : Any, reified E1 : E, A> Observable<E>.handleEveryWithAction(
    noinline subProcessor: (E1) -> A
): Observable<A> =
    ofType<E1>()
        .map(subProcessor)

inline fun <E : Any, reified E1 : E, A> Observable<E>.handleEveryWithActions(
    noinline subProcessor: (E1) -> List<A>
): Observable<A> =
    ofType<E1>()
        .map(subProcessor)
        .concatMap { Observable.fromIterable(it) }

inline fun <E : Any, reified E1 : E, A> Observable<E>.handleEveryWithStream(
    noinline subProcessor: (E1) -> Observable<A>
): Observable<A> =
    ofType<E1>()
        .flatMap(subProcessor)

inline fun <E : Any, reified E1 : E, A> Observable<E>.handleCancelingPreviousStream(
    noinline subProcessor: (E1) -> Observable<A>
): Observable<A> =
    ofType<E1>()
        .switchMap(subProcessor)

inline fun <E : Any, reified E1 : E, A> Observable<E>.handleWithoutActions(
    noinline subProcessor: (E1) -> Unit
): Observable<A> =
    ofType<E1>()
        .doOnNext(subProcessor)
        .flatMap { Observable.empty() }
