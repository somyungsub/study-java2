package study.java2.feature.record.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonTest {

  @Test
  void record_테스트() {
    Person person = new Person("person", 30);
    assertEquals(person.age(), 30);
    assertEquals(person.name(), "person");
  }

  @Test
  void record_테스트_exception() {
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      new Person("person", -1);
    });
    assertEquals(exception.getMessage(), "age cannot be negative");
  }

}