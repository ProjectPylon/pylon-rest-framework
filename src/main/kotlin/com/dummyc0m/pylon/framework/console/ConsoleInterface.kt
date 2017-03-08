package com.dummyc0m.pylon.framework.console

import com.dummyc0m.pylon.framework.experimental.Pylon
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * reads console commands and does stuff
 * Created by dummy on 2/12/17.
 */
class ConsoleInterface(private val pylon: Pylon,
                                private val inputStream: InputStream) : Thread("PylonConsoleThread") {
    val commandManager = CommandManager()

    override fun run() {
        try {
            with(BufferedReader(InputStreamReader(inputStream))) {
                var line = readLine()
                while (pylon.running && line !== null) {
                    commandManager.enqueueCommand(line)
                    line = readLine()
                }
            }
        } catch (exception: IOException) {
            pylon.logger.error("exception reading console commands", exception)
        }
    }
}
