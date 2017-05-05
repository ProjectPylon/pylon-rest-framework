package com.dummyc0m.pylon.framework.experimental

import io.vertx.core.http.HttpMethod
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

/**
 * Created by dummy on 2/21/17.
 */
abstract class Controller(val name: String) {
    lateinit var pylon: Pylon
    internal lateinit var router: Router
    val logger = LoggerFactory.getLogger(this.javaClass)!!

    abstract fun init()
}

class PylonController(name: String) : Controller(name) {
    override fun init() {}
}

fun <T> Controller.route(method: RouteMethod = RouteMethod.ANY, name: String, parse: RoutingContext.() -> T): Request<T> {
    // register request
    val request = Request<T>(method, name)
    with(if (method === RouteMethod.ANY) {
        router.route(name)
    } else {
        router.route(method.vertxMethod, name)
    }) {
        this.handler {
            request.consume(it, it.parse())
        }
    }
    return request
}

fun Controller.route(method: RouteMethod = RouteMethod.ANY, name: String): Request<RoutingContext> = route(method, name, emptyRequestHandler)

fun <T> Controller.get(name: String, init: RoutingContext.() -> T) = route(RouteMethod.GET, name, init)

// TODO Don't forget multipart
fun <T> Controller.post(name: String, init: RoutingContext.() -> T) = route(RouteMethod.POST, name, init)

fun Controller.get(name: String) = route(RouteMethod.GET, name, emptyRequestHandler)

fun Controller.post(name: String) = route(RouteMethod.POST, name, emptyRequestHandler)

fun Controller.catch(exceptionHandler: Controller.(Throwable) -> Unit) {
    router.exceptionHandler {
        this.exceptionHandler(it)
    }
}

inline fun <reified T> Controller.require(): T {
    return pylon.require<T>()
}

//fun Controller.controller(controller: Controller, controllerPath: String = "/") {
//    controller.pylon = pylon
//    controller.router = Router.router(controller.pylon.vertx)
//    controller.init()
//    router.mountSubRouter(controllerPath, controller.router)
//}
//
//fun Controller.controller(name: String = "", controllerPath: String = "/", init: Controller.() -> Unit) {
//    with(PylonController("${this.name}/$name")) {
//        pylon = this@controller.pylon
//        router = Router.router(pylon.vertx)
//        init.invoke(this)
//        this@controller.router.mountSubRouter(controllerPath, router)
//    }
//}

enum class RouteMethod(internal val vertxMethod: HttpMethod) {
    DELETE(HttpMethod.DELETE),
    HEAD(HttpMethod.HEAD),
    OPTIONS(HttpMethod.OPTIONS),
    PATCH(HttpMethod.PATCH),
    PUT(HttpMethod.PUT),
    TRACE(HttpMethod.TRACE),
    GET(HttpMethod.GET),
    POST(HttpMethod.POST),
    ANY(HttpMethod.OTHER)
}

val emptyRequestHandler: RoutingContext.() -> RoutingContext = { this }
