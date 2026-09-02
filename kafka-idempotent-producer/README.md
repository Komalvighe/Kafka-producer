# Kafka Idempotent Producer - Red Hat AMQ Streams

This is a simple Java 17 + Maven example demonstrating a Kafka idempotent producer.

The important configuration is:

    enable.idempotence=true
    acks=all
    retries=Integer.MAX_VALUE

The example uses the standard Apache Kafka Java client, so it can connect to a Red Hat AMQ Streams / Kafka cluster.

## 1. Prerequisites

Install:

- Java 17+
- Maven 3.8+
- Access to a Red Hat AMQ Streams / Kafka cluster
- A Kafka topic named `orders-topic`

For a local Kafka broker, the default in this example is:

    localhost:9092

For Red Hat AMQ Streams, use the bootstrap server exposed by your Kafka cluster, for example:

    my-cluster-kafka-bootstrap:9092

Use the actual bootstrap address, port, and security configuration from your cluster.

## 2. Create the topic

Example with Kafka CLI:

    bin/kafka-topics.sh --bootstrap-server localhost:9092       --create --topic orders-topic       --partitions 3       --replication-factor 1

For a real Red Hat AMQ Streams production cluster, choose the replication factor according to your cluster configuration.

Verify:

    bin/kafka-topics.sh --bootstrap-server localhost:9092       --describe --topic orders-topic

## 3. Build

From this project directory:

    mvn clean package

## 4. Run against local Kafka

    mvn exec:java

The program sends 5 records to `orders-topic`.

## 5. Run against Red Hat AMQ Streams

Set the bootstrap server:

Linux/macOS:

    export KAFKA_BOOTSTRAP_SERVERS="my-cluster-kafka-bootstrap:9092"

Then:

    mvn exec:java

If your Red Hat AMQ Streams listener uses TLS/SASL, this simple example must also be configured with the required security properties/certificates. Do not use plaintext settings against a TLS/SASL listener.

## 6. Verify messages

Run a console consumer:

    bin/kafka-console-consumer.sh       --bootstrap-server localhost:9092       --topic orders-topic       --from-beginning       --property print.key=true

You should see 5 records similar to:

    order-1    Order 1 from idempotent producer
    order-2    Order 2 from idempotent producer
    order-3    Order 3 from idempotent producer
    order-4    Order 4 from idempotent producer
    order-5    Order 5 from idempotent producer

## 7. Important interview explanation

Why is this producer idempotent?

The producer is configured with:

    enable.idempotence=true

Kafka uses a producer identity (PID) and per-partition sequence numbers to detect duplicate producer retries.

If an acknowledgement is lost and the producer retries the same record, the broker can recognize the duplicate sequence and avoid appending the same record again.

Important:

Idempotent producer != end-to-end exactly-once processing.

Idempotence protects Kafka producer writes from duplicate retries. If the application also updates an external database or calls an external API, those side effects need their own idempotency/transaction strategy.

## 8. Useful interview scenario

Suppose:

    Producer -> Kafka

The producer sends Order-101.

Kafka stores it, but the network drops the acknowledgement.

The producer retries Order-101.

With idempotence enabled, Kafka can identify the retry using the producer ID and sequence number, preventing a duplicate append.

Conceptually:

    First request:
    PID=123, Sequence=10 -> Order-101 -> stored

    Retry:
    PID=123, Sequence=10 -> Order-101 -> duplicate detected

## 9. Red Hat AMQ Streams note

For a Red Hat AMQ Streams deployment, the Java code does not need to use a Red Hat-specific producer API. AMQ Streams is Kafka-compatible, so the standard `org.apache.kafka:kafka-clients` API is used.

For a secured Red Hat listener, add the appropriate security properties such as:

    security.protocol
    sasl.mechanism
    sasl.jaas.config

or TLS-related properties, based on how the listener is configured.

Do not copy security credentials into source code. Prefer environment variables, Kubernetes Secrets, or another secure configuration mechanism.
