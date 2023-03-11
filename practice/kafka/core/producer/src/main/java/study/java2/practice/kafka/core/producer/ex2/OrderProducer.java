package study.java2.practice.kafka.core.producer.ex2;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import study.java2.practice.kafka.core.producer.ex2.model.OrderModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Slf4j
public class OrderProducer {
  private static final String TOPIC_NAME = "order-topic";
  private static final String HOST_NAME = "localhost:9092";
  private static final String FILE_PATH = "practice/kafka/core/producer/src/main/resources/pizza-sample";
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  public static void main(String[] args) {
    KafkaProducer<String, OrderModel> producer = new KafkaProducer(getProperties());

    try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH).toAbsolutePath())) {
      reader.lines().forEach(line -> {
        String[] tokens = StringUtils.split(line, ",");
        String key = tokens[0];
        OrderModel orderModel = new OrderModel(tokens[1], tokens[2], tokens[3],
          tokens[4], tokens[5], tokens[6], LocalDateTime.parse(tokens[7].trim(), formatter));
        sendMessage(producer, key, orderModel);
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void sendMessage(KafkaProducer<String, OrderModel> producer, String key, OrderModel value) {
    log.info("key : {}, value : {}", key, value);
    ProducerRecord<String, OrderModel> record = new ProducerRecord<>(TOPIC_NAME, key, value);
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
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, OrderSerializer.class);
    return properties;
  }
}
