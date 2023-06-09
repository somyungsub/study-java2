package study.java2.practice.kafka.core.consumer.ex2;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import study.java2.practice.kafka.core.consumer.ex2.model.OrderModel;
import study.java2.practice.kafka.core.consumer.practice.BaseConsumer;

import java.util.List;
import java.util.Properties;

public class OrderConsumerMain {
  private static final String TOPIC_NAME = "order-topic";
  private static final String HOST_NAME = "localhost:9092";
  private static final String GROUP_ID = "order-group";
  public static void main(String[] args) {
    BaseConsumer<String, OrderModel> baseConsumer = BaseConsumer.of(getProperties(), List.of(TOPIC_NAME));
    baseConsumer.start(100, true);
  }

  private static Properties getProperties() {
    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST_NAME);
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, OrderDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
    properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    return properties;
  }
}
