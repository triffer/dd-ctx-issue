package com.example

import io.github.oshai.kotlinlogging.KotlinLogging
import io.opentelemetry.api.trace.Span
import com.example.tracing.tracingContext
import io.opentelemetry.instrumentation.annotations.WithSpan
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class FixedTestService {

    private val logger = KotlinLogging.logger { }

    @WithSpan("FixedTestService.test")
    suspend fun test() {
        val span = Span.current()
        logger.info { "--> Span FixedTestService.test" }
        logger.info { "Span context valid: ${span.spanContext.isValid}" }
        logger.info { "Span ID: ${span.spanContext.spanId}" }
        logger.info { "Trace ID: ${span.spanContext.traceId}" }

        withContext(tracingContext()) {
            flow {
                for (i in 1..4) {
                    emit(i)
                }
            }
                .take(5)
                .map { doSomething(it) }
                .collect { }
        }
    }

    private suspend fun doSomething(i: Int): Int {
        val span = Span.current()
        logger.info { "--> Span FixedTestService.doSomething" }
        logger.info { "Span context valid: ${span.spanContext.isValid}" }
        logger.info { "Span ID: ${span.spanContext.spanId}" }
        logger.info { "Trace ID: ${span.spanContext.traceId}" }
        delay(1.milliseconds)
        return i
    }
}
