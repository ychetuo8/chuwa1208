## Question 1: Multi-Threading vs Async (Non-Blocking I/O)

### Multi-Threading

Multi-threading is most appropriate when tasks are **CPU-bound** or when
blocking operations cannot be avoided.

Use multi-threading when:

-   The workload is computation-heavy.
-   You need real parallel execution across multiple CPU cores.
-   You depend on blocking libraries or legacy APIs.
-   The program logic benefits from a simple sequential structure.

**Typical scenarios**

-   Image or video processing\
-   Data compression or encryption\
-   Scientific computing or ML preprocessing\
-   Batch ETL jobs\
-   Calling blocking SDKs (e.g., payment gateways)

------------------------------------------------------------------------

### Async / Non-Blocking I/O

Async programming is more suitable for **I/O-bound workloads** where
tasks spend most of their time waiting for external resources.

Use async when:

-   The application handles large numbers of concurrent requests.
-   Most time is spent waiting on network, disk, or database responses.
-   You want to maximize socket utilization without blocking OS threads.
-   You are using event-loop or future-based frameworks.

**Typical scenarios**

-   High-concurrency web servers\
-   API gateways\
-   WebSocket or chat servers\
-   Distributed crawlers

------------------------------------------------------------------------

### Practical Guideline

-   CPU-intensive tasks → scale with core count (thread-per-core
    strategy).\
-   I/O-intensive tasks → use async I/O or lightweight concurrency to
    avoid one-thread-per-connection overhead.

------------------------------------------------------------------------

## Question 2: Virtual Threads (Java 21)

### Overview

Virtual Threads (introduced in Java 21 via Project Loom) are lightweight
threads managed by the JVM rather than directly mapped one-to-one to
operating system threads.

They allow developers to write blocking-style code while maintaining
high scalability.

------------------------------------------------------------------------

### Key Differences

**Platform Threads** - One-to-one mapping with OS threads\
- High memory cost\
- Limited scalability

**Virtual Threads** - Managed by the JVM scheduler\
- Extremely lightweight\
- Can scale to hundreds of thousands or more\
- Suspended automatically during blocking operations

------------------------------------------------------------------------

### Suitable Use Cases

Virtual threads are ideal for:

-   Request-per-thread server models\
-   Applications with heavy blocking I/O\
-   Migrating from synchronous code without rewriting to async

------------------------------------------------------------------------

### Limitations

Be cautious when:

-   Tasks are purely CPU-bound\
-   Blocking occurs inside long synchronized sections\
-   ThreadLocal-heavy designs are used without lifecycle awareness

------------------------------------------------------------------------

## Question 3: Lock vs CAS

### Lock-Based Synchronization

Locks (e.g., `synchronized`, `ReentrantLock`) ensure mutual exclusion.

Advantages:

-   Clear and simple reasoning model\
-   Supports complex invariants\
-   Allows condition variables and fairness policies

Disadvantages:

-   Thread blocking and context switching\
-   Risk of deadlock\
-   Reduced scalability under contention

------------------------------------------------------------------------

### CAS (Compare-And-Swap)

CAS is a hardware-level atomic operation used in lock-free programming.

Advantages:

-   Non-blocking\
-   Efficient under low contention\
-   Enables high-performance concurrent structures

Disadvantages:

-   More complex logic\
-   Retry loops under contention\
-   Susceptible to ABA problem\
-   Typically limited to single-variable updates

------------------------------------------------------------------------

### Selection Strategy

-   Use locks for complex state management.\
-   Use CAS for simple atomic updates in performance-critical paths.

------------------------------------------------------------------------

## Question 4: Deadlock Conditions and Prevention

### Four Necessary Conditions

A deadlock requires all of the following:

1.  Mutual exclusion\
2.  Hold and wait\
3.  No preemption\
4.  Circular wait

------------------------------------------------------------------------

### Lock Ordering Strategy

By defining a global lock acquisition order and requiring all threads to
follow it, circular wait is eliminated, preventing deadlock.

------------------------------------------------------------------------

## Question 5: Thread Pool Sizing

### General Formula

    Threads ≈ NCPU × UCPU × (1 + W/C)

Where:

-   **NCPU** = number of processor cores\
-   **UCPU** = target CPU utilization\
-   **W** = waiting time\
-   **C** = compute time

------------------------------------------------------------------------

### Example A: CPU-Bound (8 cores)

If wait time is negligible:

    Threads ≈ 8

------------------------------------------------------------------------

### Example B: I/O-Bound (100ms wait, 5ms compute)

    W/C = 100 / 5 = 20
    Threads ≈ 8 × (1 + 20) = 168

------------------------------------------------------------------------

## Question 6: Exponential Backoff with Jitter

### Concept

Exponential backoff increases retry delay exponentially over time.\
Jitter introduces randomness to each retry delay.

------------------------------------------------------------------------

### Why Jitter Matters

Without jitter, retries synchronize and create retry storms.\
Jitter distributes retries over time, improving system stability and
recovery.

------------------------------------------------------------------------

## Question 7: Circuit Breaker Pattern

### CLOSED

Requests proceed normally. Failures are monitored.\
If failure threshold is exceeded → transition to OPEN.

------------------------------------------------------------------------

### OPEN

Requests fail immediately without hitting the downstream service.\
After a timeout → transition to HALF-OPEN.

------------------------------------------------------------------------

### HALF-OPEN

A limited number of test requests are allowed.\
If successful → return to CLOSED.\
If failures persist → return to OPEN.

------------------------------------------------------------------------

## Question 8: Exactly-Once Semantics

### Why Exactly-Once Delivery Is Impossible

Due to network failures:

-   Acknowledgments may be lost.
-   The sender may retry, causing duplicates.
-   If no retry occurs, data loss may happen.

It is impossible to guarantee both zero loss and zero duplication under
all failure scenarios.

------------------------------------------------------------------------

### Achieving Exactly-Once Processing

Common strategies:

-   Idempotency keys\
-   Deduplication storage\
-   Atomic state and offset commit\
-   Transactional outbox pattern

------------------------------------------------------------------------

## Question 9: Caching Strategies

### Cache-Aside

Application loads data on cache miss and writes directly to DB,
invalidating cache afterward.

Pros: simple\
Cons: stale data risk, cache stampede risk

------------------------------------------------------------------------

### Write-Through

Writes go to cache and database synchronously.

Pros: strong read consistency\
Cons: slower write performance

------------------------------------------------------------------------

### Write-Behind

Writes update cache immediately and persist to DB asynchronously.

Pros: fast writes\
Cons: possible data loss, higher complexity

------------------------------------------------------------------------

## Question 10: Cache Stampede

### Definition

Occurs when many requests simultaneously miss the cache and overload the
database.

------------------------------------------------------------------------

### Mitigation Techniques

1.  Single-flight or per-key mutex\
2.  Staggered expiration or proactive refresh
