# Sample implementation of Context issue

## Issue Missing span context on subsequent invocation
Calling `Span.Current()` inside a function repeatedly executed during Kotlin Flow consumption may not reliably capture the desired tracing context.

### Prerequisites
- run `installDist` Gradle task

### Steps to reproduce 
- run Gradle task `runWithDatadog`

### Excepted Result
- Log output for `TestService.doSomething` has a valid Span context for all invocations

### Actual Result with Datadog Agent
- Log output shows that only the first and second invocation of `TestService.doSomething` has a valid span context

### Actual Result with OTel Agent
- When executed with OTel Agent ( using Gradle task `runWithOtel`) also the first and second invocation of `TestService.doSomething` has a valid span context

---

## Issue Trace/Span ID of Span.current() are different from Trace/Span ID written by LoggingWriter

### Prerequisites
- run `installDist` Gradle task

### Steps to reproduce
- run Gradle task `runWithDatadog`

### Excepted Result
- Log output of LoggingWriter and logs from `com.example.TestJob` and `com.example.TestService` show the same Trace and Span IDs

### Actual Result with Datadog Agent
- Log output shows different IDs for custom logs and DD Agent logs

### Actual Result with OTel Agent
- When executed with OTel Agent ( using Gradle task `runWithOtel`) the Trace and Span IDs match