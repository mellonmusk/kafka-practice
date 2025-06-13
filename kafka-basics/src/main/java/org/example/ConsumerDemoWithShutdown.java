package org.example;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemoWithShutdown {

  private static final Logger log = LoggerFactory.getLogger(
      ConsumerDemoWithShutdown.class.getSimpleName());

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

    // get a reference to the main thread
    final Thread mainThread = Thread.currentThread();

    // adding the shutdown hook
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        log.info("Detected a shutdown, exit by calling consumer.wakeup()");
        consumer.wakeup();
        // join the main thread to allow the execution of the code in the main thread(waits for the code in main thread to be completed)
        try {
          mainThread.join();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });

    try {
      // subscribe to a topic
      consumer.subscribe(Arrays.asList(topic));
      // poll for data
      while (true) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

        for (ConsumerRecord<String, String> record : records) {
          log.info("Key: " + record.key() + ", Value: " + record.value());
          log.info("Partition: " + record.partition() + " ,Offset: " + record.offset());
        }
      }
    } catch (WakeupException e) {
      log.info("Consumer is starting to shut down");
    } catch (Exception e) {
      log.error("Unexpected exception in the consumer", e);
    } finally {
      consumer.close(); // close the consumer, this will also commit offsets
      log.info("The consumer is now gracefully shut down");
    }
  }
}