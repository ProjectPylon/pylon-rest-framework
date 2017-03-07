package com.dummyc0m.pylon.framework.experimental

import io.vertx.core.Handler
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.RoutingContext

/**
 * Created by dummy on 2/21/17.
 */
class Request<T>(method: RouteMethod, name: String, pylon: Pylon) {
    internal var handler: (suspend (T) -> Any?)? = null
    val logger = LoggerFactory.getLogger(this.javaClass)

    internal suspend fun consume(ctx: RoutingContext, t: T) {
        val result = handler?.invoke(t)
        when (result) {
            null, Unit -> Unit
            RequestHandler.NEXT -> ctx.next()
            is Int -> ctx.response().setStatusCode(result).end()
            is String -> ctx.response().end(result)
            is JsonObject -> ctx.response().end(result.encode())
            else -> ctx.response().end(Json.encode(result))
        }
    }

    fun next(): RequestHandler = RequestHandler.NEXT
}

fun <T> Request<T>.handle(handle: suspend (T) -> Any?) {
    handler = handle
}

fun Request<RoutingContext>.handle(handle: Handler<RoutingContext>) {
    handler = { handle.handle(it) }
}

enum class RequestHandler {
    NEXT
}