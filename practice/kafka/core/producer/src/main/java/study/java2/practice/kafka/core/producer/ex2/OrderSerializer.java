package study.java2.practice.kafka.core.producer.ex2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;
import study.java2.practice.kafka.core.producer.ex2.model.OrderModel;


@Slf4j
public class OrderSerializer implements Serializer<OrderModel> {
  private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
  @Override
  public byte[] serialize(String topic, OrderModel data) {
    try {
      return objectMapper.writeValueAsBytes(data);
    } catch (JsonProcessingException e) {
      log.error("Json processing exception:" + e.getMessage());
      throw new RuntimeException(e);
    }
  }
}