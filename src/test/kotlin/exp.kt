import com.dummyc0m.pylon.framework.experimental.*
import kotlinx.coroutines.experimental.future.await

/**
 * Created by dummy on 2/28/17.
 */
// trash below
// experimentation

fun main(args: Array<String>) {
    val something = com.dummyc0m.pylon.framework.experimental.pylon {
        port = 8080

        provider { AuthForm("ewriuadfs", "asdf") }

        controller("authController", "/auth") {
            route(com.dummyc0m.pylon.framework.experimental.RouteMethod.ANY, "/*").handle(io.vertx.ext.web.handler.BodyHandler.create())

            post("/authenticate") {
                AuthForm(it.request().getFormAttribute("username"), it.request().getFormAttribute("secret"))
            }.handle {
                val (username, secret) = it
                io.vertx.core.json.JsonObject()
            }
        }

        controller("randomController", "/random") {
            //            val anything = service<ConferenceListForm>()
            route(com.dummyc0m.pylon.framework.experimental.RouteMethod.ANY, "/*").handle(io.vertx.ext.web.handler.BodyHandler.create())
            route(com.dummyc0m.pylon.framework.experimental.RouteMethod.ANY, "/*").handle(io.vertx.ext.web.handler.CorsHandler.create("*"))

            post("/conferences/:id/list") {
                ConferenceListForm(it.pathParam("id"), it.request().getFormAttribute("formId").toInt(), it.user())
            }.handle {
                val (id, formId, user) = it
                418
            }

            get("/conferences/list").handle {
                io.vertx.core.json.JsonObject()
            }

            catch {
                logger.debug("error in $name", it)
            }
        }

        controller(ACertainStupidController(), "/science")

    }.start()
    val consoleInterface = com.dummyc0m.pylon.framework.console.ConsoleInterface(something, System.`in`)
    consoleInterface.commandManager.addConsumer("stop") {
        val future = something.shutdown()
        future.await()
        System.exit(0)
    }
    consoleInterface.start()
}

data class AuthForm(val username: String, val secret: String)

data class ConferenceListForm(val id: String, val formId: Int, val user: io.vertx.ext.auth.User)

class ACertainStupidController : com.dummyc0m.pylon.framework.experimental.Controller("theCertainScientificController") {
    override fun init() {
        val service = service<AuthForm>()

        get("/conf").handle {
            service
        }
    }
}