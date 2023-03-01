package study.java2.practice.kafka.core.simple;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SimpleProducer {
  private static final Logger log = LoggerFactory.getLogger(SimpleProducer.class.getName());
  private static final String HOST_NAME = "localhost:9093";
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
    String topicName = "simple-topic";

    // record 설정, data : key-value
    ProducerRecord record = new ProducerRecord(topicName,  "001","hello~kafka");

    // kafka producer -> message -> kafka-server
    Future<RecordMetadata> send = kafkaProducer.send(record);

    RecordMetadata recordMetadata = send.get();
    log.info("#### record metadata receive ####");
    log.info("partition : {}", recordMetadata.partition());
    log.info("offset : {}", recordMetadata.offset());
    log.info("timestamp : {}", recordMetadata.timestamp());

    // 버퍼 플러쉬 / 닫기
    kafkaProducer.flush();
    kafkaProducer.close();

  }
}
