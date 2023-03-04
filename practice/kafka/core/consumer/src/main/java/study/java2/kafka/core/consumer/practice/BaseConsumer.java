package study.java2.kafka.core.consumer.practice;

import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class BaseConsumer<K extends Serializable, V extends Serializable> {
  public static final Logger log = LoggerFactory.getLogger(BaseConsumer.class);
  private final KafkaConsumer<K, V> kafkaConsumer;
  private final List<String> topics;

  private BaseConsumer(Properties consumerProps, List<String> topics) {
    KafkaConsumer<K, V> kafkaConsumer = new KafkaConsumer<>(consumerProps);
    this.kafkaConsumer = kafkaConsumer;
    this.topics = topics;
  }

  public static <K extends Serializable, V extends Serializable> BaseConsumer<K, V> of(Properties consumerProps, List<String> topics) {
    BaseConsumer<K, V> baseConsumer = new BaseConsumer<>(consumerProps, topics);
    return baseConsumer;
  }

  public void start(long durationMillis, boolean isSync) {
    initConsumer();
    pollConsumes(durationMillis, isSync);
    closeConsumer();
  }

  private void initConsumer() {
    this.kafkaConsumer.subscribe(this.topics);
    shutdownHookToRuntime();
  }

  private void shutdownHookToRuntime() {
    //main thread
    Thread mainThread = Thread.currentThread();

    //main thread 종료시 별도의 thread로 KafkaConsumer wakeup()메소드를 호출하게 함.
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      log.info(" main program starts to exit by calling wakeup");
      kafkaConsumer.wakeup();
      try {
        mainThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }));
  }

  private void pollConsumes(long durationMillis, boolean isSync) {
    try {
      while (true) {
        if (isSync) {
          pollCommitSync(durationMillis);
        } else {
          pollCommitAsync(durationMillis);
        }
      }
    } catch (WakeupException e) {
      log.error("wakeup exception has been called");
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      log.info("##### commit sync before closing");
      this.kafkaConsumer.commitSync();
      log.info("finally consumer is closing");
      closeConsumer();
    }
  }

  private void pollCommitSync(long durationMillis) {
    ConsumerRecords<K, V> consumerRecords = this.kafkaConsumer.poll(Duration.ofMillis(durationMillis));
    processRecords(consumerRecords);
    try {
      if (consumerRecords.count() > 0) {
        this.kafkaConsumer.commitSync();
        log.info("commit sync has been called");
      }
    } catch (CommitFailedException e) {
      log.error(e.getMessage());
    }
  }

  private void pollCommitAsync(long durationMillis) {
    ConsumerRecords<K, V> consumerRecords = this.kafkaConsumer.poll(Duration.ofMillis(durationMillis));
    processRecords(consumerRecords);
    this.kafkaConsumer.commitAsync((offsets, exception) -> {
      if (exception != null) {
        log.error("offsets {} is not completed, error:{}", offsets, exception.getMessage());
      }
    });
  }

  private void processRecords(ConsumerRecords<K, V> records) {
    records.forEach(record -> {
      log.info("record key:{},  partition:{}, record offset:{} record value:{}",
        record.key(), record.partition(), record.offset(), record.value());
    });
  }

  private void closeConsumer() {
    this.kafkaConsumer.close();
  }

}

