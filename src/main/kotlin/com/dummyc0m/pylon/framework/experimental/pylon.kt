package com.dummyc0m.pylon.framework.experimental

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.net.JksOptions
import io.vertx.ext.web.Router
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Created by dummy on 2/21/17.
 */
class Pylon(internal val vertx: Vertx, parentPath: String, name: String = "pylon") {
    private val rootRouter = Router.router(vertx)
    internal val router = Router.router(vertx)
    val logger = LoggerFactory.getLogger(this.javaClass)
    private var httpServer: HttpServer? = null
    val serviceDiscovery = ConcurrentHashMap<KClass<*>, Any>()
    internal var running = false

    var ssl = false
    var jksPath = ""
    var jksPassword = ""
    var port = 8080
    var host = "localhost"

    init {
        rootRouter.mountSubRouter(parentPath, router)
    }

    inline fun <reified T> require(): T {
        with(serviceDiscovery.entries.find { (key, value) -> key == T::class }) {
            val value = this?.value
            if (this === null || value !is T) {
                throw ServiceNotFoundException("cannot find ${T::class}")
            }
            return value
        }
    }

    inline fun <reified T> provide(service: T) {
        if (service !== null) {
            serviceDiscovery.put(T::class, service)
        } else {
            throw ServiceNotInitializedException("cannot initialize ${T::class}")
        }
    }

    inline fun <reified T> Pylon.provider(provider: () -> T) {
        provide(provider())
    }

    fun start(): Pylon {
        rootRouter.route().handler {
            it.fail(404)
        }
        val httpServer = vertx.createHttpServer(HttpServerOptions()
                .setSsl(ssl)
                .setKeyCertOptions(JksOptions()
                        .setPath(jksPath)
                        .setPassword(jksPassword))
                .setPort(port)
                .setHost(host)
        ).requestHandler {
            rootRouter.accept(it)
        }
        httpServer.listen {
            logger.info("Pylon started on $host:$port${if (ssl) " with SSL" else ""}")
            this.httpServer = httpServer
            this.running = true
        }
        return this
    }

    fun shutdown(): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        httpServer?.close {
            logger.info("Pylon shutdown on $host:$port")
            this.running = false
            future.complete(Unit)
        }
        return future
    }
}

fun Vertx.pylon(parentPath: String = "/", init: Pylon.() -> Unit): Pylon {
    with(Pylon(this, parentPath)) {
        init()
        return this
    }
}

fun pylon(parentPath: String = "/", init: Pylon.() -> Unit): Pylon {
    return Vertx.vertx().pylon(parentPath, init)
}

fun Pylon.controller(controller: Controller, controllerPath: String = "/") {
    controller.pylon = this
    controller.router = Router.router(controller.pylon.vertx)
    controller.init()
    router.mountSubRouter(controllerPath, controller.router)
}

fun Pylon.controller(name: String = "", controllerPath: String = "/", init: Controller.() -> Unit) {
    with(PylonController(name)) {
        pylon = this@controller
        router = Router.router(pylon.vertx)
        init.invoke(this)
        this@controller.router.mountSubRouter(controllerPath, router)
    }
}