package study.java2.practice.kafka.core.consumer.ex2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import study.java2.practice.kafka.core.consumer.ex2.model.OrderModel;

import java.io.IOException;

@Slf4j
public class OrderDeserializer implements Deserializer<OrderModel> {
  private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
  @Override
  public OrderModel deserialize(String topic, byte[] data) {
    try {
      return objectMapper.readValue(data, OrderModel.class);
    } catch (IOException e) {
      log.error("Object mapper deserialization error : {} ", e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
