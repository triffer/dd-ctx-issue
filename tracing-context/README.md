# tracing-context

Bridges OpenTelemetry trace context into Kotlin coroutines so that `Span.current()` remains valid across suspension points and thread switches.

## The problem

The Datadog Java agent (with `dd.trace.otel.enabled=true`) stores span context in `ThreadLocal`. When a coroutine suspends on one thread and resumes on another — which happens routinely with `Dispatchers.Default` and `Dispatchers.IO` — the new thread has no span context. `Span.current()` returns an invalid span, and traces break silently.

This affects Flows, `delay`, `withTimeout`, `async`, and any other construct that crosses a suspension point.

The OpenTelemetry Java agent does not have this problem because it instruments `kotlinx-coroutines` directly. The Datadog agent does not.

## The fix

This library provides a single function:

```kotlin
fun tracingContext(): CoroutineContext
```

It captures the current OTel context and returns a `CoroutineContext` element that restores it whenever the coroutine resumes on a new thread. Use it with standard coroutine APIs:

```kotlin
@WithSpan("OrderService.process")
suspend fun process() = withContext(tracingContext()) {
    orderFlow.map { validate(it) }.collect()
}
```

```kotlin
scope.launch(tracingContext()) { doWork() }
```

```kotlin
upstream.map { transform(it) }.flowOn(tracingContext())
```

## When to use it

Any `@WithSpan` suspend function that runs on a multi-threaded dispatcher needs this. If `Span.current()` is called (directly or by a downstream function) after a suspension point, wrap the coroutine block with `tracingContext()`.

## Dependency

```kotlin
implementation("com.example:tracing-context:<version>")
```
