package com.example

import io.github.oshai.kotlinlogging.KotlinLogging
import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan

class TestJob(
    private val service: TestService,
) {
    private val logger = KotlinLogging.logger { }

    @WithSpan("TestJob.start")
    suspend fun start() {
        val span = Span.current()
        logger.info { "--> Span TestJob.start" }
        logger.info { "Span context valid: ${span.spanContext.isValid}" }
        logger.info { "Span ID: ${span.spanContext.spanId}" }
        logger.info { "Trace ID: ${span.spanContext.traceId}" }
        service.test()
    }
}
