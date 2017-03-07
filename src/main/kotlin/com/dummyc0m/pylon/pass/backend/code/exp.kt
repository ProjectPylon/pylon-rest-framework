package com.dummyc0m.pylon.pass.backend.code

import com.dummyc0m.pylon.framework.console.ConsoleInterface
import com.dummyc0m.pylon.framework.experimental.*
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import kotlinx.coroutines.experimental.future.await

/**
 * Created by dummy on 2/28/17.
 */
// trash below
// experimentation

fun main(args: Array<String>) {
    val something = pylon {
        port = 8080

        provider { AuthForm("ewriuadfs", "asdf") }

        controller("authController", "/auth") {
            route(RouteMethod.ANY, "/*").handle(BodyHandler.create())

            post("/authenticate") {
                AuthForm(it.request().getFormAttribute("username"), it.request().getFormAttribute("secret"))
            }.handle {
                val (username, secret) = it
                JsonObject()
            }
        }

        controller("randomController", "/random") {
            //            val anything = service<ConferenceListForm>()
            route(RouteMethod.ANY, "/*").handle(BodyHandler.create())
            route(RouteMethod.ANY, "/*").handle(CorsHandler.create("*"))

            post("/conferences/:id/list") {
                ConferenceListForm(it.pathParam("id"), it.request().getFormAttribute("formId").toInt(), it.user())
            }.handle {
                val (id, formId, user) = it
                418
            }

            get("/conferences/list").handle {
                JsonObject()
            }

            catch {
                logger.debug("error in $name", it)
            }
        }

        controller(ACertainStupidController())

    }.start()
    val consoleInterface = ConsoleInterface(something, System.`in`)
    consoleInterface.commandManager.addConsumer("stop") {
        val future = something.shutdown()
        future.await()
        System.exit(0)
    }
    consoleInterface.start()
}

data class AuthForm(val username: String, val secret: String)

data class ConferenceListForm(val id: String, val formId: Int, val user: User)

class ACertainStupidController : Controller("theCertainScientificController", "/science") {
    override fun init() {
        val service = service<AuthForm>()

        get("/conf").handle {
            service
        }
    }
}