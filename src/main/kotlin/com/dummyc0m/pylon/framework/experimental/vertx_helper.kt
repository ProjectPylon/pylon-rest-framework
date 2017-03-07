package com.dummyc0m.pylon.framework.experimental

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * https://github.com/Kotlin/kotlin-coroutines/blob/master/kotlin-coroutines-informal.md
 * Created by dummy on 3/7/17.
 */
inline suspend fun <T> vx(crossinline callback: (Handler<AsyncResult<T>>) -> Unit) =
        suspendCoroutine<T> { cont ->
            callback(Handler { result: AsyncResult<T> ->
                if (result.succeeded()) {
                    cont.resume(result.result())
                } else {
                    cont.resumeWithException(result.cause())
                }
            })
        }