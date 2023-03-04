package study.java2.kafka.core.consumer.practice;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.List;
import java.util.Properties;


public class BaseConsumerMain {
  private static final String TOPIC_NAME = "file-topic";
  private static final String HOST_NAME = "localhost:9093";
  private static final String GROUP_ID = "file-group";

  public static void main(String[] args) {
    BaseConsumer<String, String> baseConsumer = BaseConsumer.of(getProperties(), List.of(TOPIC_NAME));
    baseConsumer.start(100, true);
  }

  private static Properties getProperties() {
    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST_NAME);
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
    properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    return properties;
  }

}
