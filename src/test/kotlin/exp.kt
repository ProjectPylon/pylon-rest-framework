import com.dummyc0m.pylon.framework.console.ConsoleInterface
import com.dummyc0m.pylon.framework.experimental.*
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
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
        port = 34829

        provider { AuthForm("ewriuadfs", "asdf") }

        route(name = "/*").sync(CorsHandler.create("*").allowedHeader("authorization").allowedMethods(hashSetOf(HttpMethod.GET, HttpMethod.OPTIONS, HttpMethod.POST)))
        route(name = "/*").sync(BodyHandler.create())

        controller("authController", "/auth") {
            post("/authenticate") {
                AuthForm(requestParam("username"), requestParam("secret"))
            }.async {
                val (username, secret) = this
                JsonObject().put("username", username).put("secret", secret)
            }

            catch { logger.debug(it) }
        }

        controller("randomController", "/random") {
            post("/conferences/:id/list") {
                ConferenceListForm(pathParam("id"), requestParam("formId").toInt(), user())
            }.sync {
                val (id, formId, user) = this
                418
            }

            get("/conferences/list").sync {
                io.vertx.core.json.JsonObject()
            }

            catch {
                logger.debug("error in $name", this)
            }
        }

        controller(ACertainStupidController(), "/science")
    }.start()

    val consoleInterface = ConsoleInterface(something, System.`in`)
    consoleInterface.commandManager.addConsumer("quit()") {
        val future = something.shutdown()
        println("Shutting down...")
        future.await()
        System.exit(0)
    }
    consoleInterface.start()
}

data class AuthForm(val username: String, val secret: String)

data class ConferenceListForm(val id: String, val formId: Int, val user: io.vertx.ext.auth.User)

class ACertainStupidController : Controller("theCertainScientificController") {
    override fun init() {
        val service = require<AuthForm>()

        get("/conf").sync {
            service
        }
    }
}
