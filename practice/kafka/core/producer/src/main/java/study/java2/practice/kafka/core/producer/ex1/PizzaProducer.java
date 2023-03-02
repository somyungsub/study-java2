package study.java2.practice.kafka.core.producer.ex1;

import com.github.javafaker.Faker;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class PizzaProducer {
  public static final Logger logger = LoggerFactory.getLogger(PizzaProducer.class.getName());

  public static void main(String[] args) {

    String topicName = "pizza-topic2";

    //KafkaProducer configuration setting
    // null, "hello world"

    Properties props  = new Properties();
    props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
    props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//    props.setProperty(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "50000");  // 전송 타임아웃
//    props.setProperty(ProducerConfig.ACKS_CONFIG, "0"); // all : 디폴트(레플리카까지 전부 복제된 후 응답, 가장 안정적,중복허용), 1 : 중복허용전송 , 0 : 최대한번 전송
//    props.setProperty(ProducerConfig.PARTITIONER_CLASS_CONFIG, "");

    /*
      정확히한번 전송 (트랜잭션 기반 전송), 중복 없이 -> 주로 카프카 스트림에서 사용
      트랜잰션기반전송 : consumer -> process -> producer 의 .. 주로 카프카스트림
      1. 최대 한번 전송 : ack=0, ack 응답이 없어도 메시지 보냄
      2. 적어도 한번 전송 : ack=1, ack 를 받은 다음에 다음메시지 전송. 메시지 소실은 없지만 중복 전송을 할 수 있음 (네트워크 장애등)
      3. 중복 없이 전송 : ack=all(-1), ack를 받은 다음에 다음메시지 전송. 브로커에서 메시지가 중복될 경우, 메시지로그에 기록하지 않고 ack만 보냄
        - enable.idempotence=true, retries > 0, max.in.flight.requests.per.connection은 1~5값
        - 성능은 감소하나, 안정성 확보
        - config 잘못 설정하면 오류 or 기동안되거나 정상적으로 기능처리 못함, 기본설정그대로 쓰면 되긴함
        - ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG
        - ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION
        - ProducerConfig.RETRIES_CONFIG
     */

    //batch setting
//    props.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, "32000"); // 배치 사이즈
//    props.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");     // 수집시간
//    props.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "20");     // 메시지 배치의 개수

    //KafkaProducer object creation
    KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(props);
    sendPizzaMessage(kafkaProducer, topicName, -1, 500, 0, 0, false);
    kafkaProducer.close();

  }


  public static void sendPizzaMessage(KafkaProducer<String, String> kafkaProducer, String topicName, int iterCount,
                                      int interIntervalMillis, int intervalMillis,
                                      int intervalCount, boolean sync) {

    PizzaMessage pizzaMessage = new PizzaMessage();
    int iterSeq = 0;
    long seed = 2022;
    Random random = new Random(seed);
    Faker faker = Faker.instance(random);

    long startTime = System.currentTimeMillis();

    while( iterSeq++ != iterCount ) {
      HashMap<String, String> pMessage = pizzaMessage.produce_msg(faker, random, iterSeq);
      ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName,
        pMessage.get("key"), pMessage.get("message"));
      sendMessage(kafkaProducer, producerRecord, pMessage, sync);

      if((intervalCount > 0) && (iterSeq % intervalCount == 0)) {
        try {
          logger.info("####### IntervalCount:" + intervalCount +
            " intervalMillis:" + intervalMillis + " #########");
          Thread.sleep(intervalMillis);
        } catch (InterruptedException e) {
          logger.error(e.getMessage());
        }
      }

      if(interIntervalMillis > 0) {
        try {
          logger.info("interIntervalMillis:" + interIntervalMillis);
          Thread.sleep(interIntervalMillis);
        } catch (InterruptedException e) {
          logger.error(e.getMessage());
        }
      }

    }
    long endTime = System.currentTimeMillis();
    long timeElapsed = endTime - startTime;

    logger.info("{} millisecond elapsed for {} iterations", timeElapsed, iterCount);

  }

  public static void sendMessage(KafkaProducer<String, String> kafkaProducer,
                                 ProducerRecord<String, String> producerRecord,
                                 HashMap<String, String> pMessage, boolean sync) {
    if(!sync) {
      kafkaProducer.send(producerRecord, (metadata, exception) -> {
        if (exception == null) {
          logger.info("async message:" + pMessage.get("key") + " partition:" + metadata.partition() +
            " offset:" + metadata.offset());
        } else {
          logger.error("exception error from broker " + exception.getMessage());
        }
      });
    } else {
      try {
        RecordMetadata metadata = kafkaProducer.send(producerRecord).get();
        logger.info("sync message:" + pMessage.get("key") + " partition:" + metadata.partition() +
          " offset:" + metadata.offset());
      } catch (ExecutionException e) {
        logger.error(e.getMessage());
      } catch (InterruptedException e) {
        logger.error(e.getMessage());
      }
    }

  }
}
