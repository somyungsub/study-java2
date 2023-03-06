package study.java2.practice.kafka.core.producer.simple;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SimpleMultiBrokerProducer {
  private static final Logger log = LoggerFactory.getLogger(SimpleMultiBrokerProducer.class.getName());
  private static final String HOST_NAME = "localhost:9093,localhost:9094,localhost:9095";
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    // 1. KafkaProducer configuration setting
    Properties properties = new Properties();

    // bootstrap.server, key.serializer.class, value.serializer,class
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST_NAME);
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    // KafkaProducer 설정 객체 생성
    KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);

    // 데이터 전송 producer -> kafka server
    // topic name
    String topicName = "m-brk-topic-replica";

    for (int i = 0; i < 10000; i++) {
      ProducerRecord record = new ProducerRecord(topicName,  String.valueOf(i),"hello~kafka 2 : " + (i+1000));
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
