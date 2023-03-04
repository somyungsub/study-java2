package study.java2.kafka.core.consumer.jdbc;

import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import study.java2.kafka.core.consumer.jdbc.dto.OrderDto;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FileToDbConsumer<K extends Serializable, V extends Serializable> {
  private static final Logger log = LoggerFactory.getLogger(FileToDbConsumer.class);
  private final KafkaConsumer<K, V> kafkaConsumer;
  private final List<String> topics;
  private final OrderDbHandler orderDbHandler;

  private FileToDbConsumer(Properties consumerProps, List<String> topics, OrderDbHandler orderDbHandler) {
    KafkaConsumer<K, V> kafkaConsumer = new KafkaConsumer<>(consumerProps);
    this.kafkaConsumer = kafkaConsumer;
    this.topics = topics;
    this.orderDbHandler = orderDbHandler;
  }

  public static <K extends Serializable, V extends Serializable> FileToDbConsumer<K, V> of(Properties consumerProps, List<String> topics, OrderDbHandler orderDbHandler) {
    FileToDbConsumer<K, V> fileToDbConsumer = new FileToDbConsumer<>(consumerProps, topics, orderDbHandler);
    return fileToDbConsumer;
  }
  private void processRecord(ConsumerRecord<K, V> record) {
    OrderDto orderDto = makeOrderDto(record);
    this.orderDbHandler.insertOrder(orderDto);
  }

  public void start(long durationMillis, boolean isSync) {
    initConsumer();
    pollConsumes(durationMillis, isSync);
  }

  private void processRecords(ConsumerRecords<K, V> records){
    List<OrderDto> orders = makeOrders(records);
    this.orderDbHandler.insertOrders(orders);
  }

  private List<OrderDto> makeOrders(ConsumerRecords<K,V> records) {
    List<OrderDto> orders = new ArrayList<>();
//    records.forEach(record -> orders.add(makeOrderDTO(record)));
    for(ConsumerRecord<K, V> record : records) {
      OrderDto orderDto = makeOrderDto(record);
      orders.add(orderDto);
    }

    return orders;
  }


  private OrderDto makeOrderDto(ConsumerRecord<K, V> record) {
    String messageValue = (String)record.value();
    log.info("###### messageValue:" + messageValue);
    String[] tokens = messageValue.split(",");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    OrderDto orderDTO = new OrderDto(tokens[0], tokens[1], tokens[2], tokens[3],
      tokens[4], tokens[5], LocalDateTime.parse(tokens[6].trim(), formatter));
    return orderDTO;
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
    if (isSync) {
      pollCommitSync(durationMillis);
    } else {
      pollCommitAsync(durationMillis);
    }
  }

  private void pollCommitSync(long durationMillis) {
    try {
      while (true) {
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
    }catch(WakeupException e) {
      log.error("wakeup exception has been called");
    }catch(Exception e) {
      log.error(e.getMessage());
    }finally {
      log.info("##### commit sync before closing");
      kafkaConsumer.commitSync();
      log.info("finally consumer is closing");
      close();
    }
  }

  private void pollCommitAsync(long durationMillis) {
    try {
      while (true) {
        ConsumerRecords<K, V> consumerRecords = this.kafkaConsumer.poll(Duration.ofMillis(durationMillis));
        log.info("consumerRecords count:" + consumerRecords.count());
        if(consumerRecords.count() > 0) {
          try {
            processRecords(consumerRecords);
          } catch(Exception e) {
            log.error(e.getMessage());
          }
        }
        //commitAsync의 OffsetCommitCallback을 lambda 형식으로 변경
        this.kafkaConsumer.commitAsync((offsets, exception) -> {
          if (exception != null) {
            log.error("offsets {} is not completed, error:{}", offsets, exception.getMessage());
          }
        });
      }
    }catch(WakeupException e) {
      log.error("wakeup exception has been called");
    }catch(Exception e) {
      log.error(e.getMessage());
    }finally {
      log.info("##### commit sync before closing");
      kafkaConsumer.commitSync();
      log.info("finally consumer is closing");
      close();
    }
  }

  private void close() {
    this.kafkaConsumer.close();
    this.orderDbHandler.close();
  }

}

