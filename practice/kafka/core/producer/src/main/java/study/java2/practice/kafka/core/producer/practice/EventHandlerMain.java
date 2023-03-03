package study.java2.practice.kafka.core.producer.practice;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import study.java2.practice.kafka.core.producer.event.FileEventHandler;
import study.java2.practice.kafka.core.producer.event.MessageEvent;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class EventHandlerMain {
  private static final Logger log = LoggerFactory.getLogger(EventHandlerMain.class);
  private static final String TOPIC_NAME = "file-topic";
  private static final String HOST_NAME = "localhost:9093";

  private static final String FILE_PATH = "practice/kafka/core/producer/src/main/resources/pizza-sample";
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    KafkaProducer<String, String> producer = new KafkaProducer<>(getProperties());
    FileEventHandler fileEventHandler = new FileEventHandler(producer, TOPIC_NAME, true);
    fileEventHandler.onMessage(new MessageEvent("key0001", "this is test message value"));
  }

  private static Properties getProperties() {
    Properties properties = new Properties();
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST_NAME);
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return properties;
  }
}
