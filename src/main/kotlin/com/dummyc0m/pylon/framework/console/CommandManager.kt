package com.dummyc0m.pylon.framework.console

import kotlinx.coroutines.experimental.launch
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by dummy on 2/12/17.
 */
internal class CommandManager {
    private val consumers = ConcurrentHashMap<String, MutableList<suspend (List<String>) -> Unit>>()

    fun enqueueCommand(command: String) {
        val commandAndArgs = command.split(" ")
        if (commandAndArgs.isNotEmpty())
            consumers[commandAndArgs[0]]?.apply {
                this.forEach {
                    launch(kotlinx.coroutines.experimental.CommonPool) {
                        it.invoke(commandAndArgs.drop(1))
                    }
                }
            }
    }

    fun addConsumer(command: String, consumer: suspend (List<String>) -> Unit) {
        val consumerList = consumers[command]
        if (consumerList !== null) {
            consumerList.add(consumer)
        } else {
            consumers[command] = Collections.synchronizedList(arrayListOf(consumer))
        }
    }
}