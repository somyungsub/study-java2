package study.java2.practice.kafka.core.consumer.ex2.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderModel implements Serializable {
  public String orderId;
  public String shopId;
  public String menuName;
  public String userName;
  public String phoneNumber;
  public String address;
  public LocalDateTime orderTime;

  @Override
  public String toString() {
    return "OrderModel{" +
      "orderId='" + orderId + '\'' +
      ", shopId='" + shopId + '\'' +
      ", menuName='" + menuName + '\'' +
      ", userName='" + userName + '\'' +
      ", phoneNumber='" + phoneNumber + '\'' +
      ", address='" + address + '\'' +
      ", orderTime=" + orderTime +
      '}';
  }
}
