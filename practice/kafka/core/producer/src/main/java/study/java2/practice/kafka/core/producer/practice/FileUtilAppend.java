package study.java2.practice.kafka.core.producer.practice;

import com.github.javafaker.Faker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileUtilAppend {
  private static final Logger log = LoggerFactory.getLogger(FileUtilAppend.class);
  private static final String FILE_PATH = "practice/kafka/core/producer/src/main/resources/pizza-append";
  private static final List<String> pizzaNames = List.of("Potato Pizza", "Cheese Pizza",
    "Cheese Garlic Pizza", "Super Supreme", "Peperoni");
  private static final List<String> pizzaShop = List.of("A001", "B001", "C001",
    "D001", "E001", "F001", "G001", "H001", "I001", "J001", "K001", "L001", "M001", "N001",
    "O001", "P001", "Q001");
  private static int orderSeq = 5000;
  private static Random random = new Random(2022);
  public static void main(String[] args) {
//    FileUtilAppend fileUtilAppend = new FileUtilAppend();
    // seed값을 고정하여 Random 객체와 Faker 객체를 생성.
    Faker faker = Faker.instance(random);

    // 100회 반복 수행.
    for(int i=0; i<100; i++) {
      //50 라인의 주문 문자열을 출력
      writeMessage(Paths.get(FILE_PATH).toAbsolutePath().toString(), faker);
      log.info("###### iteration: {} file write is done", i);
      try {
        //주어진 기간동안 sleep
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        log.error(e.getMessage());
      }
    }
  }

  private static void writeMessage(String filePath, Faker faker) {
    try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(filePath), StandardOpenOption.APPEND)) {
      for(int i=0; i < 50; i++) {
        Map<String, String> message = produceMessage(faker, orderSeq++);
        bufferedWriter.write(StringUtils.join(message.get("key"), ",", message.get("message"), System.lineSeparator()));
      }
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
  //인자로 피자명 또는 피자가게 List와 Random 객체를 입력 받아서 random한 피자명 또는 피자 가게 명을 반환.
  private static String getRandomValueFromList(List<String> list) {
    int size = list.size();
    int index = random.nextInt(size);
    return list.get(index);
  }

  //random한 피자 메시지를 생성하고, 피자가게 명을 key로 나머지 정보를 value로 하여 Hashmap을 생성하여 반환.
  private static Map<String, String> produceMessage(Faker faker, int id) {
    String shopId = getRandomValueFromList(pizzaShop);

    List<String> value = List.of(
      "ord" + id, shopId, getRandomValueFromList(pizzaNames),
      faker.name().fullName(), faker.phoneNumber().phoneNumber(),
      faker.address().streetAddress(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREAN))
    );

    Map<String, String> messageMap = new HashMap<>();
    messageMap.put("key", shopId);
    messageMap.put("message", StringUtils.join(value, ", "));

    return messageMap;
  }
}
