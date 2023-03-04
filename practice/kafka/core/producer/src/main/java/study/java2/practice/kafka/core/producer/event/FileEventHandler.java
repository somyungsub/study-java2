package study.java2.practice.kafka.core.producer.event;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class FileEventHandler implements EventHandler {
  private static final Logger log = LoggerFactory.getLogger(FileEventHandler.class);
  private final KafkaProducer<String, String> producer;
  private final String topicName;
  private final boolean sync;
  public FileEventHandler(KafkaProducer producer, String topicName) {
    this(producer, topicName, false);
  }
  public FileEventHandler(KafkaProducer<String, String> producer, String topicName, boolean sync) {
    this.producer = producer;
    this.topicName = topicName;
    this.sync = sync;
  }

  @Override
  public void onMessage(MessageEvent messageEvent) throws InterruptedException, ExecutionException {
    ProducerRecord<String, String> record = new ProducerRecord<>(topicName, messageEvent.getKey(), messageEvent.getValue());
    if (this.sync) {
      RecordMetadata metadata = producer.send(record).get();
      log.info("==== record ======");
      log.info("partition : {}, offset : {}, timestamp : {}"
      , metadata.partition(), metadata.offset(), metadata.timestamp());
    } else {
      producer.send(record, (metadata, exception) -> {
        if (exception == null) {
          log.info("==== record ======");
          log.info("partition : {}, offset : {}, timestamp : {}"
            , metadata.partition(), metadata.offset(), metadata.timestamp());
        } else {
          log.error(exception.getMessage());
        }
      });
    }
  }
}
