package com.example.tracing

import io.opentelemetry.context.Context
import io.opentelemetry.extension.kotlin.asContextElement
import kotlin.coroutines.CoroutineContext

/**
 * Returns the current OTel trace context as a [CoroutineContext] element
 * that preserves span context across coroutine suspension points and
 * thread switches.
 *
 * Compose with standard coroutine APIs:
 * ```
 * withContext(tracingContext()) { ... }
 * flow.flowOn(tracingContext())
 * scope.launch(tracingContext()) { ... }
 * ```
 *
 * This is needed when using the Datadog agent with `dd.trace.otel.enabled=true`,
 * which stores span context in [ThreadLocal]. Without this, [io.opentelemetry.api.trace.Span.current]
 * returns an invalid span after a coroutine resumes on a different thread.
 */
fun tracingContext(): CoroutineContext =
    Context.current().asContextElement()
