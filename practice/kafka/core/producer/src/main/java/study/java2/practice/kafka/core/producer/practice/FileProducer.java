package study.java2.practice.kafka.core.producer.practice;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import study.java2.practice.kafka.core.producer.ex1.PizzaMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class FileProducer {
  private static final Logger log = LoggerFactory.getLogger(FileProducer.class);
  private static final String TOPIC_NAME = "file-topic";
  private static final String HOST_NAME = "localhost:9093";

  private static final String FILE_PATH = "practice/kafka/core/producer/src/main/resources/pizza-sample";

  public static void main(String[] args) {
    // KafkaProducer 생성 -> ProducerRecords 생성 -> send() 비동기 방식 전송
    KafkaProducer<String, String> producer = new KafkaProducer(getProperties());

    try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH).toAbsolutePath())) {
      reader.lines().forEach(line -> {
        String[] split = StringUtils.split(line, ",", 2);
        String key = split[0];
        String value = split[1];
        sendMessage(producer, key, value);
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void sendMessage(KafkaProducer<String, String> producer, String key, String value) {
    log.info("key : {}, value : {}", key, value);
    ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, key, value);
    producer.send(record, (metadata, exception) -> {
      if (exception == null) {
        log.info("partition : {}, offset: {}, timestamp : {} "
          , metadata.partition(), metadata.offset(), metadata.timestamp());
      } else {
        log.error(exception.getMessage());
      }
    });
  }


  private static Properties getProperties() {
    Properties properties = new Properties();
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST_NAME);
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return properties;
  }
}
