package study.java2.practice.kafka.core.simple;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class SimpleProducerCustomCB {
  private static final Logger log = LoggerFactory.getLogger(SimpleProducerCustomCB.class.getName());
  private static final String HOST_NAME = "localhost:9093";
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    // 1. KafkaProducer configuration setting
    Properties properties = new Properties();

    // bootstrap.server, key.serializer.class, value.serializer,class
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST_NAME);
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    // KafkaProducer 설정 객체 생성
    KafkaProducer<Integer, String> kafkaProducer = new KafkaProducer<>(properties);

    // 데이터 전송 producer -> kafka server
    // topic name
    String topicName = "simple-topic";

    // record 설정, data : key-value

    // kafka producer -> message -> kafka-server
    for (int i = 0; i < 20; i++) {
      ProducerRecord<Integer, String> record = new ProducerRecord<>(topicName,  i,"hello~kafka " + i);
      kafkaProducer.send(record, (metadata, exception) -> {
        if (!Objects.isNull(exception)) {
          log.error(exception.getMessage());
        } else {
          log.info("{}, {}, {}, {}", metadata.topic(), metadata.offset(), metadata.partition(), metadata.timestamp());
        }
      });
    }

    // 버퍼 플러쉬 / 닫기
    kafkaProducer.flush();
    kafkaProducer.close();

  }
}
