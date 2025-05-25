package org.example;

import java.util.Properties;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerDemoWithCallback {

  private static final Logger log = LoggerFactory.getLogger(
      ProducerDemoWithCallback.class.getSimpleName());

  public static void main(String[] args) {
    log.info("Kafka Producer with Callback");
    // create producer properties
    Properties properties = new Properties();

    // connect to localhost
    properties.setProperty("bootstrap.servers", "127.0.0.1:9092");

    // set producer properties
    properties.setProperty("key.serializer", StringSerializer.class.getName());
    properties.setProperty("value.serializer", StringSerializer.class.getName());

    properties.setProperty("batch.size", "400"); // it's 60kb for default

    // create producer
    KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

    for (int j = 0; j < 10; j++) {

      for (int i = 0; i < 30; i++) {

        //create a producer record
        ProducerRecord<String, String> producerRecord =
            new ProducerRecord<>("notification", "New schedule added");

        // send data <-asynchronous
        producer.send(producerRecord, new Callback() {
          @Override
          public void onCompletion(RecordMetadata metadata, Exception e) {
            // executed every time a record is successfully sent or an exception is thrown
            if (e == null) {
              // the record was successfully sent
              log.info("Received new metatdata \n" +
                  "Topic: " + metadata.topic() + "\n" +
                  "Partition: " + metadata.partition() + "\n" +
                  "Offset: " + metadata.offset() + "\n" +
                  "Timestamp: " + metadata.timestamp());
            } else {
              log.error("Error while producing", e);
            }
          }
        });
      }
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    // tell the producer to send all data and block until done <- synchronous
    producer.flush();

    // flush and close the producer
    producer.close();
  }
}