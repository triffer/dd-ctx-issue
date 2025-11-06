package com.example

import io.ktor.server.netty.EngineMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

fun main(args: Array<String>) {
    runBlocking {
        launch {
            EngineMain.main(args)
        }
        start(this)
    }
}

private fun start(scope: CoroutineScope) {
    val job = TestJob(TestService())
    scope.launch(Dispatchers.Default) {
        while (isActive) {
            job.start()
            delay(60.seconds)
        }
    }
}
