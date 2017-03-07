import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

/**
 * Created by dummy on 3/6/17.
 */
fun main(args: Array<String>) {
    println("Start")

    // Start a coroutine
    launch(CommonPool) {
        delay(1000)
        println("Hello")
    }

    Thread.sleep(2000) // wait for 2 seconds
    println("Stop")
}
