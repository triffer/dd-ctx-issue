package com.example

import io.github.oshai.kotlinlogging.KotlinLogging
import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan

class FixedTestJob(
    private val service: FixedTestService,
) {
    private val logger = KotlinLogging.logger { }

    @WithSpan("FixedTestJob.start")
    suspend fun start() {
        val span = Span.current()
        logger.info { "--> Span FixedTestJob.start" }
        logger.info { "Span context valid: ${span.spanContext.isValid}" }
        logger.info { "Span ID: ${span.spanContext.spanId}" }
        logger.info { "Trace ID: ${span.spanContext.traceId}" }
        service.test()
    }
}
