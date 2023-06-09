package study.java2.practice.kafka.core.consumer.simple;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class ConsumerMultiTopicRebalance {
  private static final Logger log = LoggerFactory.getLogger(ConsumerMultiTopicRebalance.class.getName());
  private static final String HOST_NAME = "localhost:9093";
  public static void main(String[] args) {
    // key,value
    KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(createProperties());
    // topic 구독
    kafkaConsumer.subscribe(getTopicNames());

    Thread mainThread = Thread.currentThread();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      log.info("main program starts to exit by calling wakeup");
      kafkaConsumer.wakeup();
      try {
        mainThread.join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }));

    try {
      while (true) {
        // 메시지 가져오기, 1초대기
        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(1000));
        records.iterator().forEachRemaining(record -> {
          log.info("topic:{}, key : {}, partition: {}, offset : {}, value : {}",
            record.topic(), record.key(), record.partition(), record.offset(), record.value());
        });

      }
    } catch (WakeupException e) {
      log.error("wakeup exception~");
    } finally {
      log.info("finally consumer close~");
      kafkaConsumer.close();
    }
  }

  private static Properties createProperties() {

    Properties properties = new Properties();
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST_NAME);
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-mtopic");
    properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "60000");
//    properties.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "1"); // static group membership
//    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
//    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // offset 마지막 시작점 부터

    // 파티션 할당전략. 디폴트 range
//    properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, RoundRobinAssignor.class.getName());
//    properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());

    /*
      * ConsumerConfig.FETCH_MIN_BYTES_CONFIG : Fetcher가 record들을 읽어 들이는 최소 bytes. 기본은 16K
      ConsumerConfig.FETCH_MAX_BYTES_CONFIG :  Fetcher가 한번에 가져올 수 있는 최대 bytes. 기본 50MB
      ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG  메시지 쌓일때까지 대기 시간.기본 500ms
      * ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG : 패쳐가 파티션별 한번에 최대로 가져올 수 있는 bytes. 1MB
      ConsumerConfig.MAX_POLL_RECORDS_CONFIG : 패쳐가 한번에 가져 올 수 있는 레코드수, 기본 500개
      ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG : 전략. range, round robin, sticky
     */

    return properties;
  }

  private static List<String> getTopicNames() {
    // topic 다건 구독
    return List.of("topic-p3-t1", "topic-p3-t2");
  }
}
