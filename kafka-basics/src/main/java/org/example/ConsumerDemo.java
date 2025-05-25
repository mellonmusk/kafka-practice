package org.example;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemo {

  private static final Logger log = LoggerFactory.getLogger(ConsumerDemo.class.getSimpleName());

  public static void main(String[] args) {
    log.info("Kafka Consumer");

    String groupId = "test-application";
    String topic = "notification";

    // create consumer properties
    Properties properties = new Properties();

    // connect to localhost
    properties.setProperty("bootstrap.servers", "127.0.0.1:9092");

    // set consumer properties
    properties.setProperty("key.deserializer", StringDeserializer.class.getName());
    properties.setProperty("value.deserializer", StringDeserializer.class.getName());

    properties.setProperty("group.id", groupId);

    // none: if we don't have any consumer group, then we fail - we must set a consumer group before starting our application
    // earliest: read from the beginning of the topic - which is the entire history
    // latest: read new messages starting from now on
    properties.setProperty("auto.offset.reset", "earliest");

    //create consumer
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

    // subscribe to a topic
    consumer.subscribe(Arrays.asList(topic));

    // poll for data
    while (true) {
      log.info("Polling");
      ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

      for (ConsumerRecord<String, String> record : records) {
        log.info("Key: " + record.key() + ", Value: " + record.value());
        log.info("Partition: " + record.partition() + " ,Offset: " + record.offset());
      }
    }

  }
}