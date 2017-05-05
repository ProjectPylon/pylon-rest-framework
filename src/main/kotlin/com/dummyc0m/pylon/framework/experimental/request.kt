package com.dummyc0m.pylon.framework.experimental

import io.vertx.core.Handler
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.buildIterator

/**
 * a little silly, but works. needs clean up
 * Created by dummy on 2/21/17.
 */
class Request<T>(method: RouteMethod, val name: String, pylon: Pylon) {
    internal var handler: (T.(RoutingContext) -> Any?)? = null
    val logger = LoggerFactory.getLogger(this.javaClass)

    internal fun consume(ctx: RoutingContext, t: T) {
        val result = handler?.invoke(t, ctx)
    }

    fun next(): RequestHandler = RequestHandler.NEXT

    internal fun handleResult(result: Any?, ctx: RoutingContext) {
        when (result) {
            null, Unit -> Unit
            RequestHandler.NEXT -> ctx.next()
            is Int -> ctx.response().setStatusCode(result).end()
            is String -> ctx.response().end(result)
            is JsonObject -> ctx.response().end(result.encode())
            else -> ctx.response().end(Json.encode(result))
        }
    }
}

fun <T> Request<T>.sync(handle: T.() -> Any?) {
    handler = {
        handleResult(this.handle(), it)
    }
}

fun Request<RoutingContext>.sync(handle: Handler<RoutingContext>) {
    handler = {
        handleResult(handle.handle(this), it)
    }
}

fun <T> Request<T>.async(handle: suspend T.() -> Any?) {
    handler = {
        val self = this
        launch(CommonPool) {
            handleResult(self.handle(), it)
        }
    }
}

enum class RequestHandler {
    NEXT
}