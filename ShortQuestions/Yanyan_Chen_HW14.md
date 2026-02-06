# Distributed Systems – Short Answer Questions

---

## 1. CAP Theorem: Why is Partition Tolerance (P) mandatory?

In a real distributed system, **network partitions are unavoidable**. Machines can crash, packets can be delayed or dropped, switches can fail, and data centers can lose connectivity. Because these failures are outside the control of the system designer, **Partition Tolerance (P) is not a choice but a requirement**.

Given that P is mandatory, the real trade-off is between **Consistency (C)** and **Availability (A)** **during a partition**:
- **CP systems** sacrifice availability to guarantee consistency (they may reject requests).
- **AP systems** sacrifice immediate consistency to remain available (they may return stale data).

---

## 2. Linearizability vs Eventual Consistency

**Linearizability**
- A strong consistency model.
- Every operation appears to take effect atomically at a single point in time.
- Reads always see the most recent write.
- Example: a single-leader database like a strongly consistent key-value store used for banking balances.

**Eventual Consistency**
- A weak consistency model.
- If no new updates occur, all replicas will eventually converge.
- Reads may return stale data temporarily.
- Example: DNS or a shopping cart system where updates propagate asynchronously.

---

## 3. Vector Clocks

A **vector clock** is a data structure used to track **causal relationships** between events in a distributed system.

- Each node maintains a vector of counters (one per node).
- On a local event, the node increments its own counter.
- On message send, the vector clock is attached.
- On receive, the receiver merges clocks by taking element-wise maximums.

**Causality rules**
- Event A → Event B if all entries in A’s clock ≤ B’s clock and at least one is strictly less.
- If neither clock dominates the other, the events are **concurrent**.

**Why not physical timestamps?**
- Clocks are not perfectly synchronized.
- Clock skew and drift can cause causally later events to appear earlier.
- Physical time cannot reliably capture causality.

---

## 4. Split-Brain in Single-Leader Replication

**Split-brain** occurs during failover when **two nodes both believe they are the leader**.

Why it is dangerous:
- Both leaders accept writes independently.
- Replicas diverge and data becomes inconsistent.
- Conflicting updates may overwrite each other, causing permanent data loss.

Preventing split-brain usually requires **quorum-based leader election**.

---

## 5. Quorum Condition (W + R > N)

In leaderless (Dynamo-style) replication:
- **N** = total replicas
- **W** = number of replicas that must acknowledge a write
- **R** = number of replicas contacted for a read

The condition **W + R > N** ensures that **at least one replica involved in a read has the latest write**, providing strong consistency for reads.

If the condition is not satisfied:
- Reads may miss recent writes.
- Clients can observe stale or conflicting data.

---

## 6. Two-Phase Commit (2PC) vs Saga Pattern

**Two-Phase Commit (2PC)**
- Guarantees atomicity across multiple nodes.
- Has a coordinator that blocks participants during failure.
- Main weakness: **blocking and poor fault tolerance**.

**Saga Pattern**
- Breaks a transaction into a sequence of local transactions.
- Each step has a compensating action.
- Avoids blocking and single points of failure.

**Trade-off**
- Saga avoids blocking and improves availability.
- Saga **sacrifices atomicity** and allows intermediate inconsistent states.

---

## 7. Raft: Log Up-to-Date Requirement

A Raft candidate must have a log that is **at least as up-to-date as the majority** to win an election.

This guarantees the **Leader Completeness Property**:
- Once an entry is committed, it will appear in the logs of all future leaders.

Without this rule:
- A leader with stale logs could overwrite committed entries.
- The system would violate safety.

---

## 8. CRDT and G-Counter

A **CRDT (Conflict-free Replicated Data Type)** is a data structure that **converges automatically** under concurrent updates without coordination.

**G-Counter**
- Each replica keeps a local counter.
- A replica can only increment its own counter.
- The global value is the sum of all counters.
- Merging is done by taking the maximum value per replica.

It always converges because:
- Updates are monotonic.
- Merge operations are associative, commutative, and idempotent.

---

## 9. Circuit Breaker Pattern

The Circuit Breaker prevents cascading failures by stopping calls to unhealthy services.

**Three states**
- **Closed**: requests flow normally.
- **Open**: requests fail fast without calling the service.
- **Half-Open**: limited requests are allowed to test recovery.

This prevents:
- Resource exhaustion
- Thread pool starvation
- Failure amplification across services

---

## 10. Range-Based vs Hash-Based Partitioning

**Range-Based Partitioning**
- Data is split by key ranges.
- Supports range queries efficiently.
- Suffers from hotspots if keys are skewed.

**Hash-Based Partitioning**
- Keys are hashed to partitions.
- Provides uniform distribution.
- Range queries are inefficient.

**Consistent Hashing**
- Solves the rebalancing problem of hash-mod-N.
- When nodes are added or removed, only a small fraction of keys move.
- Improves scalability and stability compared to simple modulo hashing.

---
