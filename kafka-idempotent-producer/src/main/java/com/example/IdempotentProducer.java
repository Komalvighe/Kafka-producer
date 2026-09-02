package com.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import java.util.concurrent.ExecutionException;

import java.util.Properties;

public class IdempotentProducer {

    public static void main(String[] args) {

        String bootstrapServers =
                System.getenv().getOrDefault(
                        "KAFKA_BOOTSTRAP_SERVERS",
                        "localhost:9092"
                );

        String topic =
                System.getenv().getOrDefault(
                        "KAFKA_TOPIC",
                        "orders-topic"
                );

        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        props.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer"
        );

        props.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer"
        );

        // Enable idempotent producer.
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        // Strong acknowledgement.
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        // Keep retries enabled so the producer can safely retry failed requests.
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));

        // Example tuning values.
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

        try (KafkaProducer<String, String> producer =
                     new KafkaProducer<>(props)) {

            for (int i = 1; i <= 5; i++) {

                String key = "order-" + i;
                String value = "Order " + i + " from idempotent producer";

                ProducerRecord<String, String> record =
                        new ProducerRecord<>(topic, key, value);

              //  RecordMetadata metadata = producer.send(record).get();
             /*   try {
                RecordMetadata metadata = producer.send(record).get();

                System.out.println("Topic: " + metadata.topic());
                System.out.println("Partition: " + metadata.partition());
                System.out.println("Offset: " + metadata.offset());
                } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                e.printStackTrace();
                }


                System.out.printf(
                        "Sent key=%s, topic=%s, partition=%d, offset=%d%n",
                        key,
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset()
                ); */

                try {
        RecordMetadata metadata = producer.send(record).get();

        System.out.printf(
                "Sent key=%s, topic=%s, partition=%d, offset=%d%n",
                key,
                metadata.topic(),
                metadata.partition(),
                metadata.offset()
        );

        } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        System.err.println("Producer thread was interrupted.");
        break;

    } catch (ExecutionException e) {
        System.err.println("Failed to send record: " + e.getCause());
    }
            }

            producer.flush();
        }
    }
}
