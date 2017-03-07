package com.dummyc0m.pylon.framework.experimental

import io.vertx.core.Vertx
import io.vertx.core.VertxOptions

/**
 * helper to create vertx
 * Created by dummy on 2/21/17.
 */
fun vertx(init: VertxOptions.() -> Unit): Vertx {
    return Vertx.vertx(VertxOptions().apply(init))
}
