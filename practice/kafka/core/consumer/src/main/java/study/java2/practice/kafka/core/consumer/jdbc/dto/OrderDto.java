package study.java2.practice.kafka.core.consumer.jdbc.dto;

import java.time.LocalDateTime;


public record OrderDto(String orderId, String shopId, String menuName,
                       String userName, String phoneNumber,
                       String address, LocalDateTime orderTime) {

  public static OrderDto of(String orderId, String shopId, String menuName,
                            String userName, String phoneNumber,
                            String address, LocalDateTime orderTime) {
    return new OrderDto(orderId, shopId, menuName, userName, phoneNumber, address, orderTime);
  }
}
