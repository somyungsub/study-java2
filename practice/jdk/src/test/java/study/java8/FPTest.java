package study.java8;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import study.java14.record.model.Person;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FPTest {

  private static FP fp;

  @BeforeAll
  public static void setup() {
    fp = new FP();
  }


  @Test
  void test1() {
    List<List<String>> input = List.of(
      List.of("1", "2", "3"),
      List.of("a", "b", "c"),
      List.of("!", "@", "#")
    );

    List<String> result = fp.testFlat(input);
    System.out.println(result);

    assertArrayEquals(
      result.toArray(),
      List.of("1", "2", "3", "a", "b", "c", "!", "@", "#").toArray()
    );
  }

  @Test
  void test2() {
    List<List<Person>> input = List.of(
      List.of(Person.of("test", 10), Person.of("test2", 20)),
      List.of(Person.of("s1", 20), Person.of("s3", 20)),
      List.of(Person.of("s1", 23), Person.of("s3", 24), Person.of("a3", 100))
    );

    List<Person> result = fp.testFlat(input);
    System.out.println(result);

    assertArrayEquals(
      result.toArray(),
      List.of(
        Person.of("test", 10), Person.of("test2", 20),
        Person.of("s1", 20), Person.of("s3", 20),
        Person.of("s1", 23), Person.of("s3", 24), Person.of("a3", 100)
      ).toArray()
    );

  }

  @Test
  void test3_나이합() {
    List<List<Person>> input = List.of(
      List.of(Person.of("test", 10), Person.of("test2", 20)),
      List.of(Person.of("s1", 20), Person.of("s3", 20)),
      List.of(Person.of("s1", 23), Person.of("s3", 24), Person.of("a3", 100))
    );

    List<Person> result = fp.testFlat(input);
    int sum = result.stream().mapToInt(Person::age).sum();
    System.out.println(sum);

    assertEquals(10 + 20 + 20 + 20 + 23 + 24 + 100, sum);
  }

  @Test
  void test3_이름별_나이합() {
    List<List<Person>> input = List.of(
      List.of(Person.of("test", 10), Person.of("test2", 20)),
      List.of(Person.of("s1", 20), Person.of("s3", 20)),
      List.of(Person.of("s1", 23), Person.of("s3", 24), Person.of("a3", 100))
    );

    List<Person> result = fp.testFlat(input);
    List<Map<String, Integer>> sumAgeByName = result.stream()
      .collect(Collectors.groupingBy(Person::name))
      .entrySet().stream()
      .map(entry -> {
        Map<String, Integer> map = new HashMap<>();
        map.put(entry.getKey(), entry.getValue().stream().mapToInt(Person::age).sum());
        return map;
      }).toList();

    System.out.println(sumAgeByName);

    assertEquals(5, sumAgeByName.size());
    assertEquals(10, sumAgeByName.stream().filter(map -> map.containsKey("test")).findFirst().get().get("test"));
    assertEquals(20, sumAgeByName.stream().filter(map -> map.containsKey("test2")).findFirst().get().get("test2"));
    assertEquals(43, sumAgeByName.stream().filter(map -> map.containsKey("s1")).findFirst().get().get("s1"));
    assertEquals(44, sumAgeByName.stream().filter(map -> map.containsKey("s3")).findFirst().get().get("s3"));
    assertEquals(100, sumAgeByName.stream().filter(map -> map.containsKey("a3")).findFirst().get().get("a3"));

  }

}