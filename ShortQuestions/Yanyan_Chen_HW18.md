
### Topic
A **Topic** is a logical category in Kafka where messages are published by producers and consumed by consumers.  
It acts as a named stream of records.

Each topic can be divided into multiple partitions for scalability and parallel processing.

---

### Partition
A **Partition** is a subset of a topic.  

Each partition:
- Is an ordered, immutable sequence of records
- Assigns a unique sequential ID (offset) to each message
- Enables parallel consumption

Partitions are distributed across brokers.

---

### Broker
A **Broker** is a Kafka server instance.

A Kafka cluster consists of multiple brokers that:
- Store topic partitions
- Handle producer write requests
- Handle consumer read requests
- Replicate partitions for fault tolerance

Each partition has:
- One leader broker
- Zero or more follower replicas

---

### Producer
A **Producer** sends messages to a Kafka topic.

Producers:
- Choose the topic
- Optionally choose the partition
- Can configure delivery guarantees (at-most-once, at-least-once, exactly-once)

---

### Consumer
A **Consumer** reads messages from a topic.

Consumers:
- Subscribe to one or more topics
- Pull messages from brokers
- Track progress using offsets

---

### Consumer Group
A **Consumer Group** is a group of consumers sharing the same group ID.

Rules:
- Each partition is assigned to only one consumer within the group
- Different consumer groups consume the same topic independently
- Enables load balancing and fault tolerance

---

### Offset
An **Offset** is a unique sequential identifier assigned to each message within a partition.

Offsets:
- Track consumer progress
- Allow replay of messages
- Prevent duplicate processing when properly committed

Offsets are stored in Kafka’s internal topic: `__consumer_offsets`.

---

### Zookeeper
In older Kafka versions, **Zookeeper** is used for:

- Broker coordination
- Leader election
- Cluster metadata management
- Topic configuration

(Newer Kafka versions use KRaft instead of Zookeeper.)

---

### 1. Given N (partitions) and M (consumers)

#### Case 1: N >= M

- Each consumer gets at least one partition
- Some consumers may handle multiple partitions
- System operates normally

#### Case 2: N < M

- Only N consumers will be assigned partitions
- Extra consumers remain idle
- Because a partition can only be consumed by one consumer within a group

---

### 2. How do brokers work with topics?

- Topics are divided into partitions
- Partitions are distributed across brokers
- Each partition has a leader broker
- Producers and consumers communicate with the leader
- Followers replicate data for fault tolerance

This design ensures scalability and high availability.

---

### 3. Are messages pushed or pulled?

Kafka uses a **pull model**.

Consumers pull messages from brokers instead of brokers pushing messages.

Advantages:
- Consumers control their own pace
- Better backpressure handling
- Improved scalability

---

### 4. How to avoid duplicate message consumption?

Methods:

- Disable auto-commit and commit offsets manually after processing
- Ensure idempotent consumer logic
- Use idempotent producer configuration
- Use transactional producers (for exactly-once semantics)

---

### 5. What happens if some consumers are down in a consumer group?

Kafka triggers a **rebalance**:

- Partitions from failed consumers are reassigned
- No data loss occurs
- Offsets are preserved

As long as replication is configured properly, fault tolerance is maintained.

---

### 6. What happens if an entire consumer group is down?

No data loss occurs.

Kafka retains messages based on retention policy (time or size).

When the group restarts:
- It resumes from the last committed offset

---

### 7. What is consumer lag? How to resolve it?

Consumer Lag =  
Latest partition offset − Consumer committed offset

Causes:
- Slow processing logic
- Too few partitions
- Insufficient consumer instances
- High message throughput

Solutions:
- Increase partition count
- Increase consumer concurrency
- Optimize processing logic
- Scale horizontally

---

### 8. How does Kafka track message delivery?

Kafka tracks delivery using:

- Offsets per partition per consumer group
- Stored in `__consumer_offsets` internal topic

Each consumer group maintains independent offset tracking.

---

### 9. Compare Kafka vs RabbitMQ and Kafka vs MySQL

#### Kafka vs RabbitMQ

| Feature | Kafka | RabbitMQ |
|----------|--------|----------|
| Architecture | Distributed log | Message broker |
| Scalability | Very high | Moderate |
| Throughput | Very high | Medium |
| Message Replay | Supported | Limited |
| Use Case | Event streaming | Traditional messaging |

Kafka is better suited for:
- Event-driven systems
- Big data pipelines
- Real-time analytics
- High-throughput streaming

---

#### Kafka vs MySQL

MySQL:
- Relational database
- Used for structured transactional data storage

Kafka:
- Distributed streaming platform
- Used for real-time event pipelines
- Supports decoupled microservices

Kafka is preferred when:
- High throughput is required
- Real-time data streaming is needed
- Systems require event sourcing or message replay
- Loose coupling between services is desired


- Configurable delivery guarantees

It is widely used in modern microservice architectures and real-time data systems.

---
### 10

## 1. Three Consumers in One Consumer Group

### Configuration

In `application.properties`:

```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=consumer_group_1
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.enable-auto-commit=false
```

Kafka listener:

```java
@KafkaListener(
    topics = "${kafka.topic.name}",
    groupId = "consumer_group_1",
    concurrency = "3"
)
public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
    System.out.println("Thread: " + Thread.currentThread().getName()
        + " | Partition: " + record.partition()
        + " | Offset: " + record.offset());
    ack.acknowledge();
}
```

### Observation

- Three consumers run in the same group.
- Each partition is assigned to only one consumer.
- Messages are distributed based on partition assignment.
- Maximum parallel consumption equals the number of partitions.

---

## 2. Increase Number of Consumers in Same Group

Changed:

```java
concurrency = "5"
```

### Observation

- If the topic has 3 partitions and 5 consumers:
  - Only 3 consumers actively consume messages.
  - Remaining consumers stay idle.
- Kafka triggers a rebalance when consumer count changes.
- Active consumer count cannot exceed partition count.

---

## 3. Multiple Consumer Groups and Offset Observation

Created two groups:

- consumer_group_A (concurrency = 2)
- consumer_group_B (concurrency = 1)

Example:

```java
@KafkaListener(topics = "chuwa-yyds", groupId = "consumer_group_A", concurrency = "2")
public void consumeA(...) { }

@KafkaListener(topics = "chuwa-yyds", groupId = "consumer_group_B", concurrency = "1")
public void consumeB(...) { }
```

Checked offsets:

```bash
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group consumer_group_A
kafka-consumer-groups --describe --group consumer_group_B
```

### Observation

- Each group consumes the same topic independently.
- Offsets are tracked separately for each group.
- Stopping one group does not affect the other.

---

## 4. Message Delivery Guarantees

### At-most-once

```properties
spring.kafka.consumer.enable-auto-commit=true
```

Observation:
- Offsets may be committed before processing completes.
- Messages may be lost if a crash occurs.

---

### At-least-once

```properties
spring.kafka.consumer.enable-auto-commit=false
spring.kafka.listener.ack-mode=record
```

Manual commit:

```java
ack.acknowledge();
```

Observation:
- Messages are re-delivered if crash occurs before commit.
- Duplicate processing is possible.

---

### Exactly-once

Producer:

```properties
spring.kafka.producer.acks=all
spring.kafka.producer.properties.enable.idempotence=true
spring.kafka.producer.transaction-id-prefix=tx-
```

Consumer:

```properties
spring.kafka.consumer.properties.isolation.level=read_committed
```

Observation:
- Prevents duplicates when properly configured.
- Requires transactional producer setup.

---

## 5. Partitioning Logic

### Default Behavior

- With key → Kafka hashes key to partition.
- Same key always goes to same partition.
- Ensures ordering per key.

---

### Custom Partitioner

```java
public class MyPartitioner implements Partitioner {
    @Override
    public int partition(String topic, Object key,
                         byte[] keyBytes, Object value,
                         byte[] valueBytes, Cluster cluster) {
        int partitions = cluster.partitionCountForTopic(topic);

        if (key != null && key.toString().startsWith("vip")) {
            return 0;
        }

        return Math.abs(key.hashCode()) % partitions;
    }
}
```

Configured in:

```properties
spring.kafka.producer.properties.partitioner.class=com.example.MyPartitioner
```

### Observation

- Custom rules control partition selection.
- Partition assignment determines which consumer processes the message.

