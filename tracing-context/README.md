# tracing-context

Keeps `Span.current()` working across suspension points in Kotlin coroutines when using the Datadog Java agent.

## The problem

The Datadog Java agent (with `dd.trace.otel.enabled=true`) stores span context in a `ThreadLocal`. When a coroutine
suspends on one thread and resumes on another (common with `Dispatchers.Default` and `Dispatchers.IO`) the new thread
has no span context. `Span.current()` returns an invalid span, and traces break silently.

This affects Flows, `async`, `withTimeout`, `delay`, and anything else that crosses a suspension point.

The OTel Java agent doesn't have this problem because it instruments `kotlinx-coroutines` directly. The Datadog agent
doesn't.

## The fix

One function:

```kotlin
fun tracingContext(): CoroutineContext
```

It captures the current OTel context and returns a `CoroutineContext` element that restores it when the coroutine
resumes on a new thread.

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

Any `@WithSpan` suspend function on a multi-threaded dispatcher. If `Span.current()` is called after a suspension
point — directly or by a downstream function — wrap the coroutine block with `tracingContext()`.
